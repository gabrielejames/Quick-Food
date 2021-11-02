// a comparator  interface to sort orders by customer name alphabetically

import java.util.Comparator;
public class Sortbycustomer implements Comparator<Order> {
	
		public int compare(Order a, Order b) {
			return a.customer.name.compareTo(b.customer.name);
		}
	}

