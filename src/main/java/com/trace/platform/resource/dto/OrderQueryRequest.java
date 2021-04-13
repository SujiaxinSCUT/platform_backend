package com.trace.platform.resource.dto;

import java.util.Date;

public class OrderQueryRequest {

    private String product_name;
    private Date start_date;
    private Date end_date;
    private String username;
    private boolean sales_order;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSales_order() {
        return sales_order;
    }

    public void setSales_order(boolean sales_order) {
        this.sales_order = sales_order;
    }
}
