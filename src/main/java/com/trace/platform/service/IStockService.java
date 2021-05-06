package com.trace.platform.service;

import com.trace.platform.resource.dto.ProductDetailsResponse;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.dto.StockCreateRequest;
import com.trace.platform.service.dto.StockCreateResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IStockService {

    PageableResponse<ProductDetailsResponse> getProductInStockPageable(String account_name, Pageable pageable);

    List<ProductDetailsResponse> getAllProductsInStock(String account_id);

    StockCreateResponse addStock(StockCreateRequest request) throws Exception;
}
