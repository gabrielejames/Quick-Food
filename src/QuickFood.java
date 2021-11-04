/*
This progranme allows the user to enter the details of a food order for delivery. 
It uses customer and restaurant object and writes the details of both for the order to an invoice (invoice.txt)
It writes all customers into two txt files: one capturing all customers and their locations (grouped by location), and the other capturing all customer information.
When a customer name is entered into the programme, it checks whether the customer exists. If the customer exists, their details are added to the customer object.
If the customer name is new, the user enters the attributes of this customer and their details are written to the two customer detail files.
Another file captures each order number with the customer name (grouped alphabetically). 
The programme matches the order to a driver based on the customer location, driver location, and driver delivery load. It updates the driver.txt file whenever a driver is assigned a new delivery.
*/

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Formatter;
import java.util.Collections;

class QuickFood {

  public static void main(String[] args) {

    //creates Scanner object to accept user input
    Scanner input = new Scanner(System.in); //creates a Scanner object to read user input

    //creates an ArrayList holding all the customers objects
    ArrayList < Customer > allCustomers = loadCustomers();

    //initialises the restaurant object for the order
    Restaurant restaurant = new Restaurant();

    //initialises the driver object for the order
    Driver orderDriver = new Driver();

    //creates an ArrayList holding all driver objects
    ArrayList < Driver > allDrivers = loadDrivers();

    String noDrivers = "Sorry! Our drivers are too far away from you to be able to deliver to your location."; //writes to invoice if no drivers match customer location

    ArrayList < Order > allOrders = loadOrders();
    Order newOrder = new Order();

    //Allows the user to input attributes of the customer and restaurant for the order + invoice

    System.out.println("Enter customer name");

    String customerName = input.nextLine();

    //checks ArrayList of existing customers and if there is a match returns the matching customer object 
    Customer customer = findCustomer(customerName, allCustomers);

    //if  the above method returns null, then a new customer object is created and the user inputs their details
    if (customer == null) {

      customer = new Customer();

      customer.total = 0.00;

      customer.name = customerName;
      
      System.out.println("Enter order number");
      newOrder.orderNumber = input.nextInt();
       
      input.nextLine();

      System.out.println("Enter customer address");
      customer.address = input.nextLine();

      System.out.println("Enter customer suburb:");
      customer.suburb = input.nextLine();

      System.out.println("Enter customer location");
      customer.location = input.nextLine();

      System.out.println("Enter customer contact number");
      customer.contactNumber = Integer.parseInt(input.nextLine());
      
      if (!(validateContactNumber(customer.contactNumber))){
    	  System.out.println("Invalid contact number. Please re-enter customer contact number:");
          customer.contactNumber = Integer.parseInt(input.nextLine());
          
      }
      
      System.out.println("Enter customer contact email");
      customer.contactEmail = input.nextLine();

      allCustomers.add(customer);
      outputCustomers(allCustomers);
      customerLocation(allCustomers);

      newOrder.customer = customer;

    } else {
      System.out.println("Enter order number");
      newOrder.orderNumber = input.nextInt();
      newOrder.customer = customer;
      input.nextLine();
    }

    //creates an ArrayList to hold the meals for the customer
    customer.meals = new ArrayList < Meal > ();

    orderDriver = chooseDriver(customer, allDrivers); // matches the driver to the order based on driver and customer location

    //for loop that finds the driver in the driver ArrayList and changes the object to the driver with an additional order  
    for (Driver driver: allDrivers) {

      if (driver.name.equalsIgnoreCase(orderDriver.name)) {
        int driverindex = allDrivers.indexOf(driver);
        allDrivers.set(driverindex, orderDriver);
      }
    }

    //allows the user to enter the attributes of the restaurant object

    System.out.println("Enter restaurant name");
    restaurant.name = input.nextLine();

    System.out.println("Enter restaurant address");
    restaurant.address = input.nextLine();

    System.out.println("Enter restaurant location");
    restaurant.location = input.nextLine();

    System.out.println("Enter restaurant contact number");
    restaurant.contactNumber = input.nextInt();
    input.nextLine();
    
    if (!(validateContactNumber(restaurant.contactNumber))){
  	  System.out.println("Invalid contact number. Please re-enter customer contact number:");
        restaurant.contactNumber = Integer.parseInt(input.nextLine());
        
    }

    //Allows the user to enter meals ordered by the customer, as well as the price and quantity

    String addMeal = "Y";

    //the while loop allows the user to add additional meals

    while (addMeal.equals("Y")) {

      Meal meal = new Meal();

      System.out.println("Enter meal name");
      meal.name = input.nextLine();

      System.out.println("Enter meal unit price");
      meal.price = input.nextDouble();

      System.out.println("Enter quantity of this meal");

      meal.quantity = input.nextInt();

      input.nextLine();

      customer.total += meal.price * meal.quantity;

      customer.meals.add(meal);

      System.out.println("Add another meal? (Y/N)");

      addMeal = input.nextLine().toUpperCase();

    }

    System.out.println("Special instructions for this order:");
    customer.orderInstructions = input.nextLine();

    String invoiceString = stringInvoice(customer, restaurant, orderDriver, newOrder); //turns the order details into a String

    //the try-catch statement writes the order information to a txt file (invoice.txt)

    try {

      Formatter invoice = new Formatter("invoice.txt");

      if (orderDriver.name.equals("")) {

        invoice.format("%s", noDrivers);
        invoice.close();
      } else {
        invoice.format("%s", invoiceString);
        invoice.close();
      }
    } catch (Exception e) {
      System.out.println("Error");
    }

    allOrders.add(newOrder); //adds new order object to ArrayList
    outputDrivers(allDrivers); //updates the driver txt file with matched driver having an extra delivery
    updateOrders(allOrders); //updates customerOrders.txt file with new order

    input.close();
    
  }

