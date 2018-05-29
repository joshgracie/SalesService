package impl;

import java.math.BigDecimal;

import api.ISale;

public class Sale implements ISale {
	
	private String productName;
	private BigDecimal price;
	private BigDecimal quantity;
	private BigDecimal totalValue;
	
	public Boolean isValidSale(){
		return !this.productName.equals("") && (price.compareTo(new BigDecimal(0)) == 1) && (quantity.compareTo(new BigDecimal(0)) == 1);
			
	}
	
	public Sale(String productName, float price, float quantity){
		this.productName = productName;
		this.price = new BigDecimal(price);
		this.quantity = new BigDecimal(quantity);
		this.totalValue = this.price.multiply(this.quantity);
	}
	
	public Sale(String productName, BigDecimal price, BigDecimal quantity) {
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
		this.totalValue = this.price.multiply(this.quantity);
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

}
