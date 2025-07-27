package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Cart;
import com.ecom.model.UserDtls;

public interface CartRepository  extends JpaRepository<Cart, Integer> {
	public Cart findByUserIdAndProductId(int userId,int productId);
    public List<Cart> getCartByUserId(int  userId);
    public int countByUserId(int userId);
    
}
