package com.trace.platform.resource.dto;

public class TraceRequest {

    private String batchId;
    private String clientKey;
    private String clientCrt;
    private boolean isBack;

    public boolean isBack() {
        return isBack;
    }

    public void setBack(boolean back) {
        isBack = back;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
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
}
