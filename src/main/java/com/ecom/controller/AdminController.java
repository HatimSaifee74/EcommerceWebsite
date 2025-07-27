package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.sevice.CategoryService;
import com.ecom.sevice.OrderService;
import com.ecom.sevice.ProductService;
import com.ecom.sevice.UserService;
import com.ecom.sevice.impl.OrderSeviceImpl;
import com.ecom.util.CommonUtils;
import com.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
@Autowired
    private  BCryptPasswordEncoder passwordEncoder;
 
@Autowired
	CategoryService categoryService;

@Autowired
ProductService productService;
@Autowired
UserService userService;
@Autowired
UserRepository userRepo;
@Autowired
OrderService orderService;
@Autowired
CommonUtils commonUtils;


   


@ModelAttribute
public void getUserDetails(Principal p,Model m ) {
	System.out.println("in getuserDetails");
	if(p!=null) {
		String email=p.getName();
		UserDtls user=userRepo.findByEmail(email);
		m.addAttribute("user", user);
	}
	List <Category> allactivecat=categoryService.getActiveCategory();
	m.addAttribute("categorys", allactivecat);
}

	@GetMapping("/")
	public String index() {
	return "admin/index";
	}
	
	
	@GetMapping("/category")
	public String category(@RequestParam(defaultValue = "0") int pageNo,@RequestParam(defaultValue = "1") int pageSize, Model  m) {
		Page <Category> page=categoryService.getAllCategory(pageNo,pageSize);
		
		m.addAttribute("categorys", page.getContent());

		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("pageNo",page.getNumber());
		m.addAttribute("totalPages",page.getTotalPages());
		m.addAttribute("isFirst",page.isFirst());
		m.addAttribute("isLast",page.isLast());
		m.addAttribute("pageSize",pageSize);
		
	return "admin/category";
	}
	
	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category,@RequestParam MultipartFile file ,HttpSession session) throws IOException {
		String filename=!file.isEmpty()?file.getOriginalFilename():"default.jpg";
		System.out.println(file.isEmpty());
		System.out.println(filename);
		System.out.println("Check");
		category.setImageName(filename);
		if(categoryService.existsCategory(category.getName())) {
			System.out.println("Category Already exists");
			session.setAttribute("errorMsg", "Category Already exists");
		}else {
		
			if(ObjectUtils.isEmpty(categoryService.saveCategory(category))) {
				
				session.setAttribute("errorMsg", "unable to save ! Interval server Error");
			}
			else {
				if(!file.isEmpty()) {
				File saveFile=new ClassPathResource("static/img").getFile();
				Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+filename);
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);}
				
			}
			session.setAttribute("SuccMsg", "Saved Succesfully");
		}
		return "redirect:/admin/category";
	}
	
	
	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session ) {
		if(categoryService.deleteCategory(id))
			session.setAttribute("succMsg", "Category deleted");
		else session.setAttribute("errorMsg", "Error! Internal SErver Error!");
		
	return "admin/category";
	}
	
	
	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id ,Model m) {
		Category category=categoryService.getCategoryById(id);
		m.addAttribute(category);
		
	return "admin/edit_category";
	}
	
	
	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, MultipartFile file ,HttpSession session ) throws IOException {
	Category oldCategory= categoryService.getCategoryById(category.getId());
	if(ObjectUtils.isEmpty(category))
		return "redirect:/admin/category";
	if(ObjectUtils.isEmpty(oldCategory)){
		session.setAttribute("errorMsg", "Category doesnot exist!!");
		return "admin/loadEditCategory/"+category.getId();
	}
	
	
	String fileName=file.isEmpty()?oldCategory.getImageName():file.getOriginalFilename();
	if(!ObjectUtils.isEmpty(category)) {
		if(!file.isEmpty()) {
		File saveFile=new ClassPathResource("static/img").getFile();
		Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+fileName);
		Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
		}
		oldCategory.setName(category.getName());
	oldCategory.setIsActive(category.isActive());
	oldCategory.setImageName(fileName);
	}
	Category updateCategory=categoryService.saveCategory(oldCategory);
	if(!ObjectUtils.isEmpty(updateCategory)){
		
		session.setAttribute("succMsg", "Category Updated");
	}
	else session.setAttribute("errorMsg", "Internal Server Error!!");
	
	return "redirect:/admin/loadEditCategory/"+category.getId();
	}
	

	@GetMapping("/products")
	public String products(@RequestParam(defaultValue="") String ch,@RequestParam(defaultValue = "0") int pageNo,@RequestParam(defaultValue = "1") int pageSize, Model m) {
		Page<Product> page=null;
		if(ch.equals(""))
		page=productService.getAllProducts( pageNo, pageSize);
		else
			page=productService.searchProducts( pageNo, pageSize,ch);
	m.addAttribute("products",page.getContent());
	
	m.addAttribute("totalElements", page.getTotalElements());
	m.addAttribute("pageNo",page.getNumber());
	m.addAttribute("totalPages",page.getTotalPages());
	m.addAttribute("isFirst",page.isFirst());
	m.addAttribute("isLast",page.isLast());
	m.addAttribute("pageSize",pageSize);
	
	return "admin/products";
	} 
	
	
	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session  ) {
       if(productService.deleteProduct(id))
    	   session.setAttribute("succMsg", "Product deleted");
       else
    	   session.setAttribute("errorMsg", "Product to be deleted doesn't exist!");
	return "redirect:/admin/products";
	} 
	
	
	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		m.addAttribute("categories",categoryService.getAllCategory());
	return "admin/add_product";
	} 
	
	
	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m ) {
		m.addAttribute("product",productService.getProduct(id));
		m.addAttribute("categories",categoryService.getAllCategory());
	return "admin/edit_product";
	} 
	
	
	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product,HttpSession session,MultipartFile file ) throws IOException {
		
		Product oldProduct= productService.getProduct(product.getId());
		if(ObjectUtils.isEmpty(product))
			return "redirect:/admin/products";
		if(ObjectUtils.isEmpty(oldProduct)){
			session.setAttribute("errorMsg", "product doesnot exist!!");
			return "redirect:/admin/editProduct/"+product.getId();
		}
		String fileName=file.isEmpty()?oldProduct.getImage():file.getOriginalFilename();
		
			if(!file.isEmpty()) {
			File saveFile=new ClassPathResource("static/img").getFile();
			Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+"product_img"+File.separator+fileName);
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			}
			oldProduct.setTitle(product.getTitle());
			oldProduct.setDescription(product.getDescription());
			oldProduct.setCategory(product.getCategory());
			oldProduct.setPrice(product.getPrice());
			oldProduct.setStock(product.getStock());
			oldProduct.setDiscount(product.getDiscount());
			oldProduct.setDiscountPrice(product.getPrice()-(product.getPrice()*product.getDiscount())/100 );
			oldProduct.setImage(fileName);
			oldProduct.setIsActive(product.isActive());
			
		Product updateProduct=productService.saveProduct(oldProduct);
		if(!ObjectUtils.isEmpty(updateProduct)){
			
			session.setAttribute("succMsg", "Category Updated");
		}
		else session.setAttribute("errorMsg", "Internal Server Error!!");
		
		return "redirect:/admin/editProduct/"+product.getId();
	} 
	
	
	
	
	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, MultipartFile file, HttpSession session) throws IOException {
		if(ObjectUtils.isEmpty(product)) {
			session.setAttribute("errorMsg", "Internal Server Error!");
			return "redirect:/admin/add_product";}
		System.out.println(product.isActive());
		String filename=file.isEmpty()?"default.jpg":file.getOriginalFilename();
		product.setImage(filename);
		product.setDiscountPrice(product.getPrice()-(product.getPrice()*product.getDiscount())/100 );
		if(!file.isEmpty()) {
			File saveFile=new ClassPathResource("static/img").getFile();
			Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+"product_img"+File.separator+filename);
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			}
	 Product pr=productService.saveProduct(product);
	 if(ObjectUtils.isEmpty(pr)) {
		 session.setAttribute("errorMsg", "Internal Server Error!");
		return "redirect:/admin/add_product";}
	 session.setAttribute("succMsg", "Product added");
	return "redirect:/admin/loadAddProduct";
	} 
	
