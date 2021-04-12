package com.trace.platform.resource.dto;

import com.trace.platform.entity.OrderedProduct;

import java.util.Date;
import java.util.List;

public class OrderCreateRequest {

    private int supplierId;
    private Date date;
    private List<OrderedProduct> products;

    public OrderCreateRequest() {
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
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

