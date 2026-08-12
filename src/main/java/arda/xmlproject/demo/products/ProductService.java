package arda.xmlproject.demo.products;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    ProductEntity save(ProductEntity productEntity);
    List<ProductEntity> findAll();
    Optional<ProductEntity> findOne(Long id);
    boolean isExists(Long id);


    ProductEntity partialUpdate(Long id, ProductEntity productEntity);
    void delete(Long id);
}

