package com.trace.platform.repository.dto;



import java.util.Date;

public class OrderQueryBody {

    private String supplierQueryName;
    private String clientQueryName;
    private String supplierMatchName;
    private String clientMatchName;
    private Date startDate;
    private Date endDate;
    private String productName;

    public OrderQueryBody() {
    }

    public String getSupplierQueryName() {
        return supplierQueryName;
    }

    public void setSupplierQueryName(String supplierQueryName) {
        this.supplierQueryName = supplierQueryName;
    }

    public String getClientQueryName() {
        return clientQueryName;
    }

    public void setClientQueryName(String clientQueryName) {
        this.clientQueryName = clientQueryName;
    }

    public String getSupplierMatchName() {
        return supplierMatchName;
    }

    public void setSupplierMatchName(String supplierMatchName) {
        this.supplierMatchName = supplierMatchName;
    }

    public String getClientMatchName() {
        return clientMatchName;
    }

    public void setClientMatchName(String clientMatchName) {
        this.clientMatchName = clientMatchName;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
