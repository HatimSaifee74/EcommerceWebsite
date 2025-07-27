package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CategoryRepository;
import com.ecom.repository.UserRepository;
import com.ecom.sevice.CartService;
import com.ecom.sevice.CategoryService;
import com.ecom.sevice.CommonService;
import com.ecom.sevice.ProductService;
import com.ecom.sevice.UserService;

import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MainController {
@Autowired
	UserService userService;
@Autowired
CategoryService categoryService;
@Autowired
UserRepository userRepo;
@Autowired
ProductService productService;
@Autowired
CartService cartService;
@ModelAttribute
public void getUserDetails(Principal p,Model m ) {
	System.out.println("in getuserDetails");
	if(p!=null) {
		String email=p.getName();
		UserDtls user=userRepo.findByEmail(email);
		m.addAttribute("user", user);
		int cartCount=cartService.getCountCart(user.getId());
		System.out.println("in getuserDetails3");
		m.addAttribute("countCart", cartCount);
	}
	List <Category> allactivecat=categoryService.getActiveCategory();
	m.addAttribute("categorys", allactivecat);
}
@GetMapping("/")
	public String index(Model m)
{
	List<Category> category= categoryService.getActiveCategory().stream().sorted((p1,p2)->p2.getId()-p1.getId()).limit(6).toList();
	List<Product> products=productService.getAllProducts().stream().sorted((p1,p2)->p2.getId()-p1.getId()).limit(8).toList();
	m.addAttribute("category",category);
	m.addAttribute("products", products);
	return "index";
		}

@GetMapping("/signin")
public String login()
{
	return "login";
	}

@GetMapping("/register")
public String register()
{
	return "register";
	
}
@GetMapping("/products")
public String products(Model m, @RequestParam (defaultValue ="") String category,@RequestParam(defaultValue = "0") int pageNo,@RequestParam(defaultValue = "1") int pageSize)
{
	
	m.addAttribute("categories",categoryService.getActiveCategory());
	m.addAttribute("products",productService.getActiveProducts(category));
Page<Product> products=productService.getActiveProductsPagination(pageNo,pageSize,category);
System.out.println((List)products.getContent());
	m.addAttribute("products",products.getContent());
	m.addAttribute("totalElements", products.getTotalElements());
	m.addAttribute("pageNo",products.getNumber());
	m.addAttribute("totalPages",products.getTotalPages());
	m.addAttribute("isFirst",products.isFirst());
	m.addAttribute("isLast",products.isLast());
	m.addAttribute("pageSize",pageSize);
	return "product";
	
}

@GetMapping("/product/{id}")
public String product(@PathVariable int id, Model m) {	
	Product product=productService.getProduct(id);
	m.addAttribute("product",product);
	
	return "view_product";
	
}

@PostMapping("/saveUser")
public String saveUser(@ModelAttribute UserDtls user,@RequestParam MultipartFile img, HttpSession session) throws IOException {
	if(!ObjectUtils.isEmpty(userRepo.findByEmail(user.getEmail())))
	{session.setAttribute("errorMsg", "EMail Already used");
		return "redirect:/register";}
	MultipartFile file=img;
	String fileName=file.isEmpty()?"default.jpg":file.getOriginalFilename();
	System.out.print("in save user "+ ObjectUtils.isEmpty(user));
	if(!ObjectUtils.isEmpty(user)) {
	user.setProfileImage(fileName);
	user.setRole("ROLE_USER");
	UserDtls saveduser=userService.saveUser(user);
	System.out.println("after save user");
	if(!ObjectUtils.isEmpty(saveduser)) {
		if(!file.isEmpty()) {
			File saveFile=new ClassPathResource("static/img").getFile();
			Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+"user_img"+File.separator+fileName);
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			}
	return "redirect:/signin";
	}
	else
		session.setAttribute("errorMsg", "unable to save ! Interval server Error");
	
	}
	
	
	return "redirect:/register";
}

@GetMapping("/search")
public String searchProduct(@RequestParam String ch,Model m) {
	List<Product> products=productService.searchProducts(ch);
	m.addAttribute("categories",categoryService.getActiveCategory());

	m.addAttribute("productsSize", products.size());
	
	m.addAttribute("products",products);
	System.out.println("helo   "+products.size());
	return "product";
}

}