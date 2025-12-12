package entitymodel;

public class CartItem {
	private String idCustomer;
	private String idProduct;
	private int count;
	
	public CartItem(String idCustomer, String idProduct, int count) {
		super();
		this.idCustomer = idCustomer;
		this.idProduct = idProduct;
		this.count = count;
	}
	
	public String getIdCustomer() {
		return idCustomer;
	}
	public void setIdCustomer(String idCustomer) {
		this.idCustomer = idCustomer;
	}
	public String getIdProduct() {
		return idProduct;
	}
	public void setIdProduct(String idProduct) {
		this.idProduct = idProduct;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	

}
