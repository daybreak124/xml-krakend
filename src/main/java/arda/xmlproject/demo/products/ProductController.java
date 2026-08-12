package arda.xmlproject.demo.products;

import arda.xmlproject.demo.mappers.Mapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final Mapper<ProductEntity, ProductDto> productMapper;

    public ProductController(ProductService productService, Mapper<ProductEntity, ProductDto> productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PreAuthorize("hasAnyAuthority('admin', 'products_adm', 'products_post')")
    @PostMapping(path = "/products")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductEntity productEntity = productMapper.mapFrom(productDto);
        ProductEntity savedProductEntity = productService.save(productEntity);
        ProductDto savedProductDto = productMapper.mapTo(savedProductEntity);

        return new ResponseEntity<>(savedProductDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'products_adm', 'products', 'products_get')")
    @GetMapping(path = "/products")
    public List<ProductDto> listProducts() {
        List<ProductEntity> products = productService.findAll();
        return products.stream()
                .map(productMapper::mapTo)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('admin', 'products_adm', 'products', 'products_get')")
    @GetMapping(path = "/products/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable("id") Long id) {
        Optional<ProductEntity> foundProduct = productService.findOne(id);

        return foundProduct.map(productEntity -> {
            ProductDto productDto = productMapper.mapTo(productEntity);
            return new ResponseEntity<>(productDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('admin', 'products_adm', 'products_patch')")
    @PutMapping(path = "/products/{id}")
    public ResponseEntity<ProductDto> fullUpdateProduct(@PathVariable("id") Long id, @RequestBody ProductDto productDto) {
        if (!productService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        productDto.setId(id);
        ProductEntity productEntity = productMapper.mapFrom(productDto);
        ProductEntity savedProductEntity = productService.save(productEntity);

        return new ResponseEntity<>(productMapper.mapTo(savedProductEntity), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'products_adm', 'products_patch')")
    @PatchMapping(path = "/products/{id}")
    public ResponseEntity<ProductDto> partialUpdateProduct(@PathVariable("id") Long id, @RequestBody ProductDto productDto) {
        if (!productService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ProductEntity productEntity = productMapper.mapFrom(productDto);
        ProductEntity updatedProduct = productService.partialUpdate(id, productEntity);

        return new ResponseEntity<>(productMapper.mapTo(updatedProduct), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'products_adm', 'products_del')")
    @DeleteMapping(path = "/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        if (!productService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        productService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}