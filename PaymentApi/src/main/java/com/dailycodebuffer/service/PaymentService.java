package com.dailycodebuffer.service;

import com.dailycodebuffer.dto.PaymentRequest;
import com.dailycodebuffer.dto.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
