package com.assignment.backend.services.impl;

import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignment.backend.data.entities.Category;
import com.assignment.backend.data.entities.Product;
import com.assignment.backend.data.entities.ProductImage;
import com.assignment.backend.data.repositories.ProductImageRepository;
import com.assignment.backend.data.repositories.ProductRepository;
import com.assignment.backend.dto.request.ProductCreateDto;
import com.assignment.backend.dto.response.ProductResponseDto;
import com.assignment.backend.exceptions.ResourceNotFoundException;
import com.assignment.backend.services.CategoryService;
import com.assignment.backend.services.ProductService;
import com.assignment.backend.utils.Utils;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private ModelMapper modelMapper;
    private CategoryService categoryService;
    private ProductImageRepository productImageRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper,
            CategoryService categoryService, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public List<ProductResponseDto> getAllProduct() {
        List<Product> lProducts = this.productRepository.findAll();
        List<ProductResponseDto> result = new ArrayList<>();
        for (Product product : lProducts) {
            ProductResponseDto newProduct = modelMapper.map(product, ProductResponseDto.class);
            // calculate rating of product
            newProduct.setRate(Utils.rate(product.getProductRates()));
            result.add(newProduct);
        }
        return result;
    }

    @Override
    public ProductResponseDto getProductById(int id) {
        Optional<Product> productOptional =  this.productRepository.findById(id);
        if(productOptional.isPresent()){
            Product product  = productOptional.get();
            return modelMapper.map(product, ProductResponseDto.class);
        }
        throw new ResourceNotFoundException(Utils.PRODUCT_NOT_FOUND);
    }

    @Override
    public List<Product> getProductByRate() {
        return null;
    }
    @Override
    public List<Product> getProductByCategory(int cateId) {
        return null;
    }
    
	@Override
    public List<ProductResponseDto> getProductOnTrading() {
        List<Product> lProducts = this.productRepository.findByStatus(Utils.PRODUCT_TRADING);
        if (lProducts.isEmpty()) {
            throw new ResourceNotFoundException(Utils.NO_PRODUCT);
        }

        List<ProductResponseDto> result = new ArrayList<>();
        for (Product product : lProducts) {
            ProductResponseDto productDto = modelMapper.map(product, ProductResponseDto.class);
            productDto.setRate(Utils.rate(product.getProductRates()));
            result.add(productDto);
        }
        return result;
    }

    private void saveImage(Product pro, String[] list) {
        for (String url : list) {
            ProductImage temp = new ProductImage();
            temp.setImgUrl(url);
            temp.setProduct(pro);
            this.productImageRepository.save(temp);
        }
    }

    @Override
    public ProductResponseDto createNewProduct(ProductCreateDto productCreateDto) {

        // get category
        Category category = this.categoryService.getCategoryById(productCreateDto.getCategoryId());

        Product newProduct = new Product();
        newProduct.setCategory(category);
        newProduct.setCreateDate();
        newProduct.setUpdateDate();
        newProduct.setName(productCreateDto.getName());
        newProduct.setDescription(productCreateDto.getDescription());
        newProduct.setAmount(productCreateDto.getAmount());
        newProduct.setPrice(productCreateDto.getPrice());
        newProduct.setStatus(Utils.PRODUCT_TRADING);
        Product saveProduct = this.productRepository.save(newProduct);

        // save image
        saveImage(saveProduct, productCreateDto.getImages());

        return modelMapper.map(getProductById(saveProduct.getProId()), ProductResponseDto.class);
    }

    @Override
    public ProductResponseDto deleteProduct(int proId) {
        Optional<Product> productOptional = this.productRepository.findById(proId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Boolean tempStatus = product.getStatus();
            product.setStatus(!tempStatus);
            product.setUpdateDate();

            product = this.productRepository.save(product);
            return modelMapper.map(product, ProductResponseDto.class);

        }

        throw new ResourceNotFoundException(Utils.PRODUCT_NOT_FOUND);
    }

    @Override
    public ProductResponseDto updateProduct(int id, ProductCreateDto productCreateDto) {
        Optional<Product> productOptional = this.productRepository.findById(id);
        Category category = this.categoryService.getCategoryById(productCreateDto.getCategoryId());

        if (productOptional.isPresent()) {
            Product pro = productOptional.get();
            pro.setUpdateDate();
            pro.setCategory(category);
            pro.setName(productCreateDto.getName());
            pro.setDescription(productCreateDto.getDescription());
            pro.setAmount(productCreateDto.getAmount());
            pro.setPrice(productCreateDto.getPrice());
            Product newProduct = this.productRepository.save(pro);

            // save change image
            saveImage(newProduct, productCreateDto.getImages());

            return modelMapper.map(newProduct, ProductResponseDto.class);

        }
        throw new ResourceNotFoundException(Utils.PRODUCT_NOT_FOUND);
    }
    
}
