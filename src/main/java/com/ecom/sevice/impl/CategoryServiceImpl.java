package com.ecom.sevice.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.sevice.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{
@Autowired
	CategoryRepository categoryRepository;
	@Override
	public Category saveCategory(Category category) {
		
		return  categoryRepository.save(category);
	}

	@Override
	public List<Category> getAllCategory() {
		// TODO Auto-generated method stub
		
		return categoryRepository.findAll();
	}
	
public boolean existsCategory(String name) {
	return categoryRepository.existsByName(name);
}

@Override
public boolean deleteCategory(int id) {
Category category = categoryRepository.findById(id).orElse(null);
if(!ObjectUtils.isEmpty(category)) {
categoryRepository.delete(category);
return true;
}
	return false;
}

public Category getCategoryById(int id) {
	return categoryRepository.findById(id).orElse(null);
}

@Override
public List<Category> getActiveCategory() {
	
	return categoryRepository.findByActiveTrue();
}

@Override
public Page<Category> getAllCategory(int pageNo, int pageSize) {
Pageable pageable = PageRequest.of(pageNo, pageSize);
	return categoryRepository.findAll(pageable);
}

}
