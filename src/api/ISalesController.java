package api;

public interface ISalesController {
	
	public int handleMessage(String productName, String price);
	
	public int handleMessage(String productName, String price, String quantity);
	
	public int handleMessage(String productName, String price, String quantity, String operation);

}
