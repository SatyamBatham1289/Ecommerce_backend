package com.example.ecommerce.service;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // CREATE PRODUCT
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // GET ALL PRODUCTS
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // SEARCH PRODUCTS
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    // GET PRODUCTS BY CATEGORY
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    // GET PRODUCTS BY PRICE RANGE
    public List<Product> getProductsByPriceRange(
            Double min,
            Double max) {

        return productRepository.findByPriceBetween(min, max);
    }

    // SORT PRODUCTS BY PRICE - LOW TO HIGH
    public List<Product> getProductsByPriceAsc() {

        return productRepository.findAll(
                Sort.by(
                        Sort.Direction.ASC,
                        "price"
                )
        );
    }

    // SORT PRODUCTS BY PRICE - HIGH TO LOW
    public List<Product> getProductsByPriceDesc() {

        return productRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "price"
                )
        );
    }

    // PAGINATION
    public Page<Product> getProductsByPage(
            int page,
            int size) {

        return productRepository.findAll(
                PageRequest.of(page, size)
        );
    }

    // PAGINATION + SORTING
    public Page<Product> getProductsByPageAndSort(
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {

            sort = Sort.by(
                    Sort.Direction.DESC,
                    sortBy
            );

        } else {

            sort = Sort.by(
                    Sort.Direction.ASC,
                    sortBy
            );
        }

        return productRepository.findAll(
                PageRequest.of(
                        page,
                        size,
                        sort
                )
        );
    }

    // GET PRODUCT BY ID
    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );
    }

    // UPDATE PRODUCT
    public Product updateProduct(
            Long id,
            Product product) {

        Product existingProduct =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: " + id
                                )
                        );

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setCategory(product.getCategory());

        return productRepository.save(existingProduct);
    }

    // DELETE PRODUCT
    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + id
            );
        }

        productRepository.deleteById(id);
    }
}