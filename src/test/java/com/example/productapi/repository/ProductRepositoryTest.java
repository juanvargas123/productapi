package com.example.productapi.repository;

import com.example.productapi.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void saveProduct_ShouldPersistProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));

        // Act
        Product savedProduct = productRepository.save(product);

        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals("Test Product", savedProduct.getName());
        assertEquals("Test Description", savedProduct.getDescription());
        assertEquals(new BigDecimal("99.99"), savedProduct.getPrice());
        assertNotNull(savedProduct.getCreatedAt());
    }

    @Test
    void findById_WithExistingId_ShouldReturnProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        Product savedProduct = productRepository.save(product);

        // Act
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getId(), foundProduct.get().getId());
        assertEquals("Test Product", foundProduct.get().getName());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // Act
        Optional<Product> foundProduct = productRepository.findById(999L);

        // Assert
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllProducts() {
        // Arrange
        productRepository.deleteAll(); // Ensure clean state

        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(new BigDecimal("99.99"));
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("199.99"));
        productRepository.save(product2);

        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertEquals(2, products.size());
    }

    @Test
    void findAll_WithPagination_ShouldReturnPageOfProducts() {
        // Arrange
        productRepository.deleteAll(); // Ensure clean state

        // Create 5 products
        for (int i = 1; i <= 5; i++) {
            Product product = new Product();
            product.setName("Product " + i);
            product.setDescription("Description " + i);
            product.setPrice(new BigDecimal(i * 100));
            productRepository.save(product);
        }

        // Act - Get first page with 2 products
        Pageable pageable = PageRequest.of(0, 2);
        Page<Product> productPage = productRepository.findAll(pageable);

        // Assert
        assertEquals(5, productPage.getTotalElements()); // Total 5 products
        assertEquals(3, productPage.getTotalPages()); // 3 pages (2 + 2 + 1)
        assertEquals(2, productPage.getContent().size()); // 2 products in first page
    }

    @Test
    void findAll_WithSorting_ShouldReturnSortedProducts() {
        // Arrange
        productRepository.deleteAll(); // Ensure clean state

        Product product1 = new Product();
        product1.setName("Product B");
        product1.setPrice(new BigDecimal("200.00"));
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Product A");
        product2.setPrice(new BigDecimal("100.00"));
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Product C");
        product3.setPrice(new BigDecimal("300.00"));
        productRepository.save(product3);

        // Act - Sort by name ascending
        Sort sortByName = Sort.by(Sort.Direction.ASC, "name");
        List<Product> productsByName = productRepository.findAll(sortByName);

        // Act - Sort by price descending
        Sort sortByPrice = Sort.by(Sort.Direction.DESC, "price");
        List<Product> productsByPrice = productRepository.findAll(sortByPrice);

        // Assert - Name sorting
        assertEquals(3, productsByName.size());
        assertEquals("Product A", productsByName.get(0).getName());
        assertEquals("Product B", productsByName.get(1).getName());
        assertEquals("Product C", productsByName.get(2).getName());

        // Assert - Price sorting
        assertEquals(3, productsByPrice.size());
        assertEquals(new BigDecimal("300.00"), productsByPrice.get(0).getPrice());
        assertEquals(new BigDecimal("200.00"), productsByPrice.get(1).getPrice());
        assertEquals(new BigDecimal("100.00"), productsByPrice.get(2).getPrice());
    }

    @Test
    void deleteById_ShouldRemoveProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        Product savedProduct = productRepository.save(product);

        // Act
        productRepository.deleteById(savedProduct.getId());
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());

        // Assert
        assertFalse(deletedProduct.isPresent());
    }
}
