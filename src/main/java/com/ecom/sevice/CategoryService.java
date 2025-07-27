package com.ecom.sevice;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.ecom.model.Category;

public interface CategoryService {

	public Category saveCategory(Category category);
	public List<Category> getAllCategory();
	public boolean existsCategory(String name) ;
	public boolean deleteCategory(int id);
	public Category getCategoryById(int id);
	public List<Category> getActiveCategory();
	public Page<Category> getAllCategory(int pageNo,int pageSize); 
}
