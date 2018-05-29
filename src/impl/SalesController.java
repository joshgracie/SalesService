package impl;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.ISalesController;
import impl.stocks.Stock;

@RestController
public class SalesController implements ISalesController {
	
	private HashMap<String,List<Sale>> sales;
	private HashMap<String,List<Adjustment>> adjustments;
	private int messageCount;
	private boolean acceptingMessages;
	
	public SalesController() {
		this.sales = new HashMap<String,List<Sale>>();
		this.adjustments = new HashMap<String,List<Adjustment>>();
		this.messageCount = 0;
		this.acceptingMessages = true;
	}
	
	// Handles a basic message by recording a sale with a default quantity of 1
	@RequestMapping("/message")
	public int handleMessage(@RequestParam(value="productName", defaultValue="") String productName,
			@RequestParam(value="price", defaultValue="") String price) {
		if (this.acceptingMessages){
			checkAndIncrementCount();
			Sale sale = recordSale(productName, price, "1");
			if (sale != null){
				return 1;
			}
		}
		return -1;
	}
	
	// Handles a basic message by recording a sale with a variable quantity defined by request
	@RequestMapping("/message")
	public int handleMessage(@RequestParam(value="productName", defaultValue="") String productName,
			@RequestParam(value="price", defaultValue="") String price,
			@RequestParam(value="quantity", defaultValue="") String quantity) {
		if (this.acceptingMessages){
			checkAndIncrementCount();
			Sale sale = recordSale(productName, price, quantity);
			if (sale != null){
				return 1;
			}
		}
		return -1;
	}
	
	// Handles message by recording a sale with a variable quantity, and applying an adjustment to other sales for the same product
	@RequestMapping("/message")
	public int handleMessage(@RequestParam(value="productName", defaultValue="") String productName,
			@RequestParam(value="price", defaultValue="") String price,
			@RequestParam(value="quantity", defaultValue="") String quantity,
			@RequestParam(value="operation", defaultValue="") String operation) {
		if (this.acceptingMessages){
			checkAndIncrementCount();
			Sale sale = recordSale(productName, price, quantity);
			int adjustmentResult = performAdjustment(productName, operation, sale.getPrice());
			if (sale != null && adjustmentResult != -1){
				return 1;
			}
		}
		return -1;
	}
	
	// Keeps track of number of messages, and runs reports at set intervals.
	private void checkAndIncrementCount() {
		this.messageCount++;
		if (messageCount % 10 == 0){
			logSalesReport();
		}
		if (messageCount % 50 == 0){
			logAdjustmentReport();
			this.acceptingMessages = false;
		}
	}
	

	// Records a sale for a given product
	private Sale recordSale(String productName, String price, String quantity) {
		Sale sale = new Sale (productName, Float.parseFloat(price), Float.parseFloat(quantity));
		if (sale.isValidSale()){
			if (!sales.containsKey(sale.getProductName())){
				sales.put(sale.getProductName(), new ArrayList<Sale>());
			}
			sales.get(sale.getProductName()).add(sale);
			return sale;
		}
		return null;
	}
	
	// Performs an adjustment on pre-existing sales for a given product
	private int performAdjustment(String productName, String operation, BigDecimal value){
		Adjustment adjustment = new Adjustment(productName, operation, value);
		List<Sale> salesForProduct = this.sales.get(adjustment.getProductName());
		if (!salesForProduct.isEmpty()){
			Sale sale;
			if (adjustment.getOperation().equals("ADD")){
				for (int i = 0; i < salesForProduct.size()-1; i++){ // size-1 so that the sale just added isn't adjusted
					sale = salesForProduct.get(i);
					sale.setPrice(sale.getTotalValue().add(adjustment.getValue()));
				}
			}
			else if (adjustment.getOperation().equals("SUBTRACT")){
				for (int i = 0; i < salesForProduct.size()-1; i++){ // size-1 so that the sale just added isn't adjusted
					sale = salesForProduct.get(i);
					sale.setPrice(sale.getTotalValue().subtract(adjustment.getValue()));
				}
			}
			else if (adjustment.getOperation().equals("MULTIPLY")){
				for (int i = 0; i < salesForProduct.size()-1; i++){ // size-1 so that the sale just added isn't adjusted
					sale = salesForProduct.get(i);
					sale.setPrice(sale.getTotalValue().multiply(adjustment.getValue()));
				}
			}
			
			if (!adjustments.containsKey(adjustment.getProductName())){
				adjustments.put(adjustment.getProductName(), new ArrayList<Adjustment>());
			}
			adjustments.get(adjustment.getProductName()).add(adjustment);
			
			return 1;
		}
		return -1;
	}
	
	// Logs a report of all products and their sale values.
	private void logSalesReport() {
		System.out.println("Sales Report");
		Iterator<HashMap.Entry<String,List<Sale>>> it = this.sales.entrySet().iterator();
		while (it.hasNext()){
			HashMap.Entry<String,List<Sale>> pair = it.next();
			BigDecimal totalForProduct = new BigDecimal(0);
			for (int i = 0; i < pair.getValue().size(); i++){
				totalForProduct = totalForProduct.add(pair.getValue().get(i).getTotalValue());
			}
			System.out.println("Total number of sales for " + pair.getKey() + ": " + pair.getValue().size() + ". Total value: " + totalForProduct.toString());
		}
	}
	
	// Logs a report of all adjustments that have taken place
	private void logAdjustmentReport(){
		System.out.println("Application pausing, no longer accepting messages.");
		System.out.println("Adjustment Report");
		
		Iterator<HashMap.Entry<String,List<Adjustment>>> it = this.adjustments.entrySet().iterator();
		while (it.hasNext()){
			HashMap.Entry<String,List<Adjustment>> pair = it.next();
			System.out.println("Adjustments for " + pair.getKey() + ": ");
			for (int i = 0; i < pair.getValue().size(); i++){
				Adjustment adjustment = pair.getValue().get(i);
				System.out.println(adjustment.getOperation() + " " + adjustment.getValue().toString());
			}
		}
	}

	public HashMap<String, List<Sale>> getSales() {
		return sales;
	}

	public void setSales(HashMap<String, List<Sale>> sales) {
		this.sales = sales;
	}

	public HashMap<String, List<Adjustment>> getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(HashMap<String, List<Adjustment>> adjustments) {
		this.adjustments = adjustments;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	public boolean isAcceptingMessages() {
		return acceptingMessages;
	}

	public void setAcceptingMessages(boolean acceptingMessages) {
		this.acceptingMessages = acceptingMessages;
	}

}
