package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder,Integer> {
public List<ProductOrder> findByUserId(int userId);
public ProductOrder findByOrderId(String ch);
}
