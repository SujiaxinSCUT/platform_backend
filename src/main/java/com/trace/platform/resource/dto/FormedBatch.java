package com.trace.platform.resource.dto;

import java.util.Map;

public class FormedBatch {
    private int productId;
    private String productName;
    private Map<String, Double> batchesNumMap;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Map<String, Double> getBatchesNumMap() {
        return batchesNumMap;
    }

    public void setBatchesNumMap(Map<String, Double> batchesNumMap) {
        this.batchesNumMap = batchesNumMap;
    }
}
