public class Driver {

    String name;
    String location;
    int orders;
    
public void addDelivery() {
	this.orders ++;
}

@Override
public String toString() {
	return "Driver name=" + name + ", Location=" + location + ", Orders=" + orders + "]";
}



}

// maybe make a toString() method that makes a string like the one that will be saved to a file