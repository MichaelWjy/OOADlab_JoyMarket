package models;

public class Delivery {
	private String idOrder;
	private String idCourier;
	private String status;
	
	public Delivery(String idOrder, String idCourier, String status) {
		super();
		this.idOrder = idOrder;
		this.idCourier = idCourier;
		this.status = status;
	}
	
	public String getIdOrder() {
		return idOrder;
	}
	public void setIdOrder(String idOrder) {
		this.idOrder = idOrder;
	}
	public String getIdCourier() {
		return idCourier;
	}
	public void setIdCourier(String idCourier) {
		this.idCourier = idCourier;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
}
