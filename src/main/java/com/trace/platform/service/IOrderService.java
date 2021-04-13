package com.trace.platform.service;

import com.trace.platform.entity.Order;
import com.trace.platform.repository.dto.OrderQueryBody;
import com.trace.platform.resource.dto.OrderedProductResponse;
import com.trace.platform.resource.pojo.PageableResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {

    List<OrderedProductResponse> getAllOrderedProduct(int orderId);

    PageableResponse<Order> getOrderPageable(OrderQueryBody queryBody, Pageable pageable);
}
