package com.dailycodebuffer.service;

import com.dailycodebuffer.dto.OrderRequest;
import com.dailycodebuffer.dto.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest) throws Exception;

    OrderResponse getOrderDetails(long orderId) throws Exception;
}
