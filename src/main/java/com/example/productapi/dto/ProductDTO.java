package com.example.productapi.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.productapi.config.BigDecimalDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product Data Transfer Object for creating and updating products")
public class ProductDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must be at least 3 characters")
    @Schema(description = "Name of the product", example = "Laptop")
    private String name;

    @Schema(description = "Description of the product", example = "High-performance laptop with 16GB RAM")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "Price of the product", example = "999.99")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal price;
}
