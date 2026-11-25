package com.project.code.Service;
import com.project.code.Repo.*;
import com.project.code.Model.*;

import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public ServiceClass(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository) {

        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public boolean validateInventory(Inventory inventory) {

        Inventory existingInventory =
                inventoryRepository.findByProductIdandStoreId(
                        inventory.getProduct().getId(),
                        inventory.getStore().getId());

        return existingInventory == null;
    }

    public boolean validateProduct(Product product) {

        Product existingProduct =
                productRepository.findByName(product.getName());

        return existingProduct == null;
    }

    public boolean ValidateProductId(long id) {

        Product product = productRepository.findByid(id);

        return product != null;
    }

    public Inventory getInventoryId(Inventory inventory) {

        return inventoryRepository.findByProductIdandStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId());
    }
}