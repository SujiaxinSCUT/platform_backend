package com.trace.platform.resource.dto;

import java.util.Date;

public class OrderQueryRequest {

    private String productName;
    private Date startDate;
    private Date endDate;
    private String username;
    private boolean salesOrder;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(boolean salesOrder) {
        this.salesOrder = salesOrder;
    }
}
