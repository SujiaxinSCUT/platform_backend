package com.trace.platform.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.trace.platform.entity.Order;
import com.trace.platform.entity.OrderWithProduct;
import com.trace.platform.entity.Product;
import com.trace.platform.entity.Stock;
import com.trace.platform.repository.OrderRepository;
import com.trace.platform.repository.OrderedProductRepository;
import com.trace.platform.repository.StockRepository;
import com.trace.platform.repository.dto.OrderQueryBody;
import com.trace.platform.resource.dto.OrderedProductResponse;
import com.trace.platform.resource.dto.SelectedBatches;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IOrderService;
import com.trace.platform.service.dto.*;
import com.trace.platform.utils.DateUtil;
import org.hyperledger.fabric.sdk.FabricClient;
import org.hyperledger.fabric.sdk.util.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockRepository stockRepository;

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    @Override
    public List<OrderedProductResponse> getAllOrderedProduct(int orderId) {

        List<Map<String, Object>> maps = orderedProductRepository.findOrderedProductAll(orderId);
        List<OrderedProductResponse> responseList = new ArrayList<>();

        for (Map<String, Object> map : maps) {
            OrderedProductResponse orderedProductResponse = new OrderedProductResponse();
            orderedProductResponse.setPrice(Double.valueOf(map.get("price").toString()));
            orderedProductResponse.setQuantity(Double.valueOf(map.get("quantity").toString()));
            orderedProductResponse.setId(Integer.valueOf(map.get("id").toString()));
            orderedProductResponse.setName(map.get("name").toString());
            orderedProductResponse.setDescription(map.get("description").toString());
            orderedProductResponse.setUnit(map.get("unit").toString());
            responseList.add(orderedProductResponse);
        }

        return responseList;
    }

    @Override
    public PageableResponse<Order> getOrderPageable(OrderQueryBody queryBody, Pageable pageable) {

        Page<Order> page = orderRepository.findDynamicPageable(queryBody.getSupplierQueryName(),
                queryBody.getClientQueryName(),
                queryBody.getSupplierMatchName(),
                queryBody.getClientMatchName(),
                queryBody.getStartDate(),
                queryBody.getEndDate(),
                queryBody.getProductName(), pageable);

        PageableResponse<Order> response = new PageableResponse<>();
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setContents(page.getContent());
        return response;
    }

    @Override
    public void createOrder(OrderCreateRequest request) {
        FabricClient client = new FabricClient(request.getSenderId(),
                request.getClientKey(), request.getClientCrt(), request.getServerCrt());
        List<Future<FabricOrderCreateResponse>> resultList = new ArrayList<>();
        Order order = request.getOrder();
        order.setStatus(Order.Status.CHECKING);
        orderRepository.save(order);
        try {
            client.init();
        } catch (Exception e) {
            e.printStackTrace();
            order.setStatus(Order.Status.INVALID);
            orderRepository.save(order);
            return;
        }
            List<SelectedBatches> selectedBatchesList = request.getSelectedBatchesList();
                    for (SelectedBatches batches : selectedBatchesList) {
                        Map<String, Double> map = batches.getBatches();
                        Set<String> keySet = map.keySet();
                        Stock newStock = new Stock();
                        Double totalQuantity = 0.0;
                        String batchId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                        for (String key : keySet) {
                            JSONObject proTradeObject = new JSONObject();
                            proTradeObject.put("proId", String.valueOf(batches.getProduct().getId()));
                            proTradeObject.put("proName", batches.getProduct().getName());
                            proTradeObject.put("proUnit", batches.getProduct().getUnit());
                            proTradeObject.put("bathId", key);
                            proTradeObject.put("quantity", String.valueOf(map.get(key)));
                            proTradeObject.put("date", DateUtil.toNormalizeString(request.getOrder().getDate()));
                            proTradeObject.put("sign", batches.getProductSign());
                            String proTradeJson = JSONObject.toJSONString(proTradeObject);


                            JSONObject fundsTradeObject = new JSONObject();
                            fundsTradeObject.put("proId", String.valueOf(batches.getProduct().getId()));
                            fundsTradeObject.put("proName", batches.getProduct().getName());
                            fundsTradeObject.put("unitPrice", String.valueOf(batches.getPrice()));
                            fundsTradeObject.put("totalPrice", String.valueOf(batches.getPrice() * map.get(key)));
                            fundsTradeObject.put("date", DateUtil.toNormalizeString(request.getOrder().getDate()));
                            fundsTradeObject.put("sign", batches.getFundSign());
                            String fundsTradeJson = JSONObject.toJSONString(fundsTradeObject);


                            Responses responses = client.sendTrade(request.getRcvId(), key,
                                    proTradeJson, fundsTradeJson, batchId);
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                                order.setStatus(Order.Status.INVALID);
                                orderRepository.save(order);
                                return;
                            }
                            if (responses.getCode() == 1) {
                                responses = client.sendTrade(request.getRcvId(), key,
                                        proTradeJson, fundsTradeJson, batchId);
                                if (responses.getCode() == 0) {
                                    totalQuantity += map.get(key);
                                    Stock stock = stockRepository.findByBatchId(key);
                                    stock.setRestQuantity(stock.getRestQuantity() - map.get(key));
                                    stock.setStatus(Stock.FREE);
                                    stockRepository.save(stock);
                                } else {
                                    return;
                                }
                            } else {
                                totalQuantity += map.get(key);
                                Stock stock = stockRepository.findByBatchId(key);
                                stock.setRestQuantity(stock.getRestQuantity() - map.get(key));
                                stock.setStatus(Stock.FREE);
                                stockRepository.save(stock);
                            }
                        }
                        newStock.setQuantity(totalQuantity);
                        newStock.setRestQuantity(totalQuantity);
                        newStock.setDate(new Date());
                        newStock.setStatus(Stock.FREE);
                        newStock.setBatchId(batchId);
                        newStock.setProductId(batches.getProduct().getId());
                        newStock.setAccountId(request.getRcvId());
                        newStock.setPrice(batches.getPrice());
                        stockRepository.save(newStock);
                    }
                    order.setStatus(Order.Status.SUCCESS);
                    orderRepository.save(order);


    }

    @Override
    public TraceResult traceProduct(String ownerId, String proId, String batchId, String adminName, String clientKey, String clientCrt, String serverCrt) throws Exception {
        FabricClient client = new FabricClient(adminName, clientKey, clientCrt, serverCrt);
        client.init();
        Responses responses = client.traceProducts(ownerId, proId, batchId);

        String message = responses.getMessages();
        JSONObject jsonObject = JSONObject.parseObject(message);
        List<JSONObject> jsonNodes = (List<JSONObject>) jsonObject.get("node");
        List<JSONObject> jsonEdges = (List<JSONObject>) jsonObject.get("edges");

        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

//        SimpleDateFormat sdfSource = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

        for (JSONObject object : jsonNodes) {
            String type = object.getString("type");
            if (type.equalsIgnoreCase("products")) {
                ProductNode node = new ProductNode();
                node.setId(object.getInteger("id"));
                node.setOwner(object.getString("owner"));
                node.setOrder(false);
                JSONObject value = object.getJSONObject("value");
                node.setBatchId(value.getString("bathId"));
                node.setDate(DateUtil.strToDate(value.getString("date")));
                nodes.add(node);
            } else {
                OrderNode node = new OrderNode();
                node.setId(object.getInteger("id"));
                node.setOrder(true);
                JSONObject value = object.getJSONObject("value");
                node.setDate(DateUtil.strToDate(value.getString("date")));
                node.setBatchId(value.getString("bathId"));
                node.setClientName(value.getString("reci"));
                node.setSupplierName(value.getString("send"));
                node.setQuantity(value.getDouble("quantity"));
                nodes.add(node);
            }
        }
        for (JSONObject object : jsonEdges) {
            Edge edge = new Edge();
            edge.setFrom(object.getInteger("from"));
            edge.setTo(object.getInteger("to"));
            edges.add(edge);
        }

        TraceResult traceResult = new TraceResult();
        traceResult.setEdges(edges);
        traceResult.setNodes(nodes);
        return traceResult;
    }

    @Override
    public TraceResult traceProductBack(String ownerId, String proId, String batchId, String adminName, String clientKey, String clientCrt, String serverCrt) throws Exception {
        FabricClient client = new FabricClient(adminName, clientKey, clientCrt, serverCrt);
        client.init();
        Responses responses = client.traceProductsBack(ownerId, proId, batchId);

        String message = responses.getMessages();
        JSONObject jsonObject = JSONObject.parseObject(message);
        List<JSONObject> jsonNodes = (List<JSONObject>) jsonObject.get("node");
        List<JSONObject> jsonEdges = (List<JSONObject>) jsonObject.get("edges");

        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

//        SimpleDateFormat sdfSource = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

        for (JSONObject object : jsonNodes) {
            String type = object.getString("type");
            if (type.equalsIgnoreCase("products")) {
                ProductNode node = new ProductNode();
                node.setId(object.getInteger("id"));
                node.setOwner(object.getString("owner"));
                node.setOrder(false);
                JSONObject value = object.getJSONObject("value");
                node.setBatchId(value.getString("batchId"));
                node.setDate(DateUtil.strToDate(value.getString("date")));
                nodes.add(node);
            } else {
                OrderNode node = new OrderNode();
                node.setId(object.getInteger("id"));
                node.setOrder(true);
                JSONObject value = object.getJSONObject("value");
                node.setDate(DateUtil.strToDate(value.getString("date")));
                node.setBatchId(value.getString("batchId"));
                node.setClientName(value.getString("reci"));
                node.setSupplierName(value.getString("send"));
                node.setQuantity(value.getDouble("quantity"));
                nodes.add(node);
            }
        }
        for (JSONObject object : jsonEdges) {
            Edge edge = new Edge();
            edge.setFrom(object.getInteger("from"));
            edge.setTo(object.getInteger("to"));
            edges.add(edge);
        }

        TraceResult traceResult = new TraceResult();
        traceResult.setEdges(edges);
        traceResult.setNodes(nodes);
        return traceResult;
    }

    @Override
    public PageableResponse<OrderWithProduct> getOrderByProductId(int productId, Pageable pageable) throws ParseException {
        Page<Map<String, Object>> page = orderRepository.findByProductIdPageable(productId, pageable);
        List<Map<String, Object>> content = page.getContent();
        List<OrderWithProduct> newContent = new ArrayList<>();

        for (Map<String, Object> map : content) {
            if (map.get("client_name") == null) continue;
            OrderWithProduct orderWithProduct = new OrderWithProduct();
            orderWithProduct.setClientName((String) map.get("client_name"));
            orderWithProduct.setSupplierName((String) map.get("supplier_name"));
            orderWithProduct.setDate(DateUtil.strToDate((String) map.get("date")));
            orderWithProduct.setOrderId((Integer) map.get("order_id"));
            orderWithProduct.setPrice((Double) map.get("price"));
            orderWithProduct.setQuantity((Double) map.get("quantity"));

            newContent.add(orderWithProduct);
        }

        PageableResponse<OrderWithProduct> response = new PageableResponse<>();
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setContents(newContent);
        return response;
    }


    @Transactional
    public void stockAndOrderChange(Order order, String rcvId,
                                    List<SelectedBatches> selectedBatchesList, Map<String,String> newBatches) {
        for (SelectedBatches batches : selectedBatchesList) {
            Map<String, Double> batchesMap = batches.getBatches();
            Set<String> keySet = batchesMap.keySet();
            Stock newStock = new Stock();
            Double totalQuantity = 0.0;
            for (String key : keySet) {
                Stock stock = stockRepository.findByBatchId(key);
                stock.setRestQuantity(stock.getQuantity() - batchesMap.get(key));
                stock.setStatus(Stock.FREE);
                stockRepository.save(stock);
                totalQuantity += batchesMap.get(key);
            }
            newStock.setQuantity(totalQuantity);
            newStock.setRestQuantity(totalQuantity);
            newStock.setDate(new Date());
            newStock.setStatus(Stock.FREE);
            newStock.setBatchId(newBatches.get(String.valueOf(batches.getProduct().getId())));
            newStock.setProductId(batches.getProduct().getId());
            newStock.setAccountId(rcvId);
            newStock.setPrice(batches.getPrice());
            stockRepository.save(newStock);
        }
        order.setStatus(Order.Status.SUCCESS);
        orderRepository.save(order);
    }

    class ResultExecutor implements Runnable {

        List<Future<FabricOrderCreateResponse>> resultList;

        List<SelectedBatches> selectedBatchesList;

        Order order;

        String rcvId;


        public ResultExecutor(List<Future<FabricOrderCreateResponse>> resultList,
                              Order order, List<SelectedBatches> selectedBatchesList,
                              String rcvId) {
            this.resultList = resultList;
            this.order = order;
            this.selectedBatchesList = selectedBatchesList;
            this.rcvId = rcvId;
        }

        @Override
        public void run() {
            try {
                Set<Future<FabricOrderCreateResponse>> completeSet = new HashSet<>();
                while (completeSet.size() < resultList.size()) {
                    for (Future<FabricOrderCreateResponse> future : resultList) {
                        if (!completeSet.contains(future) && future.isDone()) {
                            completeSet.add(future);
                        }
                    }
                    Thread.sleep(2000);
                }
                Map<String, String> proBatches = new HashMap<>();
                for (Future<FabricOrderCreateResponse> future : resultList) {
                    FabricOrderCreateResponse response = future.get();
                    if (response.isSuccess()) {
                        proBatches.put(response.getProductId(), response.getBatchId());
                    } else {
                        //TODO
                        order.setStatus(Order.Status.INVALID);
                        orderRepository.save(order);
                        return;
                    }
                }
                stockAndOrderChange(order, rcvId, selectedBatchesList, proBatches);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
