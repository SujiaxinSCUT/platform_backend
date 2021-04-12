package com.trace.platform.resource;

import com.trace.platform.entity.Account;
import com.trace.platform.entity.Order;
import com.trace.platform.entity.OrderedProduct;
import com.trace.platform.repository.AccountRepository;
import com.trace.platform.repository.OrderRepository;
import com.trace.platform.repository.OrderedProductRepository;
import com.trace.platform.resource.dto.OrderCreateRequest;
import com.trace.platform.resource.pojo.PageableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity createOrder(OrderCreateRequest orderCreateRequest) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentUser = accountRepository.findByName(currentUsername);

        Order order = new Order();
        order.setDate(orderCreateRequest.getDate());
        order.setClientId(currentUser.getId());
        order.setSupplierId(orderCreateRequest.getSupplierId());
        order.setStatus(Order.Status.CONFIRMING);

        Order savedOrder = orderRepository.save(order);

        List<OrderedProduct> orderedProducts = orderCreateRequest.getProducts();
        for (OrderedProduct orderedProduct : orderedProducts) {
            orderedProduct.setOrderId(savedOrder.getId());
        }
        orderedProductRepository.saveAll(orderedProducts);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/confirming/pageable/{page}/{size}")
    public PageableResponse<Order> getConfirmingOrderPageable(@PathVariable("page")int page, @PathVariable("size") int size) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentUser = accountRepository.findByName(currentUsername);

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findBySupplierIdAndStatus(currentUser.getId(), Order.Status.CONFIRMING, pageable);

        PageableResponse<Order> response = new PageableResponse<>();
        response.setPage(orders.getNumber());
        response.setSize(orders.getSize());
        response.setTotalElements(orders.getTotalElements());
        response.setTotalPages(orders.getTotalPages());
        response.setContents(orders.getContent());

        return response;
    }
}