  //Methods

  //method to print out the invoice information to the console.
  public static void printInvoice(Customer customer, Restaurant restaurant, Driver driver, Order order) {

    System.out.println("Order number " + order.orderNumber);
    System.out.println("Customer: " + customer.name);
    System.out.println("Email: " + customer.contactEmail);
    System.out.println("Phone number: " + customer.contactNumber);
    System.out.println("Location: " + customer.location);
    System.out.println(customer.suburb);
    System.out.println("");
    System.out.println("You have ordered the following from " + restaurant.name + " in " + restaurant.location + ":");

    //loops through meal objects to print details
    for (Meal i: customer.meals) {
      System.out.println(i.quantity + " X " + i.name + " (R" + i.price + ")");
    }

    System.out.println("Special instructions:" + customer.orderInstructions);

    System.out.println("");
    System.out.println("Total: " + customer.total);
    System.out.println("");

    System.out.println(driver.name + " is closest to the resuarant and so will be delivering your order at:");

    System.out.println(customer.address);
    System.out.println(customer.suburb);

    System.out.println("If you need to contact the restaurant, their number is " + restaurant.contactNumber);

  }
  //method that turns the order information into a single string.
  public static String stringInvoice(Customer customer, Restaurant restaurant, Driver driver, Order order) {

    //loops through meals ArrayList and stores information as a String
    String mealInfo = "";
    for (Meal i: customer.meals) {
      mealInfo += i.quantity + " X " + i.name + " (R" + i.price + ") \n";
    }

    return "Order number: " + order.orderNumber + "\n" +
      "Customer name: " + customer.name + "\n" +
      "Email: " + customer.contactEmail + "\n" +
      "Phone number: " + customer.contactNumber + "\n" +
      "Location: " + customer.location + "\n" +
      "\n" +
      "You have ordered the following from " + restaurant.name + " in " + restaurant.location + ": \n" +
      "\n" +
      mealInfo +
      "Special instructions: " + customer.orderInstructions + "\n" +
      "\n" +
      "Total: R" + customer.total + "\n" +
      "\n" +
      driver.name + "  is nearest to the restaurant and so he will be delivering your order to you at: \n" +
      "\n" +
      customer.address + "\n" +
      customer.suburb + "\n" +
      "\n" +
      "If you need to contact the restaurant, their number is " + restaurant.contactNumber + ".";
  }
  //method that chooses a driver that is in the same location as the customer and has the smallest order load. 
  public static Driver chooseDriver(Customer customer, ArrayList < Driver > allDrivers) {

    Driver chosenDriver = new Driver();

    chosenDriver.name = "";

    for (Driver driver: allDrivers) {

      if (customer.location.equalsIgnoreCase(driver.location)) {

        if (chosenDriver.name.equals("") || chosenDriver.orders > driver.orders) {
          chosenDriver = driver;
        }
      }
    }
    chosenDriver.addDelivery();
    return chosenDriver;
  }
  //method that reads the driver.txt file and uses information to generate an ArrayList of driver objects
  public static ArrayList < Driver > loadDrivers() {

    ArrayList < Driver > listDrivers = new ArrayList < Driver > ();

    try {

      File driverList = new File("drivers.txt");
      Scanner input = new Scanner(driverList);
      input.useDelimiter(", *|\n");

      while (input.hasNext()) {

        Driver newDriver = new Driver();

        newDriver.name = input.next();

        newDriver.location = input.next();

        String orderString = input.next();
        char orders = orderString.charAt(0);
        newDriver.orders = Integer.parseInt(String.valueOf(orders));
        listDrivers.add(newDriver);
      };

      input.close();

    } catch (FileNotFoundException e) {
      System.out.println("Error");
    }
    return listDrivers;

  }
  //method that updates the driver.txt file with the driver objects in an ArrayList.
  public static void outputDrivers(ArrayList < Driver > allDrivers) {
    try {
      Formatter drivers = new Formatter("drivers.txt");
      for (Driver driver: allDrivers) {
        String driverDetails = driver.name + ", " + driver.location + ", " + driver.orders + "\n";
        drivers.format("%s", driverDetails);
      }
      drivers.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block;
      e.printStackTrace();
    }
  }

