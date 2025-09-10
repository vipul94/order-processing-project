package com.orderManagement.orderProcessing.Repository.DBRepository;

import com.orderManagement.orderProcessing.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomerName(String customerName, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.orderStatus = :status WHERE o.orderId = :orderId")
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
}
