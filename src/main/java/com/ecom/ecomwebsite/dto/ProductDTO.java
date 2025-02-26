package com.ecom.ecomwebsite.dto;


public class ProductDTO {
    private String name;
    private String description;
    private double price;
    private int quantity;
    private Long categoryId; // Only store category ID
	private Long productId;
	public String getName() {
		return name;
	}
	public Long getProductId() {
		return productId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public void setProductId(Long productId) {
		// TODO Auto-generated method stub
		this.productId= productId;
	}
    
}