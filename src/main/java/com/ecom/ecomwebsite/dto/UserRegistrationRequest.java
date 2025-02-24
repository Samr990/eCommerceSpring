package com.ecom.ecomwebsite.dto;

import com.ecom.ecomwebsite.model.RoleType;
import com.ecom.ecomwebsite.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {
	private User user;
    private RoleType role;

}
