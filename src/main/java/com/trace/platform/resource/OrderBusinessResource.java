package com.trace.platform.resource;

import com.alibaba.fastjson.JSONObject;
import com.starkbank.ellipticcurve.Ecdsa;
import com.starkbank.ellipticcurve.PrivateKey;
import com.starkbank.ellipticcurve.PublicKey;
import com.starkbank.ellipticcurve.Signature;
import com.trace.platform.entity.*;
import com.trace.platform.repository.*;
import com.trace.platform.repository.dto.OrderQueryBody;
import com.trace.platform.resource.dto.*;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IOrderService;
import com.trace.platform.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/trace/business/order")
public class OrderBusinessResource {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderedProductRepository orderedProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private StockRepository stockRepository;

    /*
    *  创建待处理订单
    * */
    @PostMapping
    public ResponseEntity createOrder(@RequestBody OrderCreateRequest orderCreateRequest) {
        if (accountRepository.findByName(orderCreateRequest.getSupplierName()) == null) {
            return new ResponseEntity("不存在该用户", HttpStatus.NOT_FOUND);
        }

        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Date now = new Date();

        if (currentUsername.equalsIgnoreCase(orderCreateRequest.getSupplierName())) {
            return new ResponseEntity("不允许与自己交易", HttpStatus.CONFLICT);
        }
        if (orderRepository.findOne(currentUsername, orderCreateRequest.getSupplierName(), now) != null) {
            return new ResponseEntity("已存在该订单", HttpStatus.CONFLICT);
        }

        List<OrderedProduct> orderedProducts = orderCreateRequest.getProducts();
        for (OrderedProduct orderedProduct : orderedProducts) {
            Product product = productRepository.findById(orderedProduct.getProductId()).get();
            if (product == null) {
                return new ResponseEntity("不存在该产品" + orderedProduct.getId(), HttpStatus.NOT_FOUND);
            }
            PrivateKey key = null;
            try {
                key = PrivateKey.fromPem(orderCreateRequest.getPrivateKey());
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity("私钥有误", HttpStatus.CONFLICT);
            }
            JSONObject proTrade= new JSONObject();

            proTrade.put("send", orderCreateRequest.getSupplierName());
            proTrade.put("reci", currentUsername);
            proTrade.put("proId", String.valueOf(orderedProduct.getProductId()));
            proTrade.put("proName", product.getName());
            proTrade.put("quantity", String.valueOf(orderedProduct.getQuantity()));
            proTrade.put("proUnit", product.getUnit());
            proTrade.put("date", DateUtil.toNormalizeString(now).substring(0, 10));
            Signature signaturePro1 = Ecdsa.sign(JSONObject.toJSONString(proTrade), key);
            String proSign = new String(signaturePro1.toBase64().getBytes());

            Account account = accountRepository.findByName(currentUsername);
            PublicKey publicKey = null;
            try {
                publicKey = PublicKey.fromPem(new String(Files.readAllBytes(Paths.get(account.getPubKey()))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!Ecdsa.verify(JSONObject.toJSONString(proTrade), signaturePro1, publicKey)) {
                return new ResponseEntity("签名有误，请检查密钥是否正确", HttpStatus.CONFLICT);
            }

            JSONObject fundsTrade= new JSONObject();
            fundsTrade.put("send", orderCreateRequest.getSupplierName());
            fundsTrade.put("reci", currentUsername);
            fundsTrade.put("proId", String.valueOf(orderedProduct.getProductId()));
            fundsTrade.put("proName", product.getName());
            fundsTrade.put("unitPrice", String.valueOf(orderedProduct.getPrice()));
            fundsTrade.put("totalPrice", String.valueOf(orderedProduct.getQuantity() * orderedProduct.getPrice()));
            fundsTrade.put("date", DateUtil.toNormalizeString(now).substring(0, 10));

            Signature signaturePro2 = Ecdsa.sign(JSONObject.toJSONString(fundsTrade), key);
            String fundsSign = new String(signaturePro2.toBase64().getBytes());

            orderedProduct.setFundSign(fundsSign);
            orderedProduct.setProductSign(proSign);
        }
        Order order = new Order();
        order.setClientName(currentUsername);
        order.setSupplierName(orderCreateRequest.getSupplierName());
        order.setStatus(Order.Status.CONFIRMING);
        order.setDate(now);

        Order savedOrder = orderRepository.save(order);

        for (OrderedProduct orderedProduct : orderedProducts) {
            orderedProduct.setOrderId(savedOrder.getId());
        }
        orderedProductRepository.saveAll(orderedProducts);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/confirming/pageable/{page}/{size}")
    public PageableResponse<Order> getConfirmingOrderPageable(@PathVariable("page")int page, @PathVariable("size") int size) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findBySupplierIdAndStatus(currentUsername, Order.Status.CONFIRMING, pageable);

        PageableResponse<Order> response = new PageableResponse<>();
        response.setPage(orders.getNumber());
        response.setSize(orders.getSize());
        response.setTotalElements(orders.getTotalElements());
        response.setTotalPages(orders.getTotalPages());
        response.setContents(orders.getContent());

        return response;
    }

    @GetMapping("/order_id/{order_id}")
    public List<OrderedProductResponse> getOrderedProductAll(@PathVariable("order_id")int orderId) {
        List<OrderedProductResponse> orderedProductResponses = iOrderService.getAllOrderedProduct(orderId);

        return orderedProductResponses;
    }

    @PostMapping("/pageable/{page}/{size}")
    public PageableResponse<Order> getOrderPageable(@PathVariable("page")int page, @PathVariable("size")int size,
                                                    @Param("sales_order")boolean salesOrder, @Param("username")String username,
                                                    @Param("start_date") Date startDate, @Param("end_date")Date endDate,
                                                    @Param("product_name")String productName) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        OrderQueryRequest request = new OrderQueryRequest();
        request.setUsername(username);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setProductName(productName);
        request.setSalesOrder(salesOrder);
        OrderQueryBody body = new OrderQueryBody();
        System.out.println(request.isSalesOrder());
        if (request.isSalesOrder()) {
            body.setClientMatchName(request.getUsername());
            body.setSupplierQueryName(currentUsername);
        } else {
            body.setSupplierMatchName(request.getUsername());
            body.setClientQueryName(currentUsername);
        }
        body.setStartDate(request.getStartDate());
        body.setEndDate(request.getEndDate());
        body.setProductName(request.getProductName());

        Pageable pageable = PageRequest.of(page, size);
        PageableResponse<Order> response = iOrderService.getOrderPageable(body, pageable);
        return response;
    }

    @PostMapping("/confirm")
    public ResponseEntity confirmOrder(@RequestBody OrderConfirmRequest request) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByName(currentUsername);
        Order order = orderRepository.findById(request.getOrderId()).get();
        if (!order.getSupplierName().equalsIgnoreCase(currentUsername)) {
            return new ResponseEntity("无权限操作此订单", HttpStatus.FORBIDDEN);
        }
        if (request.isValid()) {
            List<SelectedBatches> batchesList = request.getSelectedBatchesList();
            for (SelectedBatches batches : batchesList) {
                Map<String, Double> map = batches.getBatches();
                Set<String> key = map.keySet();
                for (String batchId : key) {
                    Stock stock = stockRepository.findByBatchId(batchId);
                    if (stock == null || stock.getBatchId() == null) {
                        return new ResponseEntity("不存在该批次库存", HttpStatus.NOT_FOUND);
                    }
                    if (stock.getStatus() == Stock.ON_TRANSACTION) {
                        return new ResponseEntity("所选批次处于交易状态中：" + batchId, HttpStatus.CONFLICT);
                    }
                    if (stock.getQuantity() < map.get(batchId)) {
                        return new ResponseEntity("所选批次数量不足：" + batchId, HttpStatus.CONFLICT);
                    }
                    //TODO
                }
                OrderedProduct orderedProduct = orderedProductRepository.findByProIdAndOrderId(batches.getProduct().getId(), request.getOrderId());
                batches.setFundSign(orderedProduct.getFundSign());
                batches.setProductSign(orderedProduct.getProductSign());
            }
            com.trace.platform.service.dto.OrderCreateRequest orderCreateRequest = new com.trace.platform.service.dto.OrderCreateRequest();
            orderCreateRequest.setSenderId(order.getSupplierName());
            orderCreateRequest.setRcvId(order.getClientName());
            orderCreateRequest.setSelectedBatchesList(batchesList);
            orderCreateRequest.setOrder(order);
            orderCreateRequest.setClientKey(request.getClientKey());
            orderCreateRequest.setClientCrt(request.getClientCrt());
            try {
                orderCreateRequest.setServerCrt(new String(Files.readAllBytes(Paths.get(account.getCertificate()))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            iOrderService.createOrder(orderCreateRequest);
        } else {
            order.setStatus(Order.Status.INVALID);
            orderRepository.save(order);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/personal")
    public PersonalOrderResponse getPersonalOrderDetail() {
        Pageable pageable = PageRequest.of(0, 5);
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Page<Order> orders = orderRepository.findByUsername(currentUsername, pageable);

        int confirmingOrderNum = orderRepository.findCountOfConfirmingOrder(currentUsername);
        Page<Order> confirmingOrders = orderRepository.findBySupplierIdAndStatus(currentUsername, Order.Status.CONFIRMING,
                PageRequest.of(0, confirmingOrderNum > 0 ? confirmingOrderNum : 1));

        int checkingOrderNum = orderRepository.findCountOfCheckingOrder(currentUsername);
        int invalidOrderNum = orderRepository.findCountOfInvalidOrder(currentUsername);

        PersonalOrderResponse response = new PersonalOrderResponse();
        response.setConfirmingOrders(confirmingOrders.getContent());
        response.setRecentOrders(orders.getContent());
        response.setCheckingOrderNum(checkingOrderNum);
        response.setInvalidOrderNum(invalidOrderNum);
        response.setConfirmingOrderNum(confirmingOrderNum);
        response.setTotalOrderNum(orders.getTotalElements());

        return response;
    }
}
