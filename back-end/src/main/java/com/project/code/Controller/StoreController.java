package com.project.code.Controller;
import com.project.code.Service.*;
import com.project.code.Repo.*;
import com.project.code.Model.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Map<String, String> addStore(
            @RequestBody Store store) {

        Map<String, String> response = new HashMap<>();

        Store savedStore = storeRepository.save(store);

        response.put("message",
                "Store created successfully with ID: "
                        + savedStore.getId());

        return response;
    }

    @GetMapping("/validate/{storeId}")
    public boolean validateStore(
            @PathVariable Long storeId) {

        Store store = storeRepository.findByid(storeId);

        return store != null;
    }

    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(
            @RequestBody PlaceOrderRequestDTO placeOrderRequest) {

        Map<String, String> response = new HashMap<>();

        try {

            orderService.saveOrder(placeOrderRequest);

            response.put("message",
                    "Order placed successfully");

        } catch (Exception e) {

            response.put("Error",
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Unable to place order");
        }

        return response;
    }
}