package com.trace.platform.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="fabric_order")
@NamedQuery(name="FabricOrder.findAll", query="SELECT f FROM FabricOrder f")
public class FabricOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private int id;
    @Column(nullable=false, name = "order_id")
    private int orderId;
    @Column(nullable=false, name = "product_id")
    private int productId;
    @Column(nullable=false, name = "batch_id")
    private String batchId;
    @Column(nullable=false, name = "fabric_order_id")
    private int fabricOrderId;
    @Column(nullable=false, name = "quantity")
    private double quantity;
    @Column(nullable=false, name = "price")
    private double price;
}
