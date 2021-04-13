package com.trace.platform.resource;

import com.trace.platform.entity.Order;
import com.trace.platform.entity.OrderedProduct;
import com.trace.platform.repository.AccountRepository;
import com.trace.platform.repository.OrderRepository;
import com.trace.platform.repository.OrderedProductRepository;
import com.trace.platform.repository.ProductRepository;
import com.trace.platform.repository.dto.OrderQueryBody;
import com.trace.platform.resource.dto.OrderCreateRequest;
import com.trace.platform.resource.dto.OrderQueryRequest;
import com.trace.platform.resource.dto.OrderedProductResponse;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/trace/account/order")
public class OrderResource {

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

    @PostMapping
    public ResponseEntity createOrder(@RequestBody OrderCreateRequest orderCreateRequest) {
        if (accountRepository.findByName(orderCreateRequest.getSupplierName()) == null) {
            return new ResponseEntity("不存在该用户", HttpStatus.NOT_FOUND);
        }

        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        if (orderRepository.findOne(currentUsername, orderCreateRequest.getSupplierName(), orderCreateRequest.getDate()) != null) {
            return new ResponseEntity("已存在该订单", HttpStatus.CONFLICT);
        }
//        Order order = new Order();
//        order.setClientName(currentUsername);
//        order.setSupplierName(orderCreateRequest.getSupplierName());
//        order.setStatus(Order.Status.CONFIRMING);
//        order.setDate(orderCreateRequest.getDate());
//
//        Order savedOrder = orderRepository.save(order);
        orderRepository.insert(currentUsername, orderCreateRequest.getSupplierName(), Order.Status.CONFIRMING, orderCreateRequest.getDate());
        Order savedOrder = orderRepository.findOne(currentUsername, orderCreateRequest.getSupplierName(), orderCreateRequest.getDate());

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
                                                    OrderQueryRequest request) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        OrderQueryBody body = new OrderQueryBody();
        if (request.isSales_order()) {
            body.setClientMatchName(request.getUsername());
            body.setSupplierQueryName(currentUsername);
        } else {
            body.setSupplierMatchName(request.getUsername());
            body.setClientQueryName(currentUsername);
        }
        body.setStartDate(request.getStart_date());
        body.setEndDate(request.getEnd_date());
        body.setProductName(request.getProduct_name());

        Pageable pageable = PageRequest.of(page, size);
        PageableResponse<Order> response = iOrderService.getOrderPageable(body, pageable);
        return response;
    }
}
