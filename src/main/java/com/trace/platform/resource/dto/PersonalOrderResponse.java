package com.trace.platform.resource.dto;

import com.trace.platform.entity.Order;

import java.util.List;

public class PersonalOrderResponse {

    private long totalOrderNum;
    private long confirmingOrderNum;
    private int checkingOrderNum;
    private int invalidOrderNum;

    private List<Order> recentOrders;
    private List<Order> confirmingOrders;

    public long getTotalOrderNum() {
        return totalOrderNum;
    }

    public void setTotalOrderNum(long totalOrderNum) {
        this.totalOrderNum = totalOrderNum;
    }

    public long getConfirmingOrderNum() {
        return confirmingOrderNum;
    }

    public void setConfirmingOrderNum(long confirmingOrderNum) {
        this.confirmingOrderNum = confirmingOrderNum;
    }

    public int getCheckingOrderNum() {
        return checkingOrderNum;
    }

    public void setCheckingOrderNum(int checkingOrderNum) {
        this.checkingOrderNum = checkingOrderNum;
    }

    public int getInvalidOrderNum() {
        return invalidOrderNum;
    }

    public void setInvalidOrderNum(int invalidOrderNum) {
        this.invalidOrderNum = invalidOrderNum;
    }

    public List<Order> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<Order> recentOrders) {
        this.recentOrders = recentOrders;
    }

    public List<Order> getConfirmingOrders() {
        return confirmingOrders;
    }

    public void setConfirmingOrders(List<Order> confirmingOrders) {
        this.confirmingOrders = confirmingOrders;
    }
}
