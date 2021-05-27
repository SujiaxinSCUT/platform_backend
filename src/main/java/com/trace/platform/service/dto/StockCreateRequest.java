package com.trace.platform.service.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class StockCreateRequest {

    private String accountName;
    private int productId;
    private String productName;
    private double quantity;
    private String unit;
    private String batchId;
    private Date date;
    private Map<Integer, List<String>> form;
    private String clientKey;
    private String clientCrt;
    private String serverCrt;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getServerCrt() {
        return serverCrt;
    }

    public void setServerCrt(String serverCrt) {
        this.serverCrt = serverCrt;
    }

    public Map<Integer, List<String>> getForm() {
        return form;
    }

    public void setForm(Map<Integer, List<String>> form) {
        this.form = form;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientCrt() {
        return clientCrt;
    }

    public void setClientCrt(String clientCrt) {
        this.clientCrt = clientCrt;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
