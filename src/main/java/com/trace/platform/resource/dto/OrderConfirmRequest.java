package com.trace.platform.resource.dto;

import com.trace.platform.entity.Product;

import java.util.List;
import java.util.Map;

public class OrderConfirmRequest {

    private boolean valid;
    private int orderId;
    private List<SelectedBatches> selectedBatchesList;
    private String clientKey;
    private String clientCrt;

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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<SelectedBatches> getSelectedBatchesList() {
        return selectedBatchesList;
    }

    public void setSelectedBatchesList(List<SelectedBatches> selectedBatchesList) {
        this.selectedBatchesList = selectedBatchesList;
    }


}
