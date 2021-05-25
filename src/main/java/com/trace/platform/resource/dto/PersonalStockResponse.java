package com.trace.platform.resource.dto;

import com.trace.platform.entity.Stock;
import com.trace.platform.resource.pojo.PageableResponse;

import java.util.List;

public class PersonalStockResponse {

    private int totalBatchNum;
    private int transactionBatch;
    private int produceBatch;

    private PageableResponse<ProductDetailsResponse> productsResponse;
    private List<Stock> recentStock;


    public int getTotalBatchNum() {
        return totalBatchNum;
    }

    public void setTotalBatchNum(int totalBatchNum) {
        this.totalBatchNum = totalBatchNum;
    }

    public int getTransactionBatch() {
        return transactionBatch;
    }

    public void setTransactionBatch(int transactionBatch) {
        this.transactionBatch = transactionBatch;
    }

    public int getProduceBatch() {
        return produceBatch;
    }

    public void setProduceBatch(int produceBatch) {
        this.produceBatch = produceBatch;
    }

    public PageableResponse<ProductDetailsResponse> getProductsResponse() {
        return productsResponse;
    }

    public void setProductsResponse(PageableResponse<ProductDetailsResponse> productsResponse) {
        this.productsResponse = productsResponse;
    }

    public List<Stock> getRecentStock() {
        return recentStock;
    }

    public void setRecentStock(List<Stock> recentStock) {
        this.recentStock = recentStock;
    }
}
