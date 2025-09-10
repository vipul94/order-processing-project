package com.orderManagement.orderProcessing.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderManagement.orderProcessing.Constants.RedisConstants;
import com.orderManagement.orderProcessing.Constants.ResponseConstants;
import com.orderManagement.orderProcessing.Entity.Order;
import com.orderManagement.orderProcessing.Repository.DBRepository.OrderRepository;
import com.orderManagement.orderProcessing.ResponseEntity.Response;
import com.orderManagement.orderProcessing.Producer.SqsProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SqsProducerService  sqsProducerService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public ResponseEntity<?> createOrder(Order order) {

        try {
            order.setOrderStatus("PENDING");
            Order savedOrder = orderRepository.save(order);

            String cacheKey = RedisConstants.ORDER_CACHE_PREFIX + savedOrder.getOrderId();

            try {
                redisTemplate.opsForValue().set(cacheKey, savedOrder, 10, TimeUnit.MINUTES);
            } catch (RedisConnectionFailureException | RedisSystemException redisEx) {
                logger.warn("Redis unavailable while updating cache", redisEx);
            }
        } catch (DataAccessException e) {
            logger.error("Error while saving order: ", e);
            return Response.getResponse(ResponseConstants.ORDER_PROCESSING_FAILURE , HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Response.getResponse(ResponseConstants.ORDER_PROCESSING_SUCCESSFUL, HttpStatus.OK);
    }

    public ResponseEntity<?> fetchPagenatedOrder(String customerName, Integer page, Integer size) {

        List<Order> orders = new ArrayList<>();
        try {
            PageRequest pageable = PageRequest.of(page, size, Sort.by("orderTime").descending());
            Page<Order> ordersPage = orderRepository.findByCustomerName(customerName, pageable);
            orders = ordersPage.getContent();
        } catch (DataAccessException e) {
            logger.error("Error while fetching orders: ", e);
            return Response.getResponse("" , HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Response.getResponse(String.format(ResponseConstants.ORDER_FETCH_SUCCESSFUL + " for page: " + page + " with page size: " + size) , HttpStatus.OK, orders);
    }

    public ResponseEntity<?> fetchOrderStatus(Long orderId) {

        String cacheKey = RedisConstants.ORDER_CACHE_PREFIX + orderId;

        Optional<Order> optionalOrder;
        try {

            try {
                Object cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached != null && cached instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) cached;
                    Order cachedOrder = new ObjectMapper().convertValue(map, Order.class);

                    logger.info("Cache hit for orderId={}", orderId);
                    return Response.getResponse(ResponseConstants.ORDER_FETCH_SUCCESSFUL, HttpStatus.OK, cachedOrder.getOrderStatus());
                }
            } catch (RedisConnectionFailureException | RedisSystemException redisEx) {
                logger.warn("Redis unavailable while fetching order for orderId: {}", orderId, redisEx);
            }
            optionalOrder = orderRepository.findById(orderId);
        } catch (DataAccessException e) {
            logger.error("Error fetching order by ID", e);
            return Response.getResponse(ResponseConstants.FAILED_TO_FETCH_ORDER_BY_ORDER_ID, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Order order;
        if (optionalOrder.isPresent()) {
            try {
                order = optionalOrder.get();
                redisTemplate.opsForValue().set(cacheKey, order);
                return Response.getResponse(ResponseConstants.ORDER_FETCH_SUCCESSFUL, HttpStatus.OK, order.getOrderStatus());
            } catch (RedisConnectionFailureException | RedisSystemException redisEx) {
                logger.warn("Redis unavailable while setting value for orderId: {}", orderId, redisEx);
            }
        }

        logger.info("No order found with ID: " + orderId);
        return Response.getResponse(ResponseConstants.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> updateOrderStatusSync(Long orderId, String status) {
        System.out.println("");
        try {
            orderRepository.updateOrderStatus(orderId, status);

            Order updatedOrder = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            String cacheKey = RedisConstants.ORDER_CACHE_PREFIX + orderId;

            try {
                redisTemplate.opsForValue().set(cacheKey, updatedOrder);
            } catch (RedisConnectionFailureException | RedisSystemException redisEx) {
                logger.warn("Redis unavailable while fetching order for orderId: {}", orderId, redisEx);
            }

        } catch (DataAccessException e) {
            logger.error(String.format("Error while updating order status for orderId: %d", orderId), e);
            return Response.getResponse(ResponseConstants.ORDER_STATUS_UPDATE_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Response.getResponse(ResponseConstants.ORDER_STATUS_UPDATE_SUCCESSFUL, HttpStatus.OK);
    }

    public ResponseEntity<?> updateOrderStatusAsync(Map<String, String> messageMap) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String messageString = objectMapper.writeValueAsString(messageMap);

            String messageId = sqsProducerService.sendMessage(messageString);

            if (Objects.isNull(messageId)) {
                return Response.getResponse(ResponseConstants.FAILED_TO_SEND_MESSAGE_TO_SQS, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            logger.error(String.format("Error while updating order status for orderId: %s", messageMap.get("OrderId")), e);
            return Response.getResponse(ResponseConstants.FAILED_TO_SEND_MESSAGE_TO_SQS, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Response.getResponse(ResponseConstants.MESSAGE_SEND_TO_SQS, HttpStatus.OK);
    }
}
