package com.trace.platform.service.dto;

import java.util.Map;

public class OrderCreateResponse {

    public static final int SUCCESS = 0;
    public static final int COMMIT_FAILURE = 1;
    public static final int FAILED = 2;
    private Map<String, String> batches;
    private int result;

    public Map<String, String> getBatches() {
        return batches;
    }

    public void setBatches(Map<String, String> batches) {
        this.batches = batches;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
