package com.trace.platform.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.starkbank.ellipticcurve.Ecdsa;
import com.starkbank.ellipticcurve.PrivateKey;
import com.starkbank.ellipticcurve.Signature;
import com.trace.platform.entity.Order;
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
import com.trace.platform.service.dto.FabricOrderCreateResponse;
import com.trace.platform.service.dto.OrderCreateRequest;
import com.trace.platform.service.dto.OrderCreateResponse;
import com.trace.platform.utils.DateUtil;
import org.hyperledger.fabric.sdk.FabricClient;
import org.hyperledger.fabric.sdk.util.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
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
        try {
            client.init();
            List<SelectedBatches> selectedBatchesList = request.getSelectedBatchesList();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String keyFileStr = null;
                    try {
                        keyFileStr = new String(Files.readAllBytes(Paths.get("D://Desktop//keys//Mm1//privateKey.pem")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (SelectedBatches batches : selectedBatchesList) {
                        Map<String, Double> map = batches.getBatches();
                        Set<String> keySet = map.keySet();
                        for (String key : keySet) {
                            JSONObject proTradeObject = new JSONObject();
                            proTradeObject.put("proId", batches.getProduct().getId());
                            proTradeObject.put("proName", batches.getProduct().getName());
                            proTradeObject.put("proUnit", batches.getProduct().getUnit());
                            proTradeObject.put("bathId", key);
                            proTradeObject.put("quantity", map.get(key));
                            proTradeObject.put("date", DateUtil.toNormalizeString(request.getOrder().getDate()));
                            proTradeObject.put("sign", batches.getProductSign());
                            String proTradeJson = JSONObject.toJSONString(proTradeObject);


                            JSONObject fundsTradeObject = new JSONObject();
                            fundsTradeObject.put("proId", batches.getProduct().getId());
                            fundsTradeObject.put("proName", batches.getProduct().getName());
                            fundsTradeObject.put("unitPrice", batches.getPrice());
                            fundsTradeObject.put("totalPrice", batches.getPrice() * map.get(key));
                            fundsTradeObject.put("date", DateUtil.toNormalizeString(request.getOrder().getDate()));
                            fundsTradeObject.put("sign", batches.getFundSign());
                            String fundsTradeJson = JSONObject.toJSONString(fundsTradeObject);

                            String batchId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                            Responses responses = client.sendTrade(request.getRcvId(), String.valueOf(request.getOrder().getId()),
                                    proTradeJson, fundsTradeJson, batchId);
                        }
                    }



                }
            };
            runnable.run();


//            for (SelectedBatches batches : selectedBatchesList) {
//                Map<String, Double> map = batches.getBatches();
//                Set<String> keySet = map.keySet();
//                for (String key : keySet) {
//                    OrderDetails orderDetails = new OrderDetails();
//                    orderDetails.setBatchId(key);
//                    orderDetails.setDate(request.getOrder().getDate());
//                    orderDetails.setOrderId(String.valueOf(request.getOrder().getId()));
//                    orderDetails.setPrice(batches.getPrice());
//                    orderDetails.setProduct(batches.getProduct());
//                    orderDetails.setRcvId(request.getRcvId());
//                    orderDetails.setProSign(batches.getProductSign());
//                    orderDetails.setFundSign(batches.getFundSign());
//                    orderDetails.setQuantity(map.get(key));
//
//                    TransactionSender sender = new TransactionSender(client, orderDetails);
//                    Future<FabricOrderCreateResponse> future = executor.submit(sender);
//                    resultList.add(future);
//                }
//            }
//            ResultExecutor resultExecutor = new ResultExecutor(resultList, request.getOrder(), selectedBatchesList, request.getRcvId());
//            executor.submit(resultExecutor);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                stock.setQuantity(stock.getQuantity() - batchesMap.get(key));
                stock.setStatus(Stock.FREE);
                stockRepository.save(stock);
                totalQuantity += batchesMap.get(key);
            }
            newStock.setQuantity(totalQuantity);
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

class TransactionSender implements Callable<FabricOrderCreateResponse> {

    FabricClient client;
    OrderDetails orderDetails;


    public TransactionSender(FabricClient client, OrderDetails orderDetails) {
        this.client = client;
        this.orderDetails = orderDetails;
    }

    @Override
    public FabricOrderCreateResponse call() throws Exception {

//        JSONObject proTradeObject = new JSONObject();
////        proTradeObject.put("proId", String.valueOf(this.orderDetails.getProduct().getId()));
//        proTradeObject.put("proId", "1");
////        proTradeObject.put("proName", this.orderDetails.getProduct().getName());
//        proTradeObject.put("proName", "c");
//        proTradeObject.put("proUnit", this.orderDetails.getProduct().getUnit());
//        proTradeObject.put("bathId", this.orderDetails.getBatchId());
////        proTradeObject.put("quantity", String.valueOf(this.orderDetails.getQuantity()));
//        proTradeObject.put("quantity", "100");
////        proTradeObject.put("date", DateUtil.toNormalizeString(this.orderDetails.getDate()));
//        proTradeObject.put("date", "2021-03-10 20:18");
//        proTradeObject.put("sign", this.orderDetails.getProSign());
//        String proTradeJson = JSONObject.toJSONString(proTradeObject);
//
//
//        JSONObject fundsTradeObject = new JSONObject();
////        fundsTradeObject.put("proId", String.valueOf(this.orderDetails.getProduct().getId()));
//        fundsTradeObject.put("proId", "1");
////        fundsTradeObject.put("proName", this.orderDetails.getProduct().getName());
//        fundsTradeObject.put("proName", "c");
////        fundsTradeObject.put("unitPrice", String.valueOf(this.orderDetails.getPrice()));
//        fundsTradeObject.put("unitPrice", "1000");
////        fundsTradeObject.put("totalPrice", String.valueOf(this.orderDetails.getPrice() * this.orderDetails.getQuantity()));
//        fundsTradeObject.put("totalPrice", "100000");
////        fundsTradeObject.put("date", DateUtil.toNormalizeString(this.orderDetails.getDate()));
//        fundsTradeObject.put("date", "2021-03-10 20:18");
//        fundsTradeObject.put("sign", this.orderDetails.getFundSign());
//        String fundsTradeJson = JSONObject.toJSONString(fundsTradeObject);

        String keyFileStr = new String(Files.readAllBytes(Paths.get("D://Desktop//keys//Mm1//privateKey.pem")));
        PrivateKey key = PrivateKey.fromPem(keyFileStr);
        JSONObject proTrade= new JSONObject();
        proTrade.put("send", "Mp1");
        proTrade.put("reci", "Mm1");
        proTrade.put("proId", "1");
        proTrade.put("proName", "c");
        proTrade.put("quantity", "100");
        proTrade.put("proUnit", "kg");
        proTrade.put("date", "2021-03-02 22:18".substring(0, 10));
        Signature signaturePro1 = Ecdsa.sign(JSONObject.toJSONString(proTrade), key);
        String proSign = new String(signaturePro1.toBase64().getBytes());

        JSONObject proTradeObject = new JSONObject();
        proTradeObject.put("proId", "1");
        proTradeObject.put("proName", "c");
        proTradeObject.put("proUnit", "kg");
        proTradeObject.put("bathId", "202104272230000000");
        proTradeObject.put("quantity", "100");
        proTradeObject.put("date", "2021-03-02 22:18");
        proTradeObject.put("sign", proSign);
        String proTradeJson = JSONObject.toJSONString(proTradeObject);

        JSONObject fundsTrade= new JSONObject();
        fundsTrade.put("send", "Mp1");
        fundsTrade.put("reci", "Mm1");
        fundsTrade.put("proId", "1");
        fundsTrade.put("proName", "c");
        fundsTrade.put("unitPrice", "1000");
        fundsTrade.put("totalPrice", "100000");
        fundsTrade.put("date", "2021-03-02 22:18".substring(0, 10));

        Signature signaturePro2 = Ecdsa.sign(JSONObject.toJSONString(fundsTrade), key);
        String fundsSign =new String(signaturePro2.toBase64().getBytes());

        JSONObject fundsTradeObject = new JSONObject();
//			fundsTradeObject.put("send", "Mp1");
//			fundsTradeObject.put("reci", "Mm1");
        fundsTradeObject.put("proId", "1");
        fundsTradeObject.put("proName", "c");
        fundsTradeObject.put("unitPrice", "1000");
        fundsTradeObject.put("totalPrice", "100000");
        fundsTradeObject.put("date", "2021-03-02 22:18");
        fundsTradeObject.put("sign", fundsSign);
        String fundsTradeJson = JSONObject.toJSONString(fundsTradeObject);

        String batchId = UUID.randomUUID().toString().replace("-","").toLowerCase();
        Responses responses = client.sendTrade(this.orderDetails.getRcvId(), "000012",
                proTradeJson, fundsTradeJson, "2021043019000000");
        FabricOrderCreateResponse response = new FabricOrderCreateResponse();
        response.setBatchId(batchId);
        response.setSuccess(responses.getCode() == 0);
        return response;
    }


}

class OrderDetails {
    String proSign;
    String fundSign;
    Product product;
    String batchId;
    Double quantity;
    Date date;
    Double price;
    String rcvId;
    String orderId;

    public OrderDetails() {}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProSign() {
        return proSign;
    }

    public void setProSign(String proSign) {
        this.proSign = proSign;
    }

    public String getFundSign() {
        return fundSign;
    }

    public void setFundSign(String fundSign) {
        this.fundSign = fundSign;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getRcvId() {
        return rcvId;
    }

    public void setRcvId(String rcvId) {
        this.rcvId = rcvId;
    }
}