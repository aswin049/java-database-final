package com.project.code.Controller;
import com.project.code.Service.*;
import com.project.code.Repo.*;
import com.project.code.Model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

    @PutMapping
    public Map<String, String> updateInventory(
            @RequestBody CombinedRequest request) {

        Map<String, String> response = new HashMap<>();

        try {
            Product product = request.getProduct();
            Inventory inventory = request.getInventory();

            if (!serviceClass.ValidateProductId(product.getId())) {
                response.put("message", "Product not found");
                return response;
            }

            Inventory existingInventory =
                    serviceClass.getInventoryId(inventory);

            if (existingInventory != null) {

                productRepository.save(product);

                existingInventory.setStockLevel(
                        inventory.getStockLevel());

                inventoryRepository.save(existingInventory);

                response.put("message",
                        "Successfully updated product");
            } else {
                response.put("message", "No data available");
            }

        } catch (DataIntegrityViolationException e) {
            response.put("message", "Data integrity violation");
        } catch (Exception e) {
            response.put("message", "Unable to update inventory");
        }

        return response;
    }

    @PostMapping
    public Map<String, String> saveInventory(
            @RequestBody Inventory inventory) {

        Map<String, String> response = new HashMap<>();

        try {
            if (!serviceClass.validateInventory(inventory)) {
                response.put("message", "Data already present");
            } else {
                inventoryRepository.save(inventory);
                response.put("message", "Data saved successfully");
            }

        } catch (DataIntegrityViolationException e) {
            response.put("message", "Data integrity violation");
        } catch (Exception e) {
            response.put("message", "Unable to save inventory");
        }

        return response;
    }

    @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts(
            @PathVariable Long storeid) {

        Map<String, Object> response = new HashMap<>();

        response.put("products",
                productRepository.findProductsByStoreId(storeid));

        return response;
    }

    @GetMapping("/filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(
            @PathVariable String category,
            @PathVariable String name,
            @PathVariable Long storeid) {

        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if ("null".equalsIgnoreCase(category)
                && "null".equalsIgnoreCase(name)) {

            products = productRepository
                    .findProductsByStoreId(storeid);

        } else if ("null".equalsIgnoreCase(category)) {

            products = productRepository
                    .findByNameLike(storeid, name);

        } else if ("null".equalsIgnoreCase(name)) {

            products = productRepository
                    .findByCategoryAndStoreId(storeid, category);

        } else {

            products = productRepository
                    .findByNameAndCategory(storeid, name, category);
        }

        response.put("product", products);
        return response;
    }

    @GetMapping("/search/{name}/{storeId}")
    public Map<String, Object> searchProduct(
            @PathVariable String name,
            @PathVariable Long storeId) {

        Map<String, Object> response = new HashMap<>();

        response.put("product",
                productRepository.findByNameLike(storeId, name));

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(
            @PathVariable Long id) {

        Map<String, String> response = new HashMap<>();

        if (!inventoryRepository.existsById(id)) {
            response.put("message", "Inventory not found");
        } else {
            inventoryRepository.deleteById(id);
            response.put("message",
                    "Product deleted successfully");
        }

        return response;
    }

    @GetMapping("/validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(
            @PathVariable int quantity,
            @PathVariable Long storeId,
            @PathVariable Long productId) {

        Inventory inventory =
                inventoryRepository.findByProductIdandStoreId(
                        productId, storeId);

        return quantity > 0
                && inventory != null
                && inventory.getStockLevel() >= quantity;
    }
}