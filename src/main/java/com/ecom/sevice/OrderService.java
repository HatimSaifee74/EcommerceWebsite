package com.ecom.sevice;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

public interface OrderService {

	public ProductOrder saveOrder(int userid ,OrderRequest orderrequest);
	public List userOrders(int userId);
	public List allOrders();
	public Page<ProductOrder> allOrders(int pageSize,int pageNo);

	public ProductOrder updateOrderStatus(int id,String status);
	public ProductOrder searchOrder(String ch);
}
