package com.trace.platform.resource;

import com.trace.platform.entity.Account;
import com.trace.platform.entity.Order;
import com.trace.platform.entity.OrderedProduct;
import com.trace.platform.entity.Stock;
import com.trace.platform.repository.*;
import com.trace.platform.repository.dto.OrderQueryBody;
import com.trace.platform.resource.dto.*;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity createOrder(@RequestBody OrderCreateRequest orderCreateRequest) {
        if (accountRepository.findByName(orderCreateRequest.getSupplierName()) == null) {
            return new ResponseEntity("不存在该用户", HttpStatus.NOT_FOUND);
        }

        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentUsername.equalsIgnoreCase(orderCreateRequest.getSupplierName())) {
            return new ResponseEntity("不允许与自己交易", HttpStatus.CONFLICT);
        }
        if (orderRepository.findOne(currentUsername, orderCreateRequest.getSupplierName(), orderCreateRequest.getDate()) != null) {
            return new ResponseEntity("已存在该订单", HttpStatus.CONFLICT);
        }
        Order order = new Order();
        order.setClientName(currentUsername);
        order.setSupplierName(orderCreateRequest.getSupplierName());
        order.setStatus(Order.Status.CONFIRMING);
        order.setDate(orderCreateRequest.getDate());

        Order savedOrder = orderRepository.save(order);
//        orderRepository.insert(currentUsername, orderCreateRequest.getSupplierName(), Order.Status.CONFIRMING, orderCreateRequest.getDate());
//        Order savedOrder = orderRepository.findOne(currentUsername, orderCreateRequest.getSupplierName(), orderCreateRequest.getDate());

        List<OrderedProduct> orderedProducts = orderCreateRequest.getProducts();
        for (OrderedProduct orderedProduct : orderedProducts) {
            if (productRepository.findById(orderedProduct.getProductId()) == null) {
                return new ResponseEntity("不存在该产品" + orderedProduct.getId(), HttpStatus.NOT_FOUND);
            }
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
    public ResponseEntity confirmOrder(OrderConfirmRequest request) {
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
            orderCreateRequest.setServerCrt(account.getCertificate());
            iOrderService.createOrder(orderCreateRequest);
        } else {
            order.setStatus(Order.Status.INVALID);
            orderRepository.save(order);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
