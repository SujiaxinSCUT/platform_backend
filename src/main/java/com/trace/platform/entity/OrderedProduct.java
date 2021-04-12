package com.trace.platform.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="ordered_product")
@NamedQuery(name="OrderedProduct.findAll", query="SELECT o FROM OrderedProduct o")
public class OrderedProduct implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private int id;
    @Column(nullable=false, name = "order_id")
    private int orderId;
    @Column(nullable=false, name = "product_id")
    private int productId;
    @Column(nullable=false, name = "quantity")
    private double quantity;
    @Column(nullable=false, name = "price")
    private double price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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
