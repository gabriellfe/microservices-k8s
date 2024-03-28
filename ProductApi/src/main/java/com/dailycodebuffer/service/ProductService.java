package com.dailycodebuffer.service;

import com.dailycodebuffer.dto.ProductRequest;
import com.dailycodebuffer.dto.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
