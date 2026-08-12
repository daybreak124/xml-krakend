package arda.xmlproject.demo.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDto {

    private Long id;

    @JsonProperty("product_name")
    @NotBlank(message = "Ürün adı boş olamaz")
    private String productName;

    @NotBlank(message = "Kategori boş olamaz")
    private String category;

    @DecimalMin(value = "0.0", inclusive = false, message = "Fiyat 0dan büyük olmalı")
    private BigDecimal price;

    @JsonProperty("stock_quantity")
    private Integer stockQuantity;

    private String sku;

    private String brand;

    private String description;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}