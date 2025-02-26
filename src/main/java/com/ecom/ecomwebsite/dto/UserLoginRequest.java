package com.ecom.ecomwebsite.dto;

public class UserLoginRequest {

	 private String email;
	    private String password;
	    
		public String getEmail() {
			return email;
		}
		public void setEmail(String userName) {
			this.email = userName;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
}
