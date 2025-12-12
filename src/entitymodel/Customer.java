package entitymodel;

public class Customer extends User {

	private double balance;

	public Customer(String id, String name, String email, String password, String phone, String address, String gender,
			String role, double balance) {
		super(id, name, email, password, phone, address, gender, role);
		this.balance = balance;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	
    
}
