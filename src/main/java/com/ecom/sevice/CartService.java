package com.ecom.sevice;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.model.Cart;

@Service
public interface CartService {
	
	public Cart saveCart(int productId, int userId);
	public List<Cart> getCartsByUser(int userId) ;
	public int getCountCart(int userId);
	public boolean updateCartCount(String sy, int cid);
}
