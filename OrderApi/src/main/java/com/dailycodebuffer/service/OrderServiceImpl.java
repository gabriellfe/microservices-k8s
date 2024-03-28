package com.dailycodebuffer.service;

import java.time.Instant;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dailycodebuffer.commons.utils.GwTokenUtil;
import com.dailycodebuffer.dto.OrderRequest;
import com.dailycodebuffer.dto.OrderResponse;
import com.dailycodebuffer.dto.PaymentRequest;
import com.dailycodebuffer.dto.PaymentResponse;
import com.dailycodebuffer.dto.ProductResponse;
import com.dailycodebuffer.exception.CustomException;
import com.dailycodebuffer.model.Order;
import com.dailycodebuffer.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public long placeOrder(OrderRequest orderRequest) {

		// Order Entity -> Save the data with Status Order Created
		// Product Service - Block Products (Reduce the Quantity)
		// Payment Service -> Payments -> Success-> COMPLETE, Else
		// CANCELLED

		log.info("Placing Order Request: {}", orderRequest);
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("gw_token", GwTokenUtil.generateGwToken());
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		log.info("Invoking Product service to reduce the product id: {}", orderRequest.getProductId());
		restTemplate.exchange("http://product-service-svc/product/reduceQuantity/" + orderRequest.getProductId() + "?quantity" + orderRequest.getQuantity(), HttpMethod.PUT, entity, Object.class);

		log.info("Creating Order with Status CREATED");
		Order order = Order.builder().amount(orderRequest.getTotalAmount()).orderStatus("CREATED")
				.productId(orderRequest.getProductId()).orderDate(Instant.now()).quantity(orderRequest.getQuantity())
				.build();

		order = orderRepository.save(order);

		log.info("Calling Payment Service to complete the payment");
		PaymentRequest paymentRequest = PaymentRequest.builder().orderId(order.getId())
				.paymentMode(orderRequest.getPaymentMode()).amount(orderRequest.getTotalAmount()).build();

		String orderStatus = null;
		try {
			String reqBodyData = new ObjectMapper().writeValueAsString(paymentRequest);
			entity = new HttpEntity<>(reqBodyData, headers);
			restTemplate.exchange("http://payment-service-svc/payment", HttpMethod.POST, entity, Object.class);
			//			paymentService.doPayment(paymentRequest);
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
	public OrderResponse getOrderDetails(long orderId) {
		log.info("Get order details for Order Id : {}", orderId);
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("gw_token", GwTokenUtil.generateGwToken());
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		Order order = orderRepository.findById(orderId).orElseThrow(
				() -> new CustomException("Order not found for the order Id:" + orderId, "NOT_FOUND", 404));
		
		log.info("Invoking Product service to fetch the product for id: {}", order.getProductId());
		ProductResponse productResponse = restTemplate.exchange("http://product-service-svc/product/" + order.getProductId(), HttpMethod.GET, entity, ProductResponse.class).getBody();

		log.info("Getting payment information form the payment Service");
		PaymentResponse paymentResponse = restTemplate.exchange("http://payment-service-svc/payment/order/" + order.getId(), HttpMethod.GET, entity, PaymentResponse.class).getBody();

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
