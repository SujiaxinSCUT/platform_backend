package com.trace.platform.service.impl;

import com.trace.platform.entity.Order;
import com.trace.platform.repository.OrderRepository;
import com.trace.platform.repository.OrderedProductRepository;
import com.trace.platform.repository.dto.OrderQueryBody;
import com.trace.platform.resource.dto.OrderedProductResponse;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    @Autowired
    private OrderRepository orderRepository;

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
}
