// a comparator  interface to sort customers by location

import java.util.Comparator;

public class Sortbylocation implements Comparator<Customer> {
	
	public int compare(Customer a, Customer b) {
		return a.location.compareTo(b.location);
	}
}