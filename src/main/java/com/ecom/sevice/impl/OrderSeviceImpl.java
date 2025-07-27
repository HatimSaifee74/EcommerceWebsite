package com.ecom.sevice.impl;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ecom.model.Cart;
import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.sevice.OrderService;
import com.ecom.util.OrderStatus;

@Service
public class OrderSeviceImpl implements OrderService {

	@Autowired
	ProductOrderRepository orderRepo;
	@Autowired
	CartRepository cartRepo;
	@Override
	public ProductOrder saveOrder(int userid,OrderRequest orderrequest) {
		List<Cart> carts=cartRepo.getCartByUserId(userid);
		ProductOrder order=null;
		for(Cart c:carts) {
			 order= new ProductOrder();
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(new Date());
		    order.setProduct(c.getProduct());
		    order.setPrice(c.getProduct().getDiscountPrice());
		    order.setQuantity(c.getQuantity());
		    order.setUser(c.getUser());
		    order.setStatus(OrderStatus.IN_PROGRESS.name());
		    order.setPaymentType(orderrequest.getPaymentType());
		    OrderAddress address= new OrderAddress();
		    address.setAddress(orderrequest.getAddress());
		    address.setCity(orderrequest.getCity());
		    address.setEmail(orderrequest.getEmail());
		    address.setFirstName(orderrequest.getFirstName());
            address.setLastName(orderrequest.getLastName());
             address.setMobileNo(orderrequest.getMobileNo());
             address.setPincode(orderrequest.getPincode());
             address.setState(orderrequest.getState());
             order.setOrderAddress(address);
             orderRepo.save(order);
		}
		return order;
	}

	public List userOrders(int userId) {
		List<ProductOrder> orders=orderRepo.findByUserId(userId);
		return orders;
	}
	

public ProductOrder updateOrderStatus(int id,String status) {
	ProductOrder order= orderRepo.findById(id).get();
	if(order!=null) {
		order.setStatus(status);
		orderRepo.save(order);
		return order;
	}
	return null;
	
	
}

@Override
public List allOrders() {
	return orderRepo.findAll();
	}

@Override
public ProductOrder searchOrder(String ch) {
	
	return orderRepo.findByOrderId(ch);
}

@Override
public Page<ProductOrder> allOrders(int pageSize, int pageNo) {
org.springframework.data.domain.Pageable pageable =PageRequest.of(pageNo, pageSize);

	return orderRepo.findAll(pageable);
}


}
