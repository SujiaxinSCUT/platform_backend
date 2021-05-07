package com.trace.platform.resource.dto;

import com.trace.platform.entity.OrderedProduct;

import java.util.Date;
import java.util.List;

public class OrderCreateRequest {

    private String supplierName;
    private List<OrderedProduct> products;
    private String privateKey;

    public OrderCreateRequest() {
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public List<OrderedProduct> getProducts() {
        return products;
    }

    public void setProducts(List<OrderedProduct> products) {
        this.products = products;
    }
}