//	----------------users---------------
	
@GetMapping("/users")
public String getUsers(@RequestParam() int type,Model m) {
	if(type==1)
	m.addAttribute("users",userService.getUsers());
	if(type==2)
		m.addAttribute("users",userService.getAdmins());
	m.addAttribute("userType", type);
	return "admin/users";
}
	
@GetMapping("/updateStatus")
public String updateUserStatus(@RequestParam Boolean status ,@RequestParam Integer id, HttpSession session,@RequestParam  int type) {
	boolean f=userService.updateAccountStatus(id,status);
	if(f) {
		 session.setAttribute("succMsg", "user status changed");
	}
	else
		session.setAttribute("errorMsg", "Internal Server Error!");
	
	return "redirect:/admin/users?type="+type;
}
@GetMapping("/orders")
public String getOrders(@RequestParam(defaultValue="") String orderId,@RequestParam(defaultValue="") String ch,@RequestParam(defaultValue = "0") int pageNo,@RequestParam(defaultValue = "1") int pageSize,Model m) {
//	List<ProductOrder> orders=orderService.allOrders();
	if(orderId.equals("")) {
		Page<ProductOrder>page =orderService.allOrders(pageSize, pageNo);
		m.addAttribute("srch", false);
		
		m.addAttribute("orders",page.getContent());
		
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("pageNo",page.getNumber());
		m.addAttribute("totalPages",page.getTotalPages());
		m.addAttribute("isFirst",page.isFirst());
		m.addAttribute("isLast",page.isLast());
		m.addAttribute("pageSize",pageSize);
		
	}
	else {
	m.addAttribute("orderDtls",orderService.searchOrder(orderId));
	m.addAttribute("srch", true);
	}
	
	return "admin/orders";
}
@PostMapping("/update-order-status")
public String cancel(@RequestParam int id,@RequestParam int st,HttpSession session) throws UnsupportedEncodingException, MessagingException {
	OrderStatus[] val=OrderStatus.values();
	
	ProductOrder order;
	String sta="";
	for(OrderStatus status:val )
	if(status.getId()==st)sta=status.getName();
	order=orderService.updateOrderStatus(id, sta);
	commonUtils.sendMailForOrder(order, sta);
	if(!ObjectUtils.isEmpty(order)) {
		session.setAttribute("succMsg", "updated");
	}
	else session.setAttribute("errorMsg", "order doesnt exist");
	return "redirect:/admin/orders";
}
@GetMapping("/search-order")
public String searchOrder(@RequestParam String orderId,@RequestParam(defaultValue="") String ch,@RequestParam(defaultValue = "0") int pageNo,@RequestParam(defaultValue = "1") int pageSize, Model m) {
	if(orderId.equals("")) {
		m.addAttribute("orders",orderService.allOrders());
		m.addAttribute("srch", false);
	}
	else {
	m.addAttribute("orderDtls",orderService.searchOrder(orderId));
	m.addAttribute("srch", true);
	}
    return "admin/orders";
}
@GetMapping("/add-admin")
public String register()
{
	return "admin/add_admin";
	
}

