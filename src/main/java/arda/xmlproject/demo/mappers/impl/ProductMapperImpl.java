package arda.xmlproject.demo.mappers.impl;

import arda.xmlproject.demo.mappers.Mapper;
import arda.xmlproject.demo.products.ProductDto;
import arda.xmlproject.demo.products.ProductEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapperImpl implements Mapper<ProductEntity, ProductDto> {

    private final ModelMapper modelMapper;

    public ProductMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto mapTo(ProductEntity productEntity) {
        return modelMapper.map(productEntity, ProductDto.class);
    }

    @Override
    public ProductEntity mapFrom(ProductDto ProductDto) {
        return modelMapper.map(ProductDto, ProductEntity.class);
    }
}
