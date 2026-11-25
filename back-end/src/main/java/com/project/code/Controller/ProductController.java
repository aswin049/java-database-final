package com.project.code.Controller;
import com.project.code.Service.*;
import com.project.code.Repo.*;
import com.project.code.Model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepository;

    @PostMapping
    public Map<String, String> addProduct(
            @RequestBody Product product) {

        Map<String, String> response = new HashMap<>();

        try {

            if (!serviceClass.validateProduct(product)) {

                response.put("message",
                        "Product already exists");

            } else {

                productRepository.save(product);

                response.put("message",
                        "Product added successfully");
            }

        } catch (DataIntegrityViolationException e) {

            response.put("message",
                    "Product SKU already exists");

        } catch (Exception e) {

            response.put("message",
                    "Unable to add product");
        }

        return response;
    }

    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        Product product = productRepository.findByid(id);

        response.put("products", product);

        return response;
    }

    @PutMapping
    public Map<String, String> updateProduct(
            @RequestBody Product product) {

        Map<String, String> response = new HashMap<>();

        try {

            if (!serviceClass.ValidateProductId(product.getId())) {
                response.put("message", "Product not found");
                return response;
            }

            productRepository.save(product);

            response.put("message",
                    "Product updated successfully");

        } catch (Exception e) {

            response.put("message",
                    "Unable to update product");
        }

        return response;
    }

    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(
            @PathVariable String name,
            @PathVariable String category) {

        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if ("null".equalsIgnoreCase(name)
                && "null".equalsIgnoreCase(category)) {

            products = productRepository.findAll();

        } else if ("null".equalsIgnoreCase(name)) {

            products = productRepository.findByCategory(category);

        } else if ("null".equalsIgnoreCase(category)) {

            products = productRepository
                    .findProductBySubName(name);

        } else {

            products = productRepository
                    .findProductBySubNameAndCategory(
                            name, category);
        }

        response.put("products", products);

        return response;
    }

    @GetMapping
    public Map<String, Object> listProduct() {

        Map<String, Object> response = new HashMap<>();

        response.put("products", productRepository.findAll());

        return response;
    }

    @GetMapping("/filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(
            @PathVariable String category,
            @PathVariable Long storeid) {

        Map<String, Object> response = new HashMap<>();

        response.put("product",
                productRepository.findProductByCategory(
                        category, storeid));

        return response;
    }

    @Transactional
    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(
            @PathVariable Long id) {

        Map<String, String> response = new HashMap<>();

        if (!serviceClass.ValidateProductId(id)) {

            response.put("message",
                    "Product not present in database");

        } else {

            inventoryRepository.deleteByProductId(id);
            productRepository.deleteById(id);

            response.put("message",
                    "Product deleted successfully");
        }

        return response;
    }

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(
            @PathVariable String name) {

        Map<String, Object> response = new HashMap<>();

        response.put("products",
                productRepository.findProductBySubName(name));

        return response;
    }
}