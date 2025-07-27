package com.ecom.sevice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.ecom.model.Category;
import com.ecom.model.Product;
@Service
public interface ProductService {
	public Product saveProduct(Product product);
	public List<Product> getAllProducts();
	public Page<Product> getAllProducts(int pageNo, int pageSize);
	public boolean deleteProduct(int id );
	public Product getProduct(int id);
	public List<Product> getActiveProducts(String category);
	public List<Product> searchProducts(String ch);
	public Page<Product> searchProducts(int pageNo,int pageSize,String ch);
	public Page<Product> getActiveProductsPagination(int pageNo,int pageSize,String category);
}
