package com.dailycodebuffer.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dailycodebuffer.commons.utils.GwRestClientUtil;
import com.dailycodebuffer.dto.OrderRequest;
import com.dailycodebuffer.dto.OrderResponse;
import com.dailycodebuffer.dto.PaymentRequest;
import com.dailycodebuffer.dto.PaymentResponse;
import com.dailycodebuffer.dto.ProductResponse;
import com.dailycodebuffer.exception.CustomException;
import com.dailycodebuffer.model.Order;
import com.dailycodebuffer.repository.OrderRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public long placeOrder(OrderRequest orderRequest) throws Exception {

		// Order Entity -> Save the data with Status Order Created
		// Product Service - Block Products (Reduce the Quantity)
		// Payment Service -> Payments -> Success-> COMPLETE, Else
		// CANCELLED

		log.info("Placing Order Request: {}", orderRequest);
		String url = "http://product-service-svc/product/reduceQuantity/" + orderRequest.getProductId() + "?quantity=" + orderRequest.getQuantity();
		log.info("Invoking Product service url: [{}] to reduce the product id: {}", url, orderRequest.getProductId());
		GwRestClientUtil.getInstance().jsonPut(url, Object.class);

		log.info("Creating Order with Status CREATED");
		Order order = Order.builder().amount(orderRequest.getTotalAmount()).orderStatus("CREATED")
				.productId(orderRequest.getProductId()).orderDate(Instant.now()).quantity(orderRequest.getQuantity())
				.build();

		order = orderRepository.save(order);

		String urlPayment = "http://payment-service-svc/payment";
		log.info("Calling Payment Service to complete the payment: [{}]", urlPayment);
		PaymentRequest paymentRequest = PaymentRequest.builder().orderId(order.getId())
				.paymentMode(orderRequest.getPaymentMode()).amount(orderRequest.getTotalAmount()).build();

		String orderStatus = null;
		try {
			GwRestClientUtil.getInstance().jsonPost("urlPayment", paymentRequest, Object.class);
			log.info("Payment done Successfully. Changing the Oder status to PLACED");
			orderStatus = "PLACED";
		} catch (Exception e) {
			log.error("Error occurred in payment. Changing order status to PAYMENT_FAILED {}", e);
			orderStatus = "PAYMENT_FAILED";
		}

		order.setOrderStatus(orderStatus);
		orderRepository.save(order);

		log.info("Order Places successfully with Order Id: {}", order.getId());
		return order.getId();
	}

	@Override
	public OrderResponse getOrderDetails(long orderId) throws Exception {
		log.info("Get order details for Order Id : {}", orderId);
		
		Order order = orderRepository.findById(orderId).orElseThrow(
				() -> new CustomException("Order not found for the order Id:" + orderId, "NOT_FOUND", 404));
		String urlProduct = "http://product-service-svc/product/" + order.getProductId();
		log.info("Invoking Product service [{}] to fetch the product for id: {}", urlProduct,  order.getProductId());
		ProductResponse productResponse = GwRestClientUtil.getInstance().jsonGet(urlProduct, ProductResponse.class);

		String urlPayment = "http://payment-service-svc/payment/order/" + order.getId();
		log.info("Getting payment information from the payment Service [{}]", urlPayment);
		PaymentResponse paymentResponse = GwRestClientUtil.getInstance().jsonGet(urlPayment, PaymentResponse.class);

		OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
				.productName(productResponse.getProductName()).productId(productResponse.getProductId()).build();

		OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
				.paymentId(paymentResponse.getPaymentId()).paymentStatus(paymentResponse.getStatus())
				.paymentDate(paymentResponse.getPaymentDate()).paymentMode(paymentResponse.getPaymentMode()).build();

		OrderResponse orderResponse = OrderResponse.builder().orderId(order.getId()).orderStatus(order.getOrderStatus())
				.amount(order.getAmount()).orderDate(order.getOrderDate()).productDetails(productDetails)
				.paymentDetails(paymentDetails).build();

		return orderResponse;
	}
}
