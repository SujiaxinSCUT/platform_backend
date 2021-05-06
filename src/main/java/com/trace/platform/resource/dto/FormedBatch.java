package com.trace.platform.resource.dto;

import java.util.Map;

public class FormedBatch {
    private String productName;
    private Map<String, Double> batchesNumMap;

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
