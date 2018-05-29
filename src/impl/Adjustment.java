package impl;

import java.math.BigDecimal;

public class Adjustment {
	
	private String productName;
	private String operation;
	private BigDecimal value;
	
	public Adjustment(String productName, String operation, BigDecimal value) {
		this.productName = productName;
		this.operation = operation;
		this.value = value;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
