package entitymodel;

public class Promo {
	private String idPromo;
	private String code;
	private String headline;
	private double discountPercentage;
	
	public Promo(String idPromo, String code, String headline, double discountPercentage) {
		super();
		this.idPromo = idPromo;
		this.code = code;
		this.headline = headline;
		this.discountPercentage = discountPercentage;
	}
	
	public String getIdPromo() {
		return idPromo;
	}
	public void setIdPromo(String idPromo) {
		this.idPromo = idPromo;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public double getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	
	

}
