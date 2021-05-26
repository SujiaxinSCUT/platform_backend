package com.trace.platform.resource.dto;

import com.trace.platform.entity.OrderWithProduct;
import com.trace.platform.resource.pojo.PageableResponse;

public class AvgPriceResponse {

    private double avgPrice;
    private PageableResponse<OrderWithProduct> pageableResponse;

    public double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public PageableResponse<OrderWithProduct> getPageableResponse() {
        return pageableResponse;
    }

    public void setPageableResponse(PageableResponse<OrderWithProduct> pageableResponse) {
        this.pageableResponse = pageableResponse;
    }
}
