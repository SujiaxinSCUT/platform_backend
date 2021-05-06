package com.trace.platform.resource.dto;

import com.trace.platform.entity.Product;

import java.util.Map;

public class SelectedBatches {
    Product product;
    Map<String, Double> batches;
    double price;
    String productSign;
    String fundSign;

    public String getProductSign() {
        return productSign;
    }

    public void setProductSign(String productSign) {
        this.productSign = productSign;
    }

    public String getFundSign() {
        return fundSign;
    }

    public void setFundSign(String fundSign) {
        this.fundSign = fundSign;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public Map<String, Double> getBatches() {
        return batches;
    }

    public void setBatches(Map<String, Double> batches) {
        this.batches = batches;
    }
}
