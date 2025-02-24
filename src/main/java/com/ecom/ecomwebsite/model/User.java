package com.ecom.ecomwebsite.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	
	@Column(name = "passwd", nullable = false)
	private String passwd;
	
	@Column(name = "user_name", nullable = false)
	private String userName;
	
	@Column(name = "address")
	private String address;
	
	@Enumerated(EnumType.STRING) // Stores as a string in DB (CUSTOMER, SELLER, ADMIN)
	@Column(name = "role", nullable = false)
	private RoleType role;
	
	 @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	 private Cart cart;
	
	 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	 private List<Order> orders;

		
}
