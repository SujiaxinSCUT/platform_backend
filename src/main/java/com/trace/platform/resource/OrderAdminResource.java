package com.trace.platform.resource;

import com.trace.platform.entity.Order;
import com.trace.platform.repository.OrderRepository;
import com.trace.platform.resource.pojo.PageableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/trace/admin/order")
public class OrderAdminResource {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/username/{username}/pageable/{page}/{size}")
    public PageableResponse<Order> getOrdersByUsernamePageable(@PathVariable("username")String username,
                                                               @PathVariable("page")int page, @PathVariable("size")int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orderPage = orderRepository.findByUsername(username, pageable);

        PageableResponse<Order> response = new PageableResponse<>();
        response.setContents(orderPage.getContent());
        response.setTotalPages(orderPage.getTotalPages());
        response.setTotalElements(orderPage.getTotalElements());
        response.setSize(orderPage.getSize());
        response.setPage(orderPage.getNumber());

        return response;
    }
}
