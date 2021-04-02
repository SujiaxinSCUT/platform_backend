package com.trace.platform.service.impl;

import com.trace.platform.repository.StockRepository;
import com.trace.platform.resource.dto.ProductDetailsResponse;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StockServiceImpl implements IStockService {

    @Autowired
    private StockRepository stockRepository;

    @Override
    public PageableResponse<ProductDetailsResponse> getProductInStock(int account_id, Pageable pageable) {
        Page<Map<String, Object>> page = stockRepository.findProductInStock(account_id, pageable);
        List<Map<String, Object>> content = page.getContent();
        List<ProductDetailsResponse> list = new ArrayList<>();
        for (Map<String, Object> map: content) {
            ProductDetailsResponse productDetailsResponse = new ProductDetailsResponse();
            productDetailsResponse.setId(Integer.valueOf(map.get("id").toString()));
            productDetailsResponse.setDescription((String) map.get("description"));
            productDetailsResponse.setName((String) map.get("name"));
            productDetailsResponse.setUnit((String) map.get("unit"));
            productDetailsResponse.setSum(Integer.valueOf(map.get("sum").toString()));
            list.add(productDetailsResponse);
        }

        PageableResponse<ProductDetailsResponse> response = new PageableResponse<>();
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setContents(list);
        return response;
    }
}
