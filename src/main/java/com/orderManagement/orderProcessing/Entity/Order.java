package com.orderManagement.orderProcessing.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orderManagement.orderProcessing.Constants.DatabaseConstants;
import com.orderManagement.orderProcessing.DTOs.Items;
import com.orderManagement.orderProcessing.Helper.ItemsConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.List;

@Entity
@Table(name = DatabaseConstants.ORDERS)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = DatabaseConstants.ORDER_ID)
    public Long orderId;

    @NotNull
    @JsonProperty(value = "customer_name")
    @Column(name = DatabaseConstants.CUSTOMER_NAME)
    public String customerName;

    @NotNull
    @JsonProperty(value = "total_amount")
    @Column(name = DatabaseConstants.TOTAL_AMOUNT)
    public Integer totalAmount;

    @NotNull
    @JsonProperty(value = "items")
    @Column(name = DatabaseConstants.ITEMS)
    @Convert(converter = ItemsConverter.class)
    public List<Items> items;

    @JsonProperty(value = "order_status")
    @Column(name = DatabaseConstants.ORDER_STATUS)
    public String orderStatus;

    @NotNull
    @JsonProperty(value = "order_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = DatabaseConstants.ORDER_TIME)
    public String orderTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty(value = "created_at")
    @CreationTimestamp
    @Column(name = DatabaseConstants.CREATED_AT, updatable = false)
    public String createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty(value = "updated_at")
    @UpdateTimestamp
    @Column(name = DatabaseConstants.UPDATED_AT)
    public String updatedAt;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
