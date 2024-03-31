package com.dailycodebuffer.service;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dailycodebuffer.dto.ProductRequest;
import com.dailycodebuffer.dto.ProductResponse;
import com.dailycodebuffer.exception.ProductServiceCustomException;
import com.dailycodebuffer.model.Product;
import com.dailycodebuffer.model.Promocao;
import com.dailycodebuffer.repository.ProductRepository;
import com.dailycodebuffer.repository.PromocaoRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ProductService{

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PromocaoRepository promocaoRepository;

    public long addProduct(ProductRequest productRequest) {
       log.info("Adding Product..");

        Product product
                = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);

        log.info("Product Created");
        return product.getProductId();
    }

    public ProductResponse getProductById(long productId) {
        log.info("Get the product for productId: {}", productId);

        Product product
                = productRepository.findById(productId)
                .orElseThrow(
                        () -> new ProductServiceCustomException("Product with given id not found","PRODUCT_NOT_FOUND"));

        ProductResponse productResponse
                = new ProductResponse();

        copyProperties(product, productResponse);

        return productResponse;
    }

    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for Id: {}", quantity,productId);

        Product product
                = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException(
                        "Product with given Id not found",
                        "PRODUCT_NOT_FOUND"
                ));

        if(product.getQuantity() < quantity) {
        	log.error("Product does not have sufficient Quantity");
            throw new ProductServiceCustomException(
                    "Product does not have sufficient Quantity",
                    "INSUFFICIENT_QUANTITY"
            );
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product Quantity updated Successfully");
    }

	public List<Promocao> listAll() {
		return this.promocaoRepository.findAll();
	}

	public void addPromocao(Promocao promo) {
		this.promocaoRepository.save(promo);
	}
}
