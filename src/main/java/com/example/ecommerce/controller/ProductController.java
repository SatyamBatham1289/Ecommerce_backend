package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE PRODUCT
    @PostMapping
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());

        Product savedProduct =
                productService.createProduct(product);

        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice(),
                savedProduct.getQuantity(),
                savedProduct.getCategory()
        );
    }

    // SEARCH PRODUCTS BY NAME
    @GetMapping("/search")
    public List<ProductResponse> searchProducts(
            @RequestParam String name) {

        List<Product> products =
                productService.searchProducts(name);

        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getCategory()
                ))
                .toList();
    }

    // GET PRODUCTS BY CATEGORY
    @GetMapping("/category")
    public List<ProductResponse> getProductsByCategory(
            @RequestParam String category) {

        List<Product> products =
                productService.getProductsByCategory(category);

        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getCategory()
                ))
                .toList();
    }

    // GET PRODUCTS BY PRICE RANGE
    @GetMapping("/price")
    public List<ProductResponse> getProductsByPriceRange(
            @RequestParam Double min,
            @RequestParam Double max) {

        List<Product> products =
                productService.getProductsByPriceRange(min, max);

        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getCategory()
                ))
                .toList();
    }

    // GET ALL PRODUCTS
    @GetMapping
    public List<ProductResponse> getAllProducts() {

        List<Product> products =
                productService.getAllProducts();

        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getCategory()
                ))
                .toList();
    }

    // PAGINATION
    @GetMapping("/page")
    public Page<ProductResponse> getProductsByPage(
            @RequestParam int page,
            @RequestParam int size) {

        Page<Product> products =
                productService.getProductsByPage(
                        page,
                        size
                );

        return products.map(product -> new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory()
        ));
    }

    // PAGINATION + SORTING
    @GetMapping("/page-sort")
    public Page<ProductResponse> getProductsByPageAndSort(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String direction) {

        Page<Product> products =
                productService.getProductsByPageAndSort(
                        page,
                        size,
                        sortBy,
                        direction
                );

        return products.map(product -> new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory()
        ));
    }

    // SORT PRODUCTS BY PRICE - LOW TO HIGH
    @GetMapping("/sort/price-asc")
    public List<ProductResponse> getProductsByPriceAsc() {

        List<Product> products =
                productService.getProductsByPriceAsc();

        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getCategory()
                ))
                .toList();
    }

    // SORT PRODUCTS BY PRICE - HIGH TO LOW
    @GetMapping("/sort/price-desc")
    public List<ProductResponse> getProductsByPriceDesc() {

        List<Product> products =
                productService.getProductsByPriceDesc();

        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getCategory()
                ))
                .toList();
    }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable Long id) {

        Product product =
                productService.getProductById(id);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory()
        );
    }

    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());

        Product updatedProduct =
                productService.updateProduct(
                        id,
                        product
                );

        return new ProductResponse(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getPrice(),
                updatedProduct.getQuantity(),
                updatedProduct.getCategory()
        );
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public String deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return "Product deleted successfully";
    }
}