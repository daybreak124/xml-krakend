package arda.xmlproject.demo.products;

import arda.xmlproject.demo.products.ProductEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductEntity save(ProductEntity productEntity) {
        return productRepository.save(productEntity);
    }

    @Override
    public List<ProductEntity> findAll() {
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductEntity> findOne(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public boolean isExists(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    @Transactional
    public ProductEntity partialUpdate(Long id, ProductEntity productEntity) {
        return productRepository.findById(id).map(existingProduct -> {

            Optional.ofNullable(productEntity.getProductName()).ifPresent(existingProduct::setProductName);
            Optional.ofNullable(productEntity.getCategory()).ifPresent(existingProduct::setCategory);
            Optional.ofNullable(productEntity.getPrice()).ifPresent(existingProduct::setPrice);
            Optional.ofNullable(productEntity.getStockQuantity()).ifPresent(existingProduct::setStockQuantity);
            Optional.ofNullable(productEntity.getSku()).ifPresent(existingProduct::setSku);
            Optional.ofNullable(productEntity.getBrand()).ifPresent(existingProduct::setBrand);
            Optional.ofNullable(productEntity.getDescription()).ifPresent(existingProduct::setDescription);
            Optional.ofNullable(productEntity.getIsActive()).ifPresent(existingProduct::setIsActive);
            Optional.ofNullable(productEntity.getCreatedAt()).ifPresent(existingProduct::setCreatedAt);

            return productRepository.save(existingProduct);

        }).orElseThrow(() -> new RuntimeException("Product does not exist"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}