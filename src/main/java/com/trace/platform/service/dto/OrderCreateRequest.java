package com.trace.platform.service.dto;

import com.trace.platform.entity.Order;
import com.trace.platform.resource.dto.SelectedBatches;

import java.util.List;

public class OrderCreateRequest {

    private String rcvId;
    private String senderId;
    private String clientKey;
    private String clientCrt;
    private String serverCrt;
    private Order order;
    private List<SelectedBatches> selectedBatchesList;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<SelectedBatches> getSelectedBatchesList() {
        return selectedBatchesList;
    }

    public void setSelectedBatchesList(List<SelectedBatches> selectedBatchesList) {
        this.selectedBatchesList = selectedBatchesList;
    }

    public String getRcvId() {
        return rcvId;
    }

    public void setRcvId(String rcvId) {
        this.rcvId = rcvId;
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

    public String getServerCrt() {
        return serverCrt;
    }

    public void setServerCrt(String serverCrt) {
        this.serverCrt = serverCrt;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
