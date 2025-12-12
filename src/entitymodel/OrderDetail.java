package entitymodel;

public class OrderDetail {
	private String idOrder;
	private String idProduct;
	private int qty;
	
	public OrderDetail(String idOrder, String idProduct, int qty) {
		super();
		this.idOrder = idOrder;
		this.idProduct = idProduct;
		this.qty = qty;
	}
	
	public String getIdOrder() {
		return idOrder;
	}
	public void setIdOrder(String idOrder) {
		this.idOrder = idOrder;
	}
	public String getIdProduct() {
		return idProduct;
	}
	public void setIdProduct(String idProduct) {
		this.idProduct = idProduct;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	
	

}
