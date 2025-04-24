package com.example.productapi.integration;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.model.Product;
import com.example.productapi.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Create a test product in the database
        testProduct = new Product();
        testProduct.setName("Integration Test Product");
        testProduct.setDescription("Integration Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct = productRepository.save(testProduct);
    }

    @AfterEach
    void tearDown() {
        // Clean up the database after each test
        productRepository.deleteAll();
    }

    @Test
    void createProduct_ShouldCreateAndReturnProduct() throws Exception {
        // Arrange
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("New Test Product");
        productDTO.setDescription("New Test Description");
        productDTO.setPrice(new BigDecimal("149.99"));

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Test Product")))
                .andExpect(jsonPath("$.description", is("New Test Description")))
                .andExpect(jsonPath("$.price", is(149.99)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void getProduct_WithExistingId_ShouldReturnProduct() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/{id}", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testProduct.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Integration Test Product")))
                .andExpect(jsonPath("$.description", is("Integration Test Description")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    void getProduct_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found with id: 999")));
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].id", is(testProduct.getId().intValue())))
                .andExpect(jsonPath("$.content[0].name", is("Integration Test Product")))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test
    void updateProduct_WithExistingId_ShouldUpdateAndReturnProduct() throws Exception {
        // Arrange
        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("Updated Integration Product");
        updateDTO.setDescription("Updated Integration Description");
        updateDTO.setPrice(new BigDecimal("199.99"));

        // Act & Assert
        mockMvc.perform(put("/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testProduct.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Updated Integration Product")))
                .andExpect(jsonPath("$.description", is("Updated Integration Description")))
                .andExpect(jsonPath("$.price", is(199.99)));
    }

    @Test
    void updateProduct_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("Updated Product");
        updateDTO.setDescription("Updated Description");
        updateDTO.setPrice(new BigDecimal("199.99"));

        // Act & Assert
        mockMvc.perform(put("/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found with id: 999")));
    }

    @Test
    void deleteProduct_WithExistingId_ShouldDeleteProduct() throws Exception {
        // Act & Assert - Delete the product
        mockMvc.perform(delete("/products/{id}", testProduct.getId()))
                .andExpect(status().isNoContent());

        // Verify the product is deleted
        mockMvc.perform(get("/products/{id}", testProduct.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found with id: 999")));
    }

    @Test
    void createProduct_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange - Invalid product with empty name and negative price
        ProductDTO invalidDTO = new ProductDTO();
        invalidDTO.setName(""); // Invalid: name is required and min length is 3
        invalidDTO.setDescription("Test Description");
        invalidDTO.setPrice(new BigDecimal("-10.00")); // Invalid: price must be greater than 0

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", notNullValue())); // Expect validation error for name
    }
}
