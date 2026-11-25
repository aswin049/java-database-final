package com.project.code.Service;
import com.project.code.Repo.*;
import com.project.code.Model.*;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

        // Retrieve existing customer
        Customer customer = customerRepository.findByEmail(
                placeOrderRequest.getCustomerEmail());

        // Create customer if not found
        if (customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequest.getCustomerName());
            customer.setEmail(placeOrderRequest.getCustomerEmail());
            customer.setPhone(placeOrderRequest.getCustomerPhone());
            customer = customerRepository.save(customer);
        }

        // Retrieve store
        Store store = storeRepository.findById(
                placeOrderRequest.getStoreId())
                .orElseThrow(() ->
                        new RuntimeException("Store not found"));

        // Create order details
        OrderDetails orderDetails = new OrderDetails();

        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(
                placeOrderRequest.getTotalPrice());
        orderDetails.setDate(LocalDateTime.now());

        orderDetails = orderDetailsRepository.save(orderDetails);

        // Process purchased products
        for (var purchaseProduct : 
                placeOrderRequest.getPurchaseProduct()) {

            Product product = productRepository.findById(
                    purchaseProduct.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found"));

            Inventory inventory =
                    inventoryRepository.findByProductIdandStoreId(
                            product.getId(), store.getId());

            if (inventory == null) {
                throw new RuntimeException("Inventory not found");
            }

            int quantity = purchaseProduct.getQuantity();

            if (quantity <= 0) {
                throw new RuntimeException("Invalid quantity");
            }

            if (inventory.getStockLevel() < quantity) {
                throw new RuntimeException("Insufficient stock");
            }

            // Update inventory
            inventory.setStockLevel(
                    inventory.getStockLevel() - quantity);

            inventoryRepository.save(inventory);

            // Create order item
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(orderDetails);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());

            orderItemRepository.save(orderItem);
        }
    }
}