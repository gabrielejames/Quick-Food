import java.util.ArrayList;
public class Customer {
    
    //attributes
    int orderNumber;
    String name;
    int contactNumber;
    String address;
    String suburb;
    String location;
    String contactEmail;
    ArrayList<Meal> meals;
    String orderInstructions;
    Double total;

    //methods

    //method to print customer details

    public void printDetails() {
        System.out.println("Customer details:");
        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
        System.out.println("Location: " + location);
        System.out.println("Contact email: " + contactEmail);
    }

	@Override
	public String toString() {
		return "Customer /n Order Number=" + orderNumber + ", Name=" + name + ", contactNumber=" + contactNumber
				+ ", Address=" + address + ", Suburb=" + suburb + ", Location=" + location + ", Contact Email="
				+ contactEmail + ", meals=" + meals + ", Order Instructions=" + orderInstructions + ", Total=" + total
				+ "]";
	}
    
    
    
}