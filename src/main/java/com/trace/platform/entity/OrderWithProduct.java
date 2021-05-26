package com.trace.platform.entity;

//import javax.persistence.Column;
//import javax.persistence.Entity;
import java.util.Date;


public class OrderWithProduct {

//    @Column(nullable=false, name = "order_id")
    private int orderId;
//    @Column(nullable=false, name = "client_name")
    private String clientName;
//    @Column(nullable=false, name = "supplier_name")
    private String supplierName;
//    @Column(nullable=false, name = "date")
    private Date date;
//    @Column(nullable=false, name = "quantity")
    private double quantity;
//    @Column(nullable=false, name = "price")
    private double price;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