@PostMapping("/save-admin")
public String saveUser(@ModelAttribute UserDtls user,@RequestParam MultipartFile img, HttpSession session) throws IOException {
	if(!ObjectUtils.isEmpty(userRepo.findByEmail(user.getEmail())))
	{session.setAttribute("errorMsg", "EMail Already used");
		return "redirect:/register";}
	MultipartFile file=img;
	String fileName=file.isEmpty()?"default.jpg":file.getOriginalFilename();
	System.out.print("in save user "+ ObjectUtils.isEmpty(user));
	if(!ObjectUtils.isEmpty(user)) {
	user.setProfileImage(fileName);
	user.setRole("ROLE_ADMIN");
	UserDtls saveduser=userService.saveUser(user);
	System.out.println("after save user");
	if(!ObjectUtils.isEmpty(saveduser)) {
		if(!file.isEmpty()) {
			File saveFile=new ClassPathResource("static/img").getFile();
			Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+"user_img"+File.separator+fileName);
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			}
		return "redirect:/admin/add-admin";
	}
	else
		session.setAttribute("errorMsg", "unable to save ! Interval server Error");
	
	}
	
	
	return "redirect:/admin/add-admin";
}
@GetMapping("/profile")
public String userProfile() {
	return "admin/profile";
}
@PostMapping("/update-profile")
public String UpdatePofile(@ModelAttribute UserDtls user, MultipartFile img,HttpSession session) throws IOException {
	MultipartFile file=img;
	String fileName;
	if(!file.isEmpty()) {
	if(!ObjectUtils.isEmpty(user)) {
		fileName=file.getOriginalFilename();
	user.setProfileImage(fileName);}
	UserDtls saveduser=userService.updateUserProfile(user);
	
	if(!ObjectUtils.isEmpty(saveduser)) {
		if(!file.isEmpty()) {
			File saveFile=new ClassPathResource("static/img").getFile();
			Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+"user_img"+File.separator+saveduser.getProfileImage());
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);}
	}
	else
		session.setAttribute("errorMsg", "unable to update ! Interval server Error");
	
	}
	return "redirect:/admin/profile";
}

public UserDtls getUser(Principal p) {
	
	String email=p.getName();
	UserDtls user=userRepo.findByEmail(email);
	return user ;
}
@PostMapping("/change-password")
public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword,Principal p,HttpSession session) {
System.out.println("change pss 1");
	UserDtls user =getUser(p);
	boolean matches=passwordEncoder.matches(currentPassword, user.getPassword());
	if(matches) {
		System.out.println("change pss 2 " +user.getLockedDate() + user);
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setCity("Luckhnow");
	userService.updateUser(user);
	}else {
		session.setAttribute("errorMsg", "Wrong password!!");
	}
	userService.updateUser(user);
	 return "redirect:/admin/profile";
}

}