  //method that reads the customers.txt file and uses information to generate an ArrayList of customer objects 
  public static ArrayList < Customer > loadCustomers() {

    ArrayList < Customer > listCustomers = new ArrayList < Customer > ();

    try {

      File customerList = new File("customers.txt");
      Scanner input = new Scanner(customerList);
      input.useDelimiter(", *|\n");

      while (input.hasNext()) {

        Customer newCustomer = new Customer();

        newCustomer.name = input.next(); // + " ";

        newCustomer.location = input.next();

        String orders = input.next();
        newCustomer.contactNumber = Integer.parseInt(orders);
        newCustomer.contactEmail = input.next();
        newCustomer.address = input.next();
        newCustomer.suburb = input.next();

        listCustomers.add(newCustomer);
      };

      input.close();

    } catch (FileNotFoundException e) {
      System.out.println("Error");
    }
    return listCustomers;

  }
  //method that sorts an ArrayList of customers by location and outputs their information to the customerLocation.txt file
  public static void customerLocation(ArrayList < Customer > allCustomers) {
    try {
      Formatter customerLocation = new Formatter("customerLocation.txt");
      Collections.sort(allCustomers, new Sortbylocation());
      for (Customer customer: allCustomers) {
        String customerDetails = customer.name + ", " + customer.location + "\n";
        customerLocation.format("%s", customerDetails);
      }
      customerLocation.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block;
      e.printStackTrace();
    }
  }
  //method to check if customer already exists
  public static Customer findCustomer(String name, ArrayList < Customer > allCustomers) {
    for (Customer customer: allCustomers) {
      if (customer.name.equalsIgnoreCase(name)) {
        customer.total = 0.00;
        return customer;
      }
    }
    System.out.println(name + " is a new customer. Please enter their details.");
    return null;
  }
  //method to update customer.txt file when a new customer is created
  public static void outputCustomers(ArrayList < Customer > allCustomers) {
    try {
      Formatter customers = new Formatter("customers.txt");
      for (Customer customer: allCustomers) {
        String customerDetails = customer.name + ", " + customer.location + ", " + customer.contactNumber + ", " + customer.contactEmail + ", " + customer.address + ", " + customer.suburb + "\n";
        customers.format("%s", customerDetails);
      }
      customers.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block;
      e.printStackTrace();
    }
  }

  //method that creates an ArrayList of order objects by reading the customerOrders.txt file
  public static ArrayList < Order > loadOrders() {

    ArrayList < Order > listOrders = new ArrayList < Order > ();

    try {

      File orderList = new File("customerOrders.txt");
      Scanner input = new Scanner(orderList);
      input.useDelimiter(", *|\n");

      while (input.hasNext()) {

        Order newOrder = new Order();
        newOrder.customer = new Customer();

        newOrder.customer.name = input.next(); // + " ";

        String orders = input.next();
        newOrder.orderNumber = Integer.parseInt(orders);

        listOrders.add(newOrder);
      };

      input.close();

    } catch (FileNotFoundException e) {
      System.out.println("Error");
    }
    return listOrders;

  }
  //method that sorts an ArrayList of orders alphabetically by customer name and outputs their name and order number to customerOrders.txt
  public static void updateOrders(ArrayList < Order > allOrders) {
    try {
      Formatter customerAlphabetical = new Formatter("customerOrders.txt");
      Collections.sort(allOrders, new Sortbycustomer());

      for (Order order: allOrders) {

        String customerDetails = order.customer.name + ", " + order.orderNumber + "\n";
        customerAlphabetical.format("%s", customerDetails);
      }
      customerAlphabetical.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block;
      e.printStackTrace();
    }
  }
  
  public static boolean validateContactNumber(int contactNumber) {
	int lengthOfNumber = String.valueOf(contactNumber).length();
	
	if (lengthOfNumber == 10) {
	  return true;
	}
	else {
		return false;
	}
  }

}

