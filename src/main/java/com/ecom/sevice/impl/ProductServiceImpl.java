package com.ecom.sevice.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.sevice.ProductService;
@Service
public class ProductServiceImpl implements ProductService {
@Autowired
	private ProductRepository productRepository;
	@Override
	public Product saveProduct(Product product) {
		 
		return productRepository.save(product);
	}
	@Override
	public List<Product> getAllProducts() {
		
		return productRepository.findAll();
		
	}
	
	@Override
	public Product getProduct(int id) {
		Product p=productRepository.findById(id).orElse(null);
		return ObjectUtils.isEmpty(p)?new Product():p;
		
	}
	@Override
	public boolean deleteProduct(int id) {
		
Product prod =productRepository.findById(id).orElse(null);
if(!ObjectUtils.isEmpty(prod)) {
			productRepository.delete(prod);
		return true;}
return false;
	}
	@Override
	public List<Product> getActiveProducts(String category) {
		if(ObjectUtils.isEmpty(category))
		return productRepository.findByIsActiveTrue(); 
	return productRepository.findByCategory(category);
	}
	@Override
	public Page<Product> getActiveProductsPagination(int pageNo,int pageSize,String category) {
		Pageable pageable=PageRequest.of(pageNo, pageSize);
		if(ObjectUtils.isEmpty(category))
		return productRepository.findByIsActiveTrue(pageable); 
	return productRepository.findByCategory(pageable,category);
	}
	@Override
	public List<Product> searchProducts(String ch) {
//		ch="%"+ch+"%";
		System.out.println(ch);
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch);
	}
	@Override
	public Page<Product> getAllProducts(int pageNo, int pageSize) {
		Pageable pageable=PageRequest.of(pageNo, pageSize);
		
		return productRepository.findAll(pageable);
	}
	@Override
	public Page<Product> searchProducts(int pageNo,int pageSize,String ch) {
		Pageable pageable=PageRequest.of(pageNo, pageSize);
		
		
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(pageable, ch, ch);
	}

	
}
