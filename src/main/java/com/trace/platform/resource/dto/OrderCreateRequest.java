package com.trace.platform.resource.dto;

import com.trace.platform.entity.OrderedProduct;

import java.util.Date;
import java.util.List;

public class OrderCreateRequest {

    private String supplierName;
    private Date date;
    private List<OrderedProduct> products;

    public OrderCreateRequest() {
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<OrderedProduct> getProducts() {
        return products;
    }

    public void setProducts(List<OrderedProduct> products) {
        this.products = products;
    }
}

