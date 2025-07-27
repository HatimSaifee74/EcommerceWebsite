package com.ecom.model;

import java.awt.Window.Type;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductOrder {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)

	private int id;
String orderId;
private Date orderDate;
@ManyToOne
private Product product;
private double price;
private int quantity;
@ManyToOne
private UserDtls user;
private String status;
private String PaymentType;

@ManyToOne(cascade = CascadeType.ALL)
private OrderAddress orderAddress;

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getOrderId() {
	return orderId;
}

public void setOrderId(String orderId) {
	this.orderId = orderId;
}

public Date getOrderDate() {
	return orderDate;
}

public void setOrderDate(Date orderDate) {
	this.orderDate = orderDate;
}

public Product getProduct() {
	return product;
}

public void setProduct(Product product) {
	this.product = product;
}

public double getPrice() {
	return price;
}

public void setPrice(double price) {
	this.price = price;
}

public int getQuantity() {
	return quantity;
}

public void setQuantity(int quantity) {
	this.quantity = quantity;
}

public UserDtls getUser() {
	return user;
}

public void setUser(UserDtls user) {
	this.user = user;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public String getPaymentType() {
	return PaymentType;
}

public void setPaymentType(String paymentType) {
	PaymentType = paymentType;
}

public OrderAddress getOrderAddress() {
	return orderAddress;
}

public void setOrderAddress(OrderAddress orderAddress) {
	this.orderAddress = orderAddress;
}


}
