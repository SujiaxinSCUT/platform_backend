package com.trace.platform.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="product_material")
@NamedQuery(name="ProductMaterialRel.findAll", query="SELECT p FROM ProductMaterialRel p")
public class ProductMaterialRel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private int id;
    @Column(nullable=false, name = "product_name")
    private String productName;
    @Column(nullable=false, name = "product_batch_id")
    private String productBatchId;
    @Column(nullable=false, name = "material_batch_id")
    private String materialBatchId;
    @Column(nullable=false, name = "material_name")
    private String materialName;
    @Column(nullable=false, name = "product_quantity")
    private double productQuantity;
    @Column(nullable=false, name = "material_quantity")
    private double materialQuantity;
    @Column(nullable=false, name = "date")
    private Date date;
    @Column(nullable=false, name = "account_name")
    private String accountName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductBatchId() {
        return productBatchId;
    }

    public void setProductBatchId(String productBatchId) {
        this.productBatchId = productBatchId;
    }

    public String getMaterialBatchId() {
        return materialBatchId;
    }

    public void setMaterialBatchId(String materialBatchId) {
        this.materialBatchId = materialBatchId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public double getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(double productQuantity) {
        this.productQuantity = productQuantity;
    }

    public double getMaterialQuantity() {
        return materialQuantity;
    }

    public void setMaterialQuantity(double materialQuantity) {
        this.materialQuantity = materialQuantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
