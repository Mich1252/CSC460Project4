/* Author: Michael Beccarelli, Quanwei Lei, JhihYang Wu
 * Course: CSC 460
 * Assignment: Program 4
 * Instructor: Dr. McCann
 * TAs: Zhenyu Qi, Danial Bazmandeh
 * Due date: 2023-12-05
 *
 * 
 *
 * Java version: 16.0.2
 *
 * Before running the program, you may need to run this command below:
 * Add the Oracle JDBC driver to your CLASSPATH environment variable:
 * export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
 *
 * To run the program:
 * "java Program4.java"
 */

 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Scanner;
 
 public class Program4 {
 
	 public static void main(String[] args) {
		 System.out.println("Make sure to run 'export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}'");
		 System.out.println("\n\nWelcome to GYM 460!");
		 // Establish connection to database.
		 Connection dbconn = estabConn();
		 Scanner scanner = new Scanner(System.in);
		 // Infinite loop interactive menu.
		 while (true) {
			 printMenu();
			 if (!takeUserInput(dbconn, scanner))
				 break;
		 }
		 scanner.close();
		 // Close connection to database.
		 closeConn(dbconn);
	 }
 
	 private static void printMenu() {
		 System.out.println();
		 System.out.println("Available Operations:");
		 System.out.println("(a) Add a member");
		 System.out.println("(b) Delete a member");
		 System.out.println("(c) Add a course");
		 System.out.println("(d) Delete a course");
		 System.out.println("(e) Add a package");
		 System.out.println("(f) Delete a package");
		 System.out.println("(g) Update a package");
		 System.out.println("(h) Query1: List names and phone numbers of negative balance members");
		 System.out.println("(i) Query2: Check and see a member\'s class schedule for November");
		 System.out.println("(j) Query3: Check and see all trainer\'s working hours for December");
		 System.out.println("(k) Query4: Check if a Trainer is also a member of GYM 460");
		 System.out.println("(l) Exit");
	 }
 
	 private static boolean takeUserInput(Connection dbconn, Scanner scanner) {
		 System.out.print("Letter: ");
		 // wait for user input
		 while (!scanner.hasNextLine()) {
		 }
		 String letter = scanner.nextLine();
		 // Perform corresponding query.
		 if (letter.equals("a")) {
			 addMember(dbconn);
		 } else if (letter.equals("b")) {
			 deleteMember(dbconn);
		 } else if (letter.equals("c")) {
			 addCourse(dbconn);
		 } else if (letter.equals("d")) {
			 deleteCourse(dbconn);
		 } else if (letter.equals("e")) {
			 addPackage(dbconn);
		 } else if (letter.equals("f")) {
			 deletePackage(dbconn);
		 } else if (letter.equals("g")) {
			 updatePackage(dbconn);
		 } else if (letter.equals("h")) {
			 query1(dbconn);
		 } else if (letter.equals("i")) {
			 query2(dbconn);
		 } else if (letter.equals("j")) {
			 query3(dbconn);
		 } else if (letter.equals("k")) {
			 query4(dbconn);
		 } else if (letter.equals("l")) {
			 // User wants to exit.
			 return false;
		 } else {
			 System.out.println("Invalid letter.");
		 }
		 // buffer here and ask for user input to continue 
		 System.out.println("Press enter to continue...");
		 // take single line input
		 scanner.nextLine();
		 return true;
	 }
 
	 private static void addMember(Connection dbconn) {
		 try {
			 
			 //Get basic information
			 Scanner scanner = new Scanner(System.in);
			 System.out.print("Enter New Member\'s Full Name: ");
			 String name = scanner.nextLine();
			 System.out.print("Enter New Member\'s Phone Number: ");
			 String phoneNum = scanner.nextLine();
 
			 
			 // TODO
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
		 } catch (SQLException e) {
			 System.out.println("Error in addMember.");
			 System.out.println(e);
			 exit();
		 }
		 ;
	 }
 
	 private static void deleteMember(Connection dbconn) {
		 try {
			 // Get basic information
			 Scanner scanner = new Scanner(System.in);
			 System.out.print("Enter Member\'s ID: ");
			 String memberID = scanner.nextLine();
 
			 
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
		 } catch (SQLException e) {
			 System.out.println("Error in deleteMember.");
			 System.out.println(e);
		 }
		 ;
	 }
 
	 private static void addCourse(Connection dbconn) {
		 try {
			 //Get basic information
			 Scanner scanner = new Scanner(System.in);
			 System.out.print("Enter New Course Name: ");
			 String name = scanner.nextLine();
			 System.out.print("Enter New Course\'s Time (military format ex. 1600): ");
			 Integer time = Integer.parseInt(scanner.nextLine());
			 System.out.print("Enter New Course\'s Class Duration in minutes: ");
			 Integer duration = Integer.parseInt(scanner.nextLine());
			 System.out.print("Enter New Course Max enrollment amount: ");
			 Integer maxEnroll = Integer.parseInt(scanner.nextLine());
			 System.out.print("Enter New Course Start Date (MM/DD/YYYY): ");
			 String startDate = scanner.nextLine();
			 System.out.print("Enter New Course End Date (MM/DD/YYYY): ");
			 String endDate = scanner.nextLine();
 
			 
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
		 } catch (SQLException e) {
			 System.out.println("Error in addCourse.");
			 System.out.println(e);
		 }
		 ;
	 }
 
	 private static void deleteCourse(Connection dbconn) {
		 try {
			 //Display all active course
			 System.out.println("Current Active courses");
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
			 
			 // Get basic information for deletion
			 Scanner scanner = new Scanner(System.in);
			 System.out.print("Enter Course\'s ID to remove: ");
			 String courseID = scanner.nextLine();
			 
			 Statement stmt2 = dbconn.createStatement();
			 stmt2.close();
		 } catch (SQLException e) {
			 System.out.println("Error in deleteCourse.");
			 System.out.println(e);
		 }
		 ;
	 }
 
	 private static void addPackage(Connection dbconn) {
		 try {
			 
			 // Get basic information
			 Scanner scanner = new Scanner(System.in);
			 System.out.print("Enter New Package Name: ");
			 String memberID = scanner.nextLine();
 
			 // Print all active courses and add the names to a list
			 ArrayList<String> allCourses = new ArrayList<String>();
			 System.out.println("Current Active courses");
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
			 
			 //allow the admin to continuously add courses to the package if they exist
			 ArrayList<String> courses = new ArrayList<String>();
			 while (true) {
				 System.out.println("Enter the name of a course to add to the package or enter 'DONE': ");
				 String addition = scanner.nextLine();
				 if (addition.toLowerCase().equals("done")) {
					 break;
				 }
				 if (allCourses.contains(addition)) {
					 courses.add(addition);
				 } else {
					 System.out.println("Sorry that course does not exist.");
				 }
			 }
			 
			 //Ask the admin for the price of the package
			 System.out.println("Enter the Package\'s price: ");
			 Float price = Float.parseFloat(scanner.nextLine());
			 
			 //Add all the PackageCourses tuples
			 Statement stmt2 = dbconn.createStatement();
			 stmt2.close();
			 
			 //Add the Package tuple
			 Statement stmt3 = dbconn.createStatement();
			 stmt3.close();
			 
		 } catch (SQLException e) {
			 System.out.println("Error in addPackage.");
			 System.out.println(e);
		 }
		 ;
	 }
 
	 private static void deletePackage(Connection dbconn) {
		 try {
			 //print all the active packages
			 System.out.println("Current Active packages");
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
			 
			 //get basic package info
			 // Get basic information
			 Scanner scanner = new Scanner(System.in);
			 System.out.print("Enter Package ID to delete a package: ");
			 String packageID = scanner.nextLine();
 
			 //Delete the package
			 Statement stmt2 = dbconn.createStatement();
			 stmt2.close();
 
		 } catch (SQLException e) {
			 System.out.println("Error in deletePackage.");
			 System.out.println(e);
		 }
		 ;
	 }
 
	 private static void updatePackage(Connection dbconn) {
		 try {
			 // Query: SELECT name, phone_num FROM members WHERE balance < 0;
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
		 } catch (SQLException e) {
			 System.out.println("Error in updatePackage.");
			 System.out.println(e);
		 }
		 ;
	 }
 
	 private static void query1(Connection dbconn) {
		 try {
			 // TODO
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
		 } catch (SQLException e) {
			 System.out.println("Error in query1.");
			 System.out.println(e);
		 }
		 ;
	 }
 
	 private static void query2(Connection dbconn) {
		 try {
			 // Get the Member's ID that we want to check
			 Scanner scanner = new Scanner(System.in);
			 System.out.print("Enter the member\'s ID please: ");
			 String memberID = scanner.nextLine();
 
			 // Pull the member's schedule information
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
		 } catch (SQLException e) {
			 System.out.println("Error in query2.");
			 System.out.println(e);
		 }
		 ;
	 }
 
	 private static void query3(Connection dbconn) {
		 try {
			 // TODO
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
		 } catch (SQLException e) {
			 System.out.println("Error in query3.");
			 System.out.println(e);
		 }
		 ;
	 }
 
	 private static void query4(Connection dbconn) {
		 try {
			 //Print all the trainers out 
			 System.out.print("All active trainers:");
			 Statement stmt = dbconn.createStatement();
			 stmt.close();
			 
			 Scanner scanner = new Scanner(System.in);
			 System.out.print(" [Enter a Trainer\'s name from the list and see if they have a membership]: ");
			 String trainerName = scanner.nextLine();
			 
			 Statement stmt2 = dbconn.createStatement();
			 stmt2.close();
			 
		 } catch (SQLException e) {
			 System.out.println("Error in query4.");
			 System.out.println(e);
		 }
		 ;
	 }
 
	 /*
	  * Name: estabConn Purpose: Establish a connection with the database using JDBC.
	  * Returns: Connection object for communication with the database.
	  */
	 private static Connection estabConn() {
		 final String oracleURL = // Magic lectura -> aloe access spell
				 "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
 
		 String username = "jhihyangwu", // Oracle DBMS username
				 password = "a4608"; // Oracle DBMS password
 
		 // load the (Oracle) JDBC driver by initializing its base
		 // class, 'oracle.jdbc.OracleDriver'.
 
		 try {
 
			 Class.forName("oracle.jdbc.OracleDriver");
 
		 } catch (ClassNotFoundException e) {
 
			 System.err.println("*** ClassNotFoundException:  " + "Error loading Oracle JDBC driver.  \n"
					 + "\tPerhaps the driver is not on the Classpath?");
			 System.exit(-1);
 
		 }
 
		 // make and return a database connection to the user's
		 // Oracle database
 
		 Connection dbconn = null;
 
		 try {
			 dbconn = DriverManager.getConnection(oracleURL, username, password);
 
		 } catch (SQLException e) {
 
			 System.err.println("*** SQLException:  " + "Could not open JDBC connection.");
			 System.err.println("\tMessage:   " + e.getMessage());
			 System.err.println("\tSQLState:  " + e.getSQLState());
			 System.err.println("\tErrorCode: " + e.getErrorCode());
			 System.exit(-1);
 
		 }
 
		 return dbconn;
 
	 }
 
	 /*
	  * Name: closeConn Purpose: Closes the connection to the database. Parameters:
	  * conn - in - Connection object to the database to close.
	  */
	 private static void closeConn(Connection conn) {
		 try {
			 conn.close();
		 } catch (SQLException e) {
			 System.out.println("Failed to close oracle connection.");
		 }
	 }
 
	 /***
	  * Name: Exit Purpose: Exit the program Parameters: None
	  */
	 private static void exit() {
		 System.exit(1);
	 }
 
 }
 
