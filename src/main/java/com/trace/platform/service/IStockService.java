package com.trace.platform.service;

import com.trace.platform.resource.dto.ProductDetailsResponse;
import com.trace.platform.resource.pojo.PageableResponse;
import org.springframework.data.domain.Pageable;

public interface IStockService {

    PageableResponse<ProductDetailsResponse> getProductInStock(int account_id, Pageable pageable);
}
