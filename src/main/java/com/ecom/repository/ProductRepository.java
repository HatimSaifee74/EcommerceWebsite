package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import com.ecom.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	
	public List<Product> findByIsActiveTrue();
public Page<Product> findByIsActiveTrue(Pageable pageable);
public Page<Product> findByCategory( Pageable pageable,String category);
	public List<Product> findByCategory(String category);
	public List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch1, String ch2);

	public Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(Pageable pageable,String ch1, String ch2);
}
