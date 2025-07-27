package com.ecom.sevice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.sevice.CartService;

@Service
public class CartServiceImpl implements CartService {
@Autowired
	private CartRepository cartRepo;
@Autowired
private UserRepository userRepo;
@Autowired
private ProductRepository productRepo;
	@Override
	public Cart saveCart(int productId, int userId) {
		UserDtls user =userRepo.findById(userId).get();
		Product product=productRepo.findById(productId).get();
		Cart cartStatus=cartRepo.findByUserIdAndProductId(userId, productId);
		Cart cart=null;
		if(ObjectUtils.isEmpty(cartStatus)) {
			cart=new Cart();
			cart.setProduct(product);
			cart.setUser(user);
			cart.setQuantity(1);
			cart.setTotalPrice(product.getDiscountPrice());
		}else {
			cart=cartStatus;
			cart.setQuantity(cart.getQuantity()+1);
			cart.setTotalPrice(cart.getQuantity()*product.getDiscountPrice());
		}
		Cart saveCart=cartRepo.save(cart);
		return saveCart;
	}

	@Override
	public List<Cart> getCartsByUser(int userId) {
		return cartRepo.getCartByUserId(userId);
	}

	public boolean updateCartCount(String sy, int cid) {
		Cart cart=cartRepo.findById(cid).get();
		int quan=cart.getQuantity();
		if (sy.equals("de")) {
			if (quan>1)
			{
				cart.setQuantity(quan-1);
				cartRepo.save(cart);
			}
			else
				cartRepo.delete(cart);
			return true;
			
		}else if(sy.equals("in")) {
			cart.setQuantity(quan+1);
			cartRepo.save(cart);
			return true;
		}
		return false;
	}
	@Override

	public int getCountCart(int userId) {
		System.out.println("in getcount cart");
	int countbyuserid= cartRepo.countByUserId(userId);
	 return countbyuserid;
	}

}
