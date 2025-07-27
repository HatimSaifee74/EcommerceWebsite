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
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.config.SecurityConfig;
import com.ecom.model.*;
import com.ecom.repository.UserRepository;
import com.ecom.sevice.CartService;
import com.ecom.sevice.CategoryService;
import com.ecom.sevice.OrderService;
import com.ecom.sevice.UserService;
import com.ecom.util.CommonUtils;
import com.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequestMapping("/user")
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder;

    

    private final SecurityConfig securityConfig;

   
	@Autowired
	private	UserRepository userRepo;
	@Autowired
	private CategoryService categoryservice;
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CommonUtils commonUtils;
	@Autowired
	private UserService userService;

    UserController(MainController mainController, SecurityConfig securityConfig, UserService userService, BCryptPasswordEncoder passwordEncoder) {
       
        this.securityConfig = securityConfig;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    public UserDtls getUser(Principal p) {
    		
    			String email=p.getName();
    			UserDtls user=userRepo.findByEmail(email);
    			return user ;
    }
	@GetMapping("/")
	public String index() {
		return "user/index";
	}
	@ModelAttribute
	public void getUserDetails(Principal p,Model m ) {
		System.out.println("in getuserDetails");
		if(p!=null) {
			String email=p.getName();
			UserDtls user=userRepo.findByEmail(email);
			System.out.println("in getuserDetails2");
			m.addAttribute("user", user);
		
			int cartCount=cartService.getCountCart(user.getId());
			System.out.println("in getuserDetails3");
			m.addAttribute("countCart", cartCount);
		}
		List <Category> allactivecat=categoryservice.getActiveCategory();
		m.addAttribute("categorys", allactivecat);
	}
	@GetMapping("/addCart")
	public String addCart(@RequestParam int pid,@RequestParam int uid, HttpSession session ) {
		Cart cart=cartService.saveCart(pid, uid);
		if(ObjectUtils.isEmpty(cart))
			session.setAttribute("errorMsg", "unable to add to cart ! Interval server Error");
		else session.setAttribute("succMsg", "Added to cart");
		return "redirect:/product/"+pid;
	}
	@GetMapping("/cart")
	public String loadCartPage(Principal p , Model m) {
		if(p!=null) {
			String email=p.getName();
			UserDtls user=userRepo.findByEmail(email);
		List<Cart> Carts=cartService.getCartsByUser(user.getId());
		double totalOrderPrice=0;
		for(Cart cart: Carts)
			totalOrderPrice+=cart.getTotalPrice();
		m.addAttribute("carts",Carts);
		m.addAttribute("totalOrderPrice",totalOrderPrice);
		return "user/cart";
		}
		return "redirect:/signin";
		
	}
	@GetMapping("/cartQuantityUpdate")
	public String cartQuantityUpdate(@RequestParam String sy,@RequestParam int cid) {
		cartService.updateCartCount(sy,cid);
				
		return "redirect:/user/cart";
	}
	@GetMapping("/orders")
	public String ordersPage(Principal p,  Model m)	{
		String email=p.getName();
		UserDtls user=userRepo.findByEmail(email);
		
		double totalOrderPrice=0;
		List<Cart> Carts=cartService.getCartsByUser(user.getId());
		for(Cart cart: Carts)
			totalOrderPrice+=cart.getTotalPrice();
		m.addAttribute("orderPrice", totalOrderPrice);
		return "user/order";
	}
	@PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest,Principal p) throws UnsupportedEncodingException, MessagingException	{
		String email=p.getName();
		UserDtls user=userRepo.findByEmail(email);
		ProductOrder order=orderService.saveOrder(user.getId(), orderRequest);
		commonUtils.sendMailForOrder(order,"Placed");
		return "redirect:/user/success";
	}
	@GetMapping("/success")
	public String success() {
		return "user/success";
	}
	@GetMapping("/user-orders")
	public String myOrders(Principal p,Model m) {
		UserDtls user= getUser(p);
		List<ProductOrder> orders= orderService.userOrders(user.getId());
		m.addAttribute("orders",orders);
		return "user/my_orders";
	}
	@GetMapping("/update-status")
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
		return "redirect:/user/user-orders";
	}
	@GetMapping("/profile")
	public String userProfile() {
		return "user/profile";
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
		return "redirect:/user/profile";
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
		 return "redirect:/user/profile";
	}
}
