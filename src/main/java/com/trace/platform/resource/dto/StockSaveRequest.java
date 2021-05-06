package com.trace.platform.resource.dto;

import com.trace.platform.entity.Stock;

import java.util.List;
import java.util.Map;

public class StockSaveRequest {

    private Stock stock;
    private List<FormedBatch> batchList;
    private String clientKey;
    private String clientCrt;
    private String productName;
    private String unit;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public List<FormedBatch> getBatchList() {
        return batchList;
    }

    public void setBatchList(List<FormedBatch> batchList) {
        this.batchList = batchList;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientCrt() {
        return clientCrt;
    }

    public void setClientCrt(String clientCrt) {
        this.clientCrt = clientCrt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


}
