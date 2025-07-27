package com.ecom.util;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ecom.model.ProductOrder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class CommonUtils {

 @Autowired
 JavaMailSender mailSender;
	public Boolean sendMailForOrder(ProductOrder order,String status) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message=mailSender.createMimeMessage();
	    MimeMessageHelper helper=new MimeMessageHelper(message,true);
	    helper.setFrom("hatimsaifee098765@gmail.com","ECOM.commerce");
	    helper.setTo(order.getOrderAddress().getEmail());
	   
	    	String msssg="<p>Hey"+order.getOrderAddress().getFirstName()+"</p>"
	    		+ "<p>Your Order "+status +"</p>"
	    		+ "<p>Order Details</p>"+
	    		"<p>Name:"+order.getProduct().getTitle()+"</p>"+
	    		"<p>qunatity:"+order.getQuantity()+"</p>"+
	    		"<p>Price:"+order.getPrice()+"</p>";
	    		
	    		helper.setSubject("Order"+status+" on Ecom");
	    		helper.setText(msssg);
	    		mailSender.send(message);
	    		return true;
	    
	}
}
