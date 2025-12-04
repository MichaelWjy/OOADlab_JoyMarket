package models;

public class Admin extends User{
	
	private String emergencyContact;

	public Admin(String id, String name, String email, String password, String phone, String address, String gender,
			String role, String emergencyContact) {
		super(id, name, email, password, phone, address, gender, role);
		this.emergencyContact = emergencyContact;
	}

	public String getEmergencyContact() {
		return emergencyContact;
	}

	public void setEmergencyContact(String emergencyContact) {
		this.emergencyContact = emergencyContact;
	}

	
	
	
	
}
