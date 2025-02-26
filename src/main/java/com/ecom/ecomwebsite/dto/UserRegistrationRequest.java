package com.ecom.ecomwebsite.dto;

import com.ecom.ecomwebsite.model.RoleType;
import com.ecom.ecomwebsite.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {
	 private String email;
	    private String password;
	    private String userName;
	    private String address;
    private RoleType role;

}
