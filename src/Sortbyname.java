// a comparator  interface to sort customers alphabetically
import java.util.Comparator;

public class Sortbyname implements Comparator<Customer> {
	
	public int compare(Customer a, Customer b) {
		return a.name.compareTo(b.name);
	}
}