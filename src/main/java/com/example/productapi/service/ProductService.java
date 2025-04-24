package com.example.productapi.service;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Product createProduct(ProductDTO productDTO);
    Product getProductById(Long id);
    Page<Product> getAllProducts(Pageable pageable);
    Product updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
}