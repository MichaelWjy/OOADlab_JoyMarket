package models;

public class Courier extends User{
	private String vehicleType;
	private String vehiclePlate;
	
	public Courier(String id, String name, String email, String password, String phone, String address, String gender,
			String role, String vehicleType, String vehiclePlate) {
		super(id, name, email, password, phone, address, gender, role);
		this.vehicleType = vehicleType;
		this.vehiclePlate = vehiclePlate;
	}
	
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getVehiclePlate() {
		return vehiclePlate;
	}
	public void setVehiclePlate(String vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}
	
}
