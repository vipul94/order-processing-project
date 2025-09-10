package com.orderManagement.orderProcessing.Controller;

import com.orderManagement.orderProcessing.Entity.Order;
import com.orderManagement.orderProcessing.Service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1")
public class ControllerV1 {

    @Autowired
    private OrderService orderService;

    @PostMapping(path = "/add/order", produces = "application/json")
    public ResponseEntity<?> addOrder(@Valid @RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @GetMapping(path = "/fetch/orders/pagenation")
    public ResponseEntity<?> fetchOrders(@RequestParam String customerName,
                                         @RequestParam(defaultValue = "0") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size) {
        return orderService.fetchPagenatedOrder(customerName, page, size);
    }

    @GetMapping(path = "/fetch/order/status")
    public ResponseEntity<?> fetchOrderStatus(@RequestParam Long orderId) {
        return orderService.fetchOrderStatus(orderId);
    }

    @PatchMapping(path = "/update/order/status/sync")
    public ResponseEntity<?> syncOrderStatus(@RequestParam Long orderId,
                                             @RequestBody Map<String, String> body) {
        String status = body.get("orderStatus");
        return orderService.updateOrderStatusSync(orderId, status);
    }

    @PatchMapping(path = "/update/order/status/async")
    public ResponseEntity<?> asyncOrderStatus(@RequestParam Long orderId, @RequestBody Map<String, String> body) {
        body.put("OrderId", String.valueOf(orderId));
        return orderService.updateOrderStatusAsync(body);
    }
}
