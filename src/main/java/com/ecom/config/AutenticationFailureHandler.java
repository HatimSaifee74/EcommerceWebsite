package com.ecom.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.sevice.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AutenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	@Autowired
	UserRepository userRepo;
	@Autowired
	UserService userService;
@Override
public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException{
	
	String email=request.getParameter("username");
	UserDtls user=userRepo.findByEmail(email);
	if(user!=null) 
		if(user.isEnable()) {
			if(user.isAccountNonLocked())
			{
			if(user.getFailedAttempt()<3) {
				userService.increaseFailedAttempt(user);
			}else {
				userService.userAccountLock(user);
				exception= new LockedException("Account is locked!! try after some time");
			}
		}else {
			if(userService.userAccountLockTimeExpired(user))
				exception= new LockedException("Account is Unlocked try Again.");
			else
			exception=new LockedException("Account is locked!! try after some time");
		}
	}else {
		exception= new LockedException("User is Disabled");
		
		
	}else {
		exception= new LockedException("Invalid Email !!");
	}
		super.setDefaultFailureUrl("/signin?error");
	super.onAuthenticationFailure(request, response, exception);
}
}
