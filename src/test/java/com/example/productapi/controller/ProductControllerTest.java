package com.example.productapi.controller;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.model.Product;
import com.example.productapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setCreatedAt(LocalDateTime.now());

        productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");
        productDTO.setPrice(new BigDecimal("99.99"));
    }

    @Test
    void createProduct_WithValidInput_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(99.99)));

        verify(productService, times(1)).createProduct(any(ProductDTO.class));
    }

    @Test
    void createProduct_WithInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ProductDTO invalidDTO = new ProductDTO();
        invalidDTO.setName(""); // Invalid: name is required and min length is 3
        invalidDTO.setPrice(new BigDecimal("0")); // Invalid: price must be greater than 0

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(ProductDTO.class));
    }

    @Test
    void getProduct_WithExistingId_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(99.99)));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void getProduct_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(99L)).thenThrow(new EntityNotFoundException("Product not found with id: 99"));

        // Act & Assert
        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found with id: 99")));

        verify(productService, times(1)).getProductById(99L);
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(product);
        Page<Product> productPage = new PageImpl<>(products);
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Test Product")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(productService, times(1)).getAllProducts(any(Pageable.class));
    }

    @Test
    void updateProduct_WithExistingIdAndValidInput_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(new BigDecimal("199.99"));
        updatedProduct.setCreatedAt(LocalDateTime.now());

        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("Updated Product");
        updateDTO.setDescription("Updated Description");
        updateDTO.setPrice(new BigDecimal("199.99"));

        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.price", is(199.99)));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductDTO.class));
    }

    @Test
    void updateProduct_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.updateProduct(eq(99L), any(ProductDTO.class)))
                .thenThrow(new EntityNotFoundException("Product not found with id: 99"));

        // Act & Assert
        mockMvc.perform(put("/products/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found with id: 99")));

        verify(productService, times(1)).updateProduct(eq(99L), any(ProductDTO.class));
    }

    @Test
    void deleteProduct_WithExistingId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void deleteProduct_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("Product not found with id: 99"))
                .when(productService).deleteProduct(99L);

        // Act & Assert
        mockMvc.perform(delete("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found with id: 99")));

        verify(productService, times(1)).deleteProduct(99L);
    }
}
