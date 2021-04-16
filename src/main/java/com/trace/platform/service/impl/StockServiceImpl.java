package com.trace.platform.service.impl;

import com.trace.platform.repository.StockRepository;
import com.trace.platform.resource.dto.ProductDetailsResponse;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IStockService;
import org.apache.commons.lang.StringUtils;
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
    public PageableResponse<ProductDetailsResponse> getProductInStockPageable(int account_id, Pageable pageable) {
        Page<Map<String, Object>> page = stockRepository.findProductInStockPageable(account_id, pageable);
        List<Map<String, Object>> content = page.getContent();
        List<ProductDetailsResponse> list = new ArrayList<>();

        PageableResponse<ProductDetailsResponse> response = new PageableResponse<>();
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setContents(list);

        try {
            for (Map<String, Object> map: content) {
                ProductDetailsResponse productDetailsResponse = new ProductDetailsResponse();
                productDetailsResponse.setId(Integer.valueOf(map.get("id").toString()));
                productDetailsResponse.setDescription((String) map.get("description"));
                productDetailsResponse.setName((String) map.get("name"));
                productDetailsResponse.setUnit((String) map.get("unit"));
                productDetailsResponse.setSum(Double.valueOf(map.get("sum").toString()));
                list.add(productDetailsResponse);
            }
        } catch (Exception e) {
            return response;
        }


        return response;
    }

    @Override
    public List<ProductDetailsResponse> getAllProductsInStock(int account_id) {
        List<Map<String, Object>> mapList = stockRepository.findAllProductsInStock(account_id);
        List<ProductDetailsResponse> list = new ArrayList<>();

        try {
            for (Map<String, Object> map: mapList) {
                ProductDetailsResponse productDetailsResponse = new ProductDetailsResponse();
                productDetailsResponse.setId(Integer.valueOf(map.get("id").toString()));
                productDetailsResponse.setDescription((String) map.get("description"));
                productDetailsResponse.setName((String) map.get("name"));
                productDetailsResponse.setUnit((String) map.get("unit"));
                productDetailsResponse.setSum(Double.valueOf(map.get("sum").toString()));
                list.add(productDetailsResponse);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return list;
        }


        return list;
    }
}
