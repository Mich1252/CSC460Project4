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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.ResultSetMetaData;

public class Program4 {
	
	/*
	 * Argument for an ID counter
	 */
	private static Integer IDcounter = 0;
	
	/**
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		System.out.println(
				"Make sure to run 'export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}'");
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
		System.out.println("(k) Query4: Check who teaches most courses for a specific member");
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

	/*
	 * Name: printResults Purpose: Print the results of a query. Parameters: answer
	 * - in - ResultSet object containing the results of a query.
	 * NOTE - any ResultSet given MUST have a statement created using a format of:
	 * Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	 */
	private static int printResults(ResultSet answer) {
		System.out.println();
		int rows = 0;
		// call the query
		try {
			if (answer != null) {
				// print out the columns we need to dynamically expand the line size with the actual
				// information within
				ResultSetMetaData answermetadata = answer.getMetaData();
				
				int[] columnLength = new int[answermetadata.getColumnCount()];
				int lenSum = 0;
				// find the maximum length of each column
				while (answer.next()) {
					// replace the maximum values if found, also check the length of the column name
					for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
						// if a datetime object is detected, then take it in as a datetime object
						if (answer.getString(i).length() > columnLength[i - 1]) {
							columnLength[i - 1] = answer.getString(i).length();
						}
						if (answermetadata.getColumnName(i).length() > columnLength[i - 1]) {
							columnLength[i - 1] = answermetadata.getColumnName(i).length();
						}
					}
				}

				// get the sum of the column lengths
				for (int i = 0; i < columnLength.length; i++) {
					lenSum += columnLength[i];
				}

				// if 0 lenSum, then there are no results
				if (lenSum == 0) {
					return 0;
				}

				int length = 0;
				// reset answer to the beginning
				answer.beforeFirst();

				// print out column names
				for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
					System.out.print(answermetadata.getColumnName(i));
					length = answermetadata.getColumnName(i).length();
					// print out the spaces to make the columns line up
					for (int j = 0; j < columnLength[i - 1] - length; j++) {
						System.out.print(" ");
					}
					if (i != answermetadata.getColumnCount()) {
						System.out.print("  |  ");
					}
				}

				System.out.println();
				// print out a divider line made up of dashes
				for (int i = 0; i < columnLength.length; i++) {
					for (int j = 0; j < columnLength[i]; j++) {
						System.out.print("-");
					}
					if (i != columnLength.length - 1) {
						System.out.print("--|--");
					}
				}

				// print out the rows and their respective data
				while (answer.next()) {
					System.out.println();
					for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
						System.out.print(answer.getString(i));
						length = answer.getString(i).length();
						// print out the spaces to make the columns line up
						for (int j = 0; j < columnLength[i - 1] - length; j++) {
							System.out.print(" ");
						}
						if (i != answermetadata.getColumnCount()) {
							System.out.print("  |  ");
						}
					}
					rows++;
				}
				System.out.println("\n");
			}
			return rows;
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
		return rows;
	}

	/***
	 * Name: addMember
	 * 
	 * Purpose: Adds a new member to GYM460.
	 * 
	 * Return: None
	 * 
	 * @param dbconn
	 */
	private static void addMember(Connection dbconn) {
		try {

			// Get basic information
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter New Member\'s Full Name: ");
			String name = scanner.nextLine();
			System.out.print("Enter New Member\'s Phone Number (XXX-XXX-XXXX): ");
			String phoneNum = scanner.nextLine();

			// get the highest ID, and increment it by 1
			Statement IDstmt = dbconn.createStatement();
			String IDquery = "SELECT MAX(MemberID) FROM Member";
			ResultSet IDresults = IDstmt.executeQuery(IDquery);
			// get the only value from the result set and increment it by 1
			if (IDresults.next()) {
				IDcounter = IDresults.getInt("MAX(MemberID)") + 1;
			}
			IDstmt.close();

			// append quotation around the name and phone number to make it a string
			name = "\'" + name + "\'";
			phoneNum = "\'" + phoneNum + "\'";
			
			System.out.println("IDcounter: " + IDcounter);
			// use insert to add a member
			Statement stmt = dbconn.createStatement();
			String query = "insert into Member values ("+IDcounter+", "+name+", "+phoneNum+", 0, 0, 0)";
			System.out.println(query);
			stmt.executeQuery(query);
			stmt.close();
<<<<<<< Updated upstream
			
=======
>>>>>>> Stashed changes
		} catch (SQLException e) {
			System.out.println("Error in addMember.");
			System.out.println(e);
			exit();
		}
	}

	private static void deleteMember(Connection dbconn) {
		try {
			// Get basic information
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter Member\'s ID: ");
			String memberID = scanner.nextLine();

<<<<<<< Updated upstream
			// First check if the member has a negative balance
			Statement negativecheck = dbconn.createStatement();
			String query = "SELECT Balance FROM Member WHERE MemberID = " + memberID;
			// if the returned balance is negative, then the member has a negative balance,
			// return an error
			ResultSet results = negativecheck.executeQuery(query);
			if (results.getInt("Balance") < 0) {
				System.out.println("Error: Member has a negative balance. Cannot delete member.");
				return;
			}

			Statement stmt = dbconn.createStatement();
			stmt.close();
=======
			// First check if the member has a negative balance, we look at table Transaction for all Paid='N' with our memberID
			Statement negativecheck = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT * FROM Transaction WHERE MemberID = " + memberID + "AND Paid = \'N\'";
			// if the returned balance is negative, then the member has a negative balance,
			// return an error
			ResultSet results = negativecheck.executeQuery(query);

			int rows = printResults(results);
			if (rows > 0) {
				// print out number of unpaid
				System.out.println("\nNumber of unpaid transactions: " + rows + "\n");
				System.out.println("This member has a negative balance, please pay the balance first.");
				return;
			}

			// The member exists and has a non negative balance, now we check for equipment rentals
			Statement equipmentcheck = dbconn.createStatement();
			// check for borrowed and equipment that is not returned
			query = "SELECT COUNT(*) FROM Borrowed WHERE MemberID = " + memberID + " AND ISLOST = \'N\'";
			results = equipmentcheck.executeQuery(query);
			
			int numEquipmentRentals = 0;
			// if there next
			if (results.next()) {
				numEquipmentRentals = results.getInt("COUNT(*)");
			}

			equipmentcheck.close();
			
			// if the user indeed has equipment rentals, then we need to update them.
			if (numEquipmentRentals > 0) {
				Statement quantityequip = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				// start by getting all of the equipmentID and quantityborrowed that is NOT lost
				query = "SELECT EquipmentID, QuantityBorrowed FROM Borrowed WHERE MemberID = " + memberID + " AND ISLOST = \'N\'";
				results = quantityequip.executeQuery(query);
				// array for equipmentID and quantity borrowed
				int[] equipmentID = new int[numEquipmentRentals];
				int[] quantityBorrowed = new int[numEquipmentRentals];
				int i = 0;
				// get the equipmentID and quantity borrowed
				while (results.next()) {
					equipmentID[i] = results.getInt("EquipmentID");
					quantityBorrowed[i] = results.getInt("QuantityBorrowed");
					i++;
				}
				quantityequip.close();
				// now write queries to update the equipment table, subtracting the quantity borrowed from the quantity available
				for (int j = 0; j < numEquipmentRentals; j++) {
					Statement updateequip = dbconn.createStatement();
					query = "UPDATE Equipment SET Available = Available - " + quantityBorrowed[j] + " WHERE EquipmentID = " + equipmentID[j];
					updateequip.executeQuery(query);
					updateequip.close();
				}

				// now we need to update the borrowed table to set islost to Y
				Statement updateborrowed = dbconn.createStatement();
				query = "UPDATE Borrowed SET ISLOST = \'Y\' WHERE MemberID = " + memberID;
				updateborrowed.executeQuery(query);
				updateborrowed.close();
			}

			// Now we need to delete the member from the member table
			Statement deletemember = dbconn.createStatement();
			query = "DELETE FROM Member WHERE MemberID = " + memberID;
			deletemember.executeQuery(query);
			deletemember.close();
			
			System.out.println("Member " + memberID + " has been deleted.\n");

>>>>>>> Stashed changes
		} catch (SQLException e) {
			System.out.println("Error in deleteMember.");
			System.out.println(e);
		}
		;
	}

	private static void addCourse(Connection dbconn) {
		try {
			// Get basic information about the course
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter New Course Name: ");
			String name = scanner.nextLine();
			System.out.print("Enter New Course Trainer ID: ");
			Integer trainer = Integer.parseInt(scanner.nextLine());
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
			System.out.print("What Days of the Week Will the Course Run (MTWRF): ");
			String days = scanner.nextLine();


			Statement stmt = dbconn.createStatement();
			
			String query = "insert into Course values ("
					+ IDcounter+", "
					+ name+", "
					+ trainer+", "
					+ time+", "
					+ startDate+", "
					+ endDate+", "
					+ duration+", "
					+ maxEnroll+", "
					+ "0, "
					+ days+")";
			stmt.executeQuery(query);
			stmt.close();
			
			//Increment IDcounter and close the scanner
			IDcounter++;
			scanner.close();
			
		} catch (SQLException e) {
			System.out.println("Error in addCourse.");
			System.out.println(e);
		}
		;
	}

	private static void deleteCourse(Connection dbconn) {
		try {
			// Display all active course
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

	/***
	 * Name: addPackage
	 * 
	 * Purpose: Adds a new package to GYM460.
	 * 
	 * Return: None
	 * 
	 * @param dbconn
	 */
	private static void addPackage(Connection dbconn) {
		try {
			
			//Get package name
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter New Package Name: ");
			String name = scanner.nextLine();

			
			// Print all active courses and add the names to a list
			ArrayList<Integer> allCourses = new ArrayList<Integer>();
			System.out.println("Current Active courses");
			Statement stmt = dbconn.createStatement();
			String query = "SELECT CourseID, Name FROM Course";
			ResultSet results = stmt.executeQuery(query);
			stmt.close();
			
			//Add every ID from the results to all courses
			while (results.next()) {
				allCourses.add(results.getInt("CourseID"));
			}
			//display results
			System.out.println(results);

			
			// allow the admin to continuously add courses to the package if they exist
			ArrayList<Integer> courses = new ArrayList<Integer>();
			while (true) {
				System.out.println("Enter the ID of a course to add to the package or enter 'DONE': ");
				String addition = scanner.nextLine();
				if (addition.toLowerCase().equals("done")) {
					break;
				}
				//If the id exists in all courses, then add it to the courses array
				if (allCourses.contains(Integer.parseInt(addition))) {
					courses.add(Integer.parseInt(addition));
				} else {
					System.out.println("Sorry that course does not exist.");
				}
			}

			
			// Ask the admin for the price of the package
			System.out.println("Enter the Package\'s price: ");
			Float price = Float.parseFloat(scanner.nextLine());

			
			// Add the Package tuple
			Statement stmt3 = dbconn.createStatement();
			query = "insert into Package values ("+IDcounter+", "+name+", "+price+")";
			stmt.executeQuery(query);
			stmt3.close();
			
			// Add all the PackageCourses tuples using a loop
			for (int ids : courses) {
				Statement stmt2 = dbconn.createStatement();
				query = "insert into PackageCourses values ("+IDcounter+", "+ids+")";
				stmt.executeQuery(query);
				stmt2.close();
			}
			
			//Increment IDcounter and close the scanner
			IDcounter++;
			scanner.close();

		} catch (SQLException e) {
			System.out.println("Error in addPackage.");
			System.out.println(e);
		}
	}

	private static void deletePackage(Connection dbconn) {
		try {
			// print all the active packages
			System.out.println("Current Active packages");
			Statement stmt = dbconn.createStatement();
			stmt.close();

			// get basic package info
			// Get basic information
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter Package ID to delete a package: ");
			String packageID = scanner.nextLine();

			// Delete the package
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
			// Get negative balance members
			// Query: SELECT name, phone_num FROM members WHERE balance < 0;
			Statement stmt = dbconn.createStatement();
			String query = "SELECT name, phone_num FROM members WHERE balance < 0";
			ResultSet results = stmt.executeQuery(query);
			stmt.close();

			// Print results
			System.out.println(results);

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

			// We are joining 3 tables here
			// Query: SELECT C.Name AS CourseName, C.StartDate AS CourseStartDate, C.EndDate
			// AS CourseEndDate, C.DaysOfTheWeek
			// FROM Member M JOIN MemberCourse MC ON M.MemberID = MC.MemberID JOIN Course C
			// ON MC.CourseID = C.CourseID
			// WHERE M.MemberID = your_member_id AND (EXTRACT(MONTH FROM C.StartDate) <= 11
			// AND EXTRACT(MONTH FROM C.EndDate) >= 11);
			// Pull the member's schedule information for November
			Statement stmt = dbconn.createStatement();
			String query = "SELECT C.Name AS CourseName, C.StartDate AS CourseStartDate, C.EndDate AS CourseEndDate, C.DaysOfTheWeek "
					+ "FROM Member M JOIN MemberCourse MC ON M.MemberID = MC.MemberID JOIN Course C ON MC.CourseID = C.CourseID "
					+ "WHERE M.MemberID = " + memberID
					+ " AND (EXTRACT(MONTH FROM C.StartDate) <= 11 AND EXTRACT(MONTH FROM C.EndDate) >= 11)";
			ResultSet results = stmt.executeQuery(query);
			stmt.close();

			// Display results
			System.out.println(results);

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
			//Get the member's ID to check
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter the Members ID: ");
			String memberID = scanner.nextLine();

			Statement stmt = dbconn.createStatement();
			stmt.close();


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
