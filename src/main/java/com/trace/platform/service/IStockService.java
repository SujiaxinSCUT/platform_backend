package com.trace.platform.service;

import com.trace.platform.resource.dto.ProductDetailsResponse;
import com.trace.platform.resource.pojo.PageableResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IStockService {

    PageableResponse<ProductDetailsResponse> getProductInStockPageable(int account_id, Pageable pageable);

    List<ProductDetailsResponse> getAllProductsInStock(int account_id);
}
