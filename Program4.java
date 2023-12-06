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
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.sql.ResultSetMetaData;

public class Program4 {

	private static String todayDate = "12/01/2023";

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
		System.out.println("(k) Query4: List the names and numbers of all trainers that teach a specific member.");
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
	 * - in - ResultSet object containing the results of a query. NOTE - any
	 * ResultSet given MUST have a statement created using a format of: Statement
	 * stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	 * ResultSet.CONCUR_READ_ONLY);
	 */
	private static int printResults(ResultSet answer, boolean print) {
		System.out.println();
		int rows = 0;
		// call the query
		try {
			if (answer != null) {
				// print out the columns we need to dynamically expand the line size with the
				// actual
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

				if (print == false) {
					return rows;
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
			System.err.println("*** SQLException:  " + "Could not fetch query results.");
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
			// git the only value from the result set and increment it by 1
			int newMemberID = 0;
			if (IDresults.next()) {
				newMemberID = IDresults.getInt("MAX(MemberID)") + 1;
			}
			IDstmt.close();

			// append quotation around the name and phone number to make it a string
			name = "\'" + name + "\'";
			phoneNum = "\'" + phoneNum + "\'";

			// use insert to add a member
			Statement stmt = dbconn.createStatement();
			String query = "insert into Member values (" + newMemberID + ", " + name + ", " + phoneNum + ", 0, 0, 0)";
			stmt.executeQuery(query);
			stmt.close();

			System.out.println("Please select a package:");
			// Present user with packages to select.
			Statement packagesStmt = dbconn.createStatement();
			String packagesQuery = "SELECT * FROM Package";
			ResultSet packagesRes = packagesStmt.executeQuery(packagesQuery);
			while (packagesRes.next()) {
				int packID = packagesRes.getInt("PackageID");
				String packName = packagesRes.getString("Name");
				double price = packagesRes.getFloat("Price");
				// check if this package has any courses that are full
				Statement fullcheck = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				String fullCheckquery = "SELECT Course.CourseID, Name, StartTime, StartDate, EndDate, Duration, CurrentEnrolled, MaxEnrolled, DaysOfTheWeek FROM PackageCourses, Course WHERE PackageCourses.CourseID=Course.CourseID AND PackageCourses.PackageID="
						+ packID + " AND CurrentEnrolled>=MaxEnrolled";
				ResultSet fullSet = fullcheck.executeQuery(fullCheckquery);
				int rows = printResults(fullSet, false);
				// if there are any courses that are full, then we do not display the package
				if (rows > 0) {
					continue;
				}

                // If package has no active courses, don't display package
                Statement emptyPack = dbconn.createStatement();
				String emptyPackQuery = "SELECT COUNT(*) FROM PackageCourses, Course WHERE PackageCourses.CourseID=Course.CourseID AND PackageCourses.PackageID="
						+ packID + " AND EndDate>TO_DATE('" + todayDate + "', 'MM/DD/YYYY')";
                ResultSet emptyPackRes = emptyPack.executeQuery(emptyPackQuery);
                emptyPackRes.next();
                if (emptyPackRes.getInt("COUNT(*)") == 0) {
                    // Zero active courses, don't display this package
                    emptyPack.close();
                    continue;
                }
                emptyPack.close();

				System.out.println("PackageID: " + packID + "\t" + packName + " $" + price);
				// Display courses in package
				Statement whichCourses = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				String whichCoursesQuery = "SELECT Course.CourseID, Name, StartTime, StartDate, EndDate, Duration, CurrentEnrolled, MaxEnrolled, DaysOfTheWeek FROM PackageCourses, Course WHERE PackageCourses.CourseID=Course.CourseID AND PackageCourses.PackageID="
						+ packID + " AND EndDate>TO_DATE('" + todayDate + "', 'MM/DD/YYYY')";
				ResultSet whichCoursesRes = whichCourses.executeQuery(whichCoursesQuery);
				printResults(whichCoursesRes, true);
			}
			packagesStmt.close();

			// Ask user to select a packageID.
			System.out.print("PackageID: ");
			int chosenPack = Integer.valueOf(scanner.nextLine());

			// Actually enroll member in package chosenPack (update CurrentEnrolled,
			// MemberCourse Table, MemberEnrolledTable, etc).
			// enroll into memberenrolled with give memberID and packageID
			Statement enroll = dbconn.createStatement();
			query = "insert into MemberEnrolled values (" + newMemberID + ", " + chosenPack + ")";
			enroll.executeQuery(query);
			enroll.close();

			// using the packageID, get all of the courseID that is in the package
			Statement courseID = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			query = "SELECT CourseID FROM PackageCourses WHERE PackageID = " + chosenPack;
			ResultSet courseIDresults = courseID.executeQuery(query);
			// array for courseID
			int[] courseIDs = new int[100];
			int i = 0;
			// get the courseID
			while (courseIDresults.next()) {
				courseIDs[i] = courseIDresults.getInt("CourseID");
				i++;
			}

			// now we need to enroll the member into the courses that are in the package
			for (int j = 0; j < i; j++) {
				Statement enrollcourse = dbconn.createStatement();
				query = "insert into MemberCourse values (" + newMemberID + ", " + courseIDs[j] + ")";
				enrollcourse.executeQuery(query);
				enrollcourse.close();

                // increase CurrentEnrolled for courses this new member signed up for
                enrollcourse = dbconn.createStatement();
                query = "UPDATE Course SET CurrentEnrolled = CurrentEnrolled + 1 WHERE CourseID = " + courseIDs[j];
                enrollcourse.executeQuery(query);
                enrollcourse.close();
			}

            // Depending on what membership type they have, increase the amount spent accordingly.
            //     Get discount
            Statement discount = dbconn.createStatement();
            String discountQuery = "SELECT Discount, AmountSpent FROM Member, MembershipInfo WHERE Member.MembershipTypeID=MembershipInfo.MembershipTypeID AND MemberID=" + newMemberID;
            ResultSet discountRes = discount.executeQuery(discountQuery);
            discountRes.next();
            int discountRate = discountRes.getInt("Discount");
            float oldAmountSpent = discountRes.getFloat("AmountSpent");
            float percentPay = (100 - discountRate) / 100;
            discount.close();
            //     Get price
            Statement price = dbconn.createStatement();
            query = "SELECT Price FROM Package WHERE PackageID="+chosenPack;
            ResultSet priceRes = price.executeQuery(query);
            priceRes.next();
            float pricePack = priceRes.getFloat("Price");
            price.close();
            //     Update AmountSpent
            float amountSpent = percentPay * pricePack; // How much member is paying for this package
            float newAmountSpent = oldAmountSpent + amountSpent; // New amount spent of the member
            Statement newAmtStmt = dbconn.createStatement();
            query = "UPDATE Member SET AmountSpent = " + newAmountSpent + " WHERE MemberID=" + newMemberID;
            newAmtStmt.executeQuery(query);
            newAmtStmt.close();
            
            // Create transaction tuple for this new course purchase
            //     get new TransactionID
            Statement nextTransID = dbconn.createStatement();
            query = "SELECT MAX(TransactionID) FROM Transaction";
            ResultSet nextTransIDRes = nextTransID.executeQuery(query);
            nextTransIDRes.next();
            int newTranID = nextTransIDRes.getInt("MAX(TransactionID)") + 1;
            //     insert new Transaction tuple
            Statement newTrans = dbconn.createStatement();
            query = "insert into Transaction values ("+newTranID+", "+newMemberID+", "+amountSpent+", TO_DATE('"+todayDate+"', 'MM/DD/YYYY'), 'Purchase', 'N')";
            newTrans.executeQuery(query);
            newTrans.close();

            // Update member's membership type depending on newAmountSpent
            //     Get correct membership type
            Statement newMemType = dbconn.createStatement();
            query = "SELECT MembershipTypeID FROM MembershipInfo WHERE "+newAmountSpent+">MinSpend ORDER BY MinSpend DESC";
            ResultSet newMemTypeRes = newMemType.executeQuery(query);
            newMemTypeRes.next();
            int newMemTypeID = newMemTypeRes.getInt("MembershipTypeID");
            newMemType.close();
            //     Save potentially new membership type
            newMemType = dbconn.createStatement();
            query = "UPDATE Member SET MembershipTypeID = " + newMemTypeID + " WHERE MemberID = " + newMemberID;
            newMemType.executeQuery(query);
            newMemType.close();
		} catch (SQLException e) {
			System.out.println("Error in addMember.");
			System.out.println(e);
		}
	}

	private static void deleteMember(Connection dbconn) {
		try {
			// Get basic information
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter Member\'s ID: ");
			int memberID = Integer.valueOf(scanner.nextLine());

			// First, examine transaction history for unpaid amounts.
			// we look at table Transaction for all Paid='N' with our memberID
			Statement negativecheck = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT * FROM Transaction WHERE MemberID = " + memberID + "AND Paid = 'N'";
			// if there are any unpaid transaction, the member has a negative balance
			ResultSet results = negativecheck.executeQuery(query);
			int rows = printResults(results, true);
			if (rows > 0) {
				// print out number of unpaid
				System.out.println("\nNumber of unpaid transactions: " + rows);
				System.out.println("This member has a negative balance, please pay the balance first.");
				return;
			}

			// purge member from transaction table
			Statement purgemember = dbconn.createStatement();
			query = "DELETE FROM Transaction WHERE MemberID = " + memberID;
			purgemember.executeQuery(query);
			purgemember.close();

			// Verify if the member has any unreturned equipment.
			// The member exists and has a non negative balance, now we check for equipment
			// rentals
			Statement equipmentcheck = dbconn.createStatement();
			// check for borrowed and equipment that is not returned
			query = "SELECT COUNT(*) FROM Borrowed WHERE MemberID = " + memberID
					+ " AND isLost = 'N' AND ReturnTime IS NULL";
			results = equipmentcheck.executeQuery(query);
			results.next();
			int numUnreturned = results.getInt("COUNT(*)");
			equipmentcheck.close();

			// if the user indeed has equipment rentals unreturned, then we need to update
			// them.
			if (numUnreturned > 0) {
				Statement quantityequip = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				// start by getting all of the equipmentID and quantityborrowed that is NOT lost
				// yet
				query = "SELECT EquipmentID, QuantityBorrowed FROM Borrowed WHERE MemberID = " + memberID
						+ " AND isLost = 'N' AND ReturnTime IS NULL";
				results = quantityequip.executeQuery(query);
				// array for equipmentID and quantity borrowed
				int[] equipmentID = new int[numUnreturned];
				int[] quantityBorrowed = new int[numUnreturned];
				int i = 0;
				// get the equipmentID and quantity borrowed
				while (results.next()) {
					equipmentID[i] = results.getInt("EquipmentID");
					quantityBorrowed[i] = results.getInt("QuantityBorrowed");
					i++;
				}
				quantityequip.close();
				// now write queries to update the equipment table, subtracting the quantity
				// borrowed from the quantity available
				for (int j = 0; j < numUnreturned; j++) {
					Statement updateequip = dbconn.createStatement();
					query = "UPDATE Equipment SET MaxQuantity = MaxQuantity - " + quantityBorrowed[j]
							+ " WHERE EquipmentID = " + equipmentID[j];
					updateequip.executeQuery(query);
					updateequip.close();
				}
				// now we need to update the borrowed table to set islost to Y
				Statement updateborrowed = dbconn.createStatement();
				query = "UPDATE Borrowed SET isLost = 'Y' WHERE MemberID = " + memberID + "AND ReturnTime IS NULL";
				updateborrowed.executeQuery(query);
				updateborrowed.close();

				// change the memberID of lost borrowed to -1, so that it is not associated with
				// any member
				Statement updatelost = dbconn.createStatement();
				query = "UPDATE Borrowed SET MemberID = -1 WHERE MemberID = " + memberID;
				updatelost.executeQuery(query);
				updatelost.close();
			}

			// query for finding courses of member under MemberCourse, then reducing those
			// courses' CurrentEnrolled by 1
			Statement coursecheck = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			query = "SELECT CourseID FROM MemberCourse WHERE MemberID = " + memberID;
			results = coursecheck.executeQuery(query);
			// array for courseID
			int[] courseIDs = new int[100];
			int i = 0;
			// get the courseID
			while (results.next()) {
				courseIDs[i] = results.getInt("CourseID");
				i++;
			}
			coursecheck.close();
			// now we need to update the course table to reduce the current enrolled by 1
			for (int j = 0; j < i; j++) {
				Statement updatecourse = dbconn.createStatement();
				query = "UPDATE Course SET CurrentEnrolled = CurrentEnrolled - 1 WHERE CourseID = " + courseIDs[j];
				updatecourse.executeQuery(query);
				updatecourse.close();
			}

			// Unenroll member from any courses he/she was in.
			Statement unenroll = dbconn.createStatement();
			query = "DELETE FROM MemberCourse WHERE MemberID = " + memberID;
			unenroll.executeQuery(query);
			unenroll.close();

			// Now we need to delete the member from the member table
			Statement deletemember = dbconn.createStatement();
			query = "DELETE FROM Member WHERE MemberID = " + memberID;
			deletemember.executeQuery(query);
			deletemember.close();
			System.out.println("Member " + memberID + " has been deleted.\n");
		} catch (SQLException e) {
			System.out.println("Error in deleteMember.");
			System.out.println(e);
		}
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

			// Get MAX(CourseID) first.
			Statement maxStmt = dbconn.createStatement();
			String maxQuery = "SELECT MAX(CourseID) FROM Course";
			ResultSet maxQueryRes = maxStmt.executeQuery(maxQuery);
			maxQueryRes.next();
			int newCourseID = maxQueryRes.getInt("MAX(CourseID)") + 1;
			maxStmt.close();

			// Create new Course in database.
			Statement stmt = dbconn.createStatement();
			name = "'" + name + "'";
			startDate = "TO_DATE('" + startDate + "', 'MM/DD/YYYY')";
			endDate = "TO_DATE('" + endDate + "', 'MM/DD/YYYY')";
			days = "'" + days + "'";
			String query = "insert into Course values (" + newCourseID + ", " + name + ", " + trainer + ", " + time
					+ ", " + startDate + ", " + endDate + ", " + duration + ", " + maxEnroll + ", " + "0, " + days
					+ ")";
			stmt.executeQuery(query);
			stmt.close();

			System.out.println("Course created successfully.");
		} catch (SQLException e) {
			System.out.println("Error in addCourse.");
			System.out.println(e);
		}
	}

	private static void deleteCourse(Connection dbconn) {
		try {
			// Get basic information for deletion
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter Course's ID to remove: ");
			int courseID = Integer.valueOf(scanner.nextLine());

			// Check if it is active and has people enrolled.
			Statement needToNotify = dbconn.createStatement();
			String needToNotifyQuery = "SELECT COUNT(*) FROM Course WHERE CourseID=" + courseID
					+ " AND EndDate>TO_DATE('" + todayDate + "', 'MM/DD/YYYY') AND CurrentEnrolled>0";
			ResultSet needToNotifyRes = needToNotify.executeQuery(needToNotifyQuery);
			needToNotifyRes.next();

			if (needToNotifyRes.getInt("COUNT(*)") > 0) {
				// Need to notify first before deleting course.
				System.out.println("Please notify these members about Course deletion:");
				Statement notification = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				String notifQuery = "SELECT Name, PhoneNum FROM MemberCourse, Member WHERE MemberCourse.MemberID=Member.MemberID AND CourseID="
						+ courseID;
				ResultSet notifRes = notification.executeQuery(notifQuery);
				printResults(notifRes, true);
				notification.close();
			}
			needToNotify.close();

			// Delete the course.
			Statement deletecourse = dbconn.createStatement();
			String delQuery = "DELETE FROM Course WHERE CourseID = " + courseID;
			deletecourse.executeQuery(delQuery);
			deletecourse.close();
			// Delete MemberCourse tuples with deleted courseID.
			deletecourse = dbconn.createStatement();
			delQuery = "DELETE FROM MemberCourse WHERE CourseID = " + courseID;
			deletecourse.executeQuery(delQuery);
			deletecourse.close();

			System.out.println("Course " + courseID + " has been deleted.");
		} catch (SQLException e) {
			System.out.println("Error in deleteCourse.");
			System.out.println(e);
		}
	}

	/**
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
			// Get package name
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter New Package Name: ");
			String name = scanner.nextLine();

			// Print all active courses and add the names to a list
			System.out.println("Current Active courses");
			Statement stmt = dbconn.createStatement();
			String query = "SELECT CourseID, Name FROM Course WHERE EndDate>TO_DATE('" + todayDate + "', 'MM/DD/YYYY')";
			ResultSet results = stmt.executeQuery(query);

			// Add every ID from the results to all courses
			ArrayList<Integer> allCourses = new ArrayList<>();
			ArrayList<String> allNames = new ArrayList<>();
			while (results.next()) {
				allCourses.add(results.getInt("CourseID"));
				allNames.add(results.getString("Name"));
			}
			stmt.close();
			// Display active courses.
			for (int i = 0; i < allCourses.size(); i++) {
				System.out.println(allCourses.get(i) + " | " + allNames.get(i));
			}

			// allow the admin to continuously add courses to the package if they exist
			ArrayList<Integer> courses = new ArrayList<Integer>();
			while (true) {
				System.out.println("Enter the ID of a course to add to the package or enter 'DONE': ");
				String addition = scanner.nextLine();
				if (addition.toLowerCase().equals("done")) {
					break;
				}
				// If the id exists in all courses, then add it to the courses array
				if (allCourses.contains(Integer.parseInt(addition))) {
					courses.add(Integer.parseInt(addition));
				} else {
					System.out.println("Sorry that course does not exist.");
				}
			}

			// Ask the admin for the price of the package
			System.out.println("Enter the Package\'s price: ");
			Float price = Float.parseFloat(scanner.nextLine());

			// Get new PackageID
			Statement newPackID = dbconn.createStatement();
			query = "SELECT MAX(PackageID) FROM Package";
			ResultSet newPackIDRes = newPackID.executeQuery(query);
			newPackIDRes.next();
			int newID = newPackIDRes.getInt("MAX(PackageID)") + 1;
			newPackID.close();

			// Add the Package tuple
			Statement stmt3 = dbconn.createStatement();
			query = "insert into Package values (" + newID + ", '" + name + "', " + price + ")";
			stmt3.executeQuery(query);
			stmt3.close();

			// Add all the PackageCourses tuples using a loop
			for (int ids : courses) {
				Statement stmt2 = dbconn.createStatement();
				query = "insert into PackageCourses values (" + newID + ", " + ids + ")";
				stmt2.executeQuery(query);
				stmt2.close();
			}

			System.out.println("Package created.");
		} catch (SQLException e) {
			System.out.println("Error in addPackage.");
			System.out.println(e);
		}
	}

	private static void deletePackage(Connection dbconn) {
		try {
			// Display all packages.
			Statement packagesStmt = dbconn.createStatement();
			String packagesQuery = "SELECT * FROM Package";
			ResultSet packagesRes = packagesStmt.executeQuery(packagesQuery);
			while (packagesRes.next()) {
				int packID = packagesRes.getInt("PackageID");
				String packName = packagesRes.getString("Name");
				double price = packagesRes.getFloat("Price");
				System.out.println("PackageID: " + packID + "\t" + packName + " $" + price);
				// Present which courses are in this package.
				Statement whichCourses = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				String whichCoursesQuery = "SELECT Course.CourseID, Name, StartTime, StartDate, EndDate, Duration, CurrentEnrolled, MaxEnrolled, DaysOfTheWeek FROM PackageCourses, Course WHERE PackageCourses.CourseID=Course.CourseID AND PackageCourses.PackageID="
						+ packID;
				ResultSet whichCoursesRes = whichCourses.executeQuery(whichCoursesQuery);
				printResults(whichCoursesRes, true);
			}
			packagesStmt.close();

			// Ask user to select a packageID.
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter Package ID to delete a package: ");
			int chosenPack = Integer.valueOf(scanner.nextLine());

			// Delete the package
			Statement delPack = dbconn.createStatement();
			String delPackQuery = "DELETE FROM Package WHERE PackageID=" + chosenPack;
			delPack.executeQuery(delPackQuery);
			delPack.close();

			// Delete tuples from PackageCourses
			Statement delPackCour = dbconn.createStatement();
			String delPackCourQuery = "DELETE FROM PackageCourses WHERE PackageID=" + chosenPack;
			delPackCour.executeQuery(delPackCourQuery);
			delPackCour.close();

			System.out.println("Package deleted.");
		} catch (SQLException e) {
			System.out.println("Error in deletePackage.");
			System.out.println(e);
		}
	}

	private static void updatePackage(Connection dbconn) {
		try {
			Statement stmt = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// present a list of all course packages, so that the user can edit the package.
			String query = "SELECT * FROM Package";
			ResultSet results = stmt.executeQuery(query);
			// print results
			printResults(results, true);
			System.out.printf("Please select a package to update: ");
			Scanner scanner = new Scanner(System.in);
			int packageID = Integer.valueOf(scanner.nextLine());
			// print out the package that the user selected
			Statement pack = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			query = "SELECT * FROM Package WHERE PackageID = " + packageID;
			results = pack.executeQuery(query);
			printResults(results, true);

			// show options to delete or add courses
			System.out.print("Would you like to add or delete courses from this package, or change price? (A/D)(P): ");
			String choice = scanner.nextLine();
			while (!choice.toLowerCase().equals("a") && !choice.toLowerCase().equals("d")
					&& !choice.toLowerCase().equals("p")) {
				System.out.print("Invalid choice, please enter A or D or P: ");
				choice = scanner.nextLine();
			}
			Statement add = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// if the user wants to add courses display courses that are not in the package
			if (choice.toLowerCase().equals("a")) {
				// display courses that are not in the package
				query = "SELECT CourseID, Course.Name FROM Course WHERE CourseID NOT IN (SELECT CourseID FROM PackageCourses WHERE PackageID = "
						+ packageID + ")";
				results = add.executeQuery(query);
				printResults(results, true);
				// ask the user to select a course to add
				System.out.print("Please select a course to add: ");
				int courseID = Integer.valueOf(scanner.nextLine());
				// add the course to the package
				Statement addcourse = dbconn.createStatement();
				query = "INSERT INTO PackageCourses VALUES (" + packageID + ", " + courseID + ")";
				addcourse.executeQuery(query);
				addcourse.close();

				System.out.println("Package updated.");

				// display the courses within the package
				Statement updated = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				query = "SELECT Course.CourseId, Course.Name FROM PackageCourses JOIN Package ON PackageCourses.PackageID = Package.PackageID JOIN Course ON PackageCourses.CourseID = Course.CourseID WHERE Package.PackageID = "
						+ packageID;
				results = updated.executeQuery(query);
				printResults(results, true);
			} else if (choice.toLowerCase().equals("d")) {
				// display courses that are in the package able to be deleted
				query = "SELECT CourseID, Course.Name FROM Course WHERE CourseID IN (SELECT CourseID FROM PackageCourses WHERE PackageID = "
						+ packageID + ")";
				results = add.executeQuery(query);
				printResults(results, true);
				// ask the user to select a course to delete
				System.out.print("Please select a course to delete: ");
				int courseID = Integer.valueOf(scanner.nextLine());
				// delete the course from the package
				Statement deletecourse = dbconn.createStatement();
				query = "DELETE FROM PackageCourses WHERE PackageID = " + packageID + " AND CourseID = " + courseID;
				deletecourse.executeQuery(query);
				deletecourse.close();
				// display the courses within the package
				Statement updated = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				query = "SELECT Course.CourseId, Course.Name FROM PackageCourses JOIN Package ON PackageCourses.PackageID = Package.PackageID JOIN Course ON PackageCourses.CourseID = Course.CourseID WHERE Package.PackageID = "
						+ packageID;
				results = updated.executeQuery(query);
				printResults(results, true);
			} else {
				// change the price of the package
				System.out.print("Please enter the new price: ");
				double price = Double.valueOf(scanner.nextLine());
				Statement changeprice = dbconn.createStatement();
				query = "UPDATE Package SET Price = " + price + " WHERE PackageID = " + packageID;
				changeprice.executeQuery(query);
				changeprice.close();

				System.out.println("Package updated.");

				// display the update package
				Statement updated = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				query = "SELECT * FROM Package WHERE PackageID = " + packageID;
				results = updated.executeQuery(query);
				printResults(results, true);
			}

			// would you like to update another package?
			System.out.print("Would you like to update another package? (Y/N): ");
			String another = scanner.nextLine();
			while (!another.toLowerCase().equals("y") && !another.toLowerCase().equals("n")) {
				System.out.print("Invalid choice, please enter Y or N: ");
				another = scanner.nextLine();
			}
			if (another.toLowerCase().equals("y")) {
				updatePackage(dbconn);
			}

			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error in updatePackage.");
			System.out.println(e);
		}
		;
	}

	private static void query1(Connection dbconn) {
		try {
			// Get a list of members.
			List<Integer> memberIDs = new ArrayList<>();
			List<String> memberNames = new ArrayList<>();
			List<String> memberPhones = new ArrayList<>();
			Statement allMemb = dbconn.createStatement();
			String allMembQuery = "SELECT MemberID, Name, PhoneNum FROM Member";
			ResultSet allMembRes = allMemb.executeQuery(allMembQuery);
			while (allMembRes.next()) {
				memberIDs.add(allMembRes.getInt("MemberID"));
				memberNames.add(allMembRes.getString("Name"));
				memberPhones.add(allMembRes.getString("PhoneNum"));
			}

			// Check if each member has negative balance.
			for (int i = 0; i < memberIDs.size(); i++) {
				int memberID = memberIDs.get(i);
				// we look at table Transaction for all Paid='N' with our memberID
				Statement negativecheck = dbconn.createStatement();
				String query = "SELECT COUNT(*) FROM Transaction WHERE MemberID = " + memberID + "AND Paid = 'N'";
				ResultSet results = negativecheck.executeQuery(query);
				results.next();
				int numUnpaid = results.getInt("COUNT(*)");
				if (numUnpaid > 0) {
					System.out.println(memberNames.get(i) + " | " + memberPhones.get(i));
				}
			}
		} catch (SQLException e) {
			System.out.println("Error in query1.");
			System.out.println(e);
		}
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

			// Display results
			System.out.println("===============================================================");
			System.out.println("SCHEDULE");
			System.out.println("===============================================================");
			System.out.println("CourseName -- CourseStartDate -- CourseEndDate -- DaysOfTheWeek");
			System.out.println("===============================================================");
			while (results.next()) {
				System.out.println(results.getString("CourseName") + " -- " + results.getString("CourseStartDate")
						+ " -- " + results.getString("CourseEndDate") + " -- " + results.getString("DaysOfTheWeek"));
			}

		} catch (SQLException e) {
			System.out.println("Error in query2.");
			System.out.println(e);
		}
	}

	private static void query3(Connection dbconn) {
		try {

			// We will need to get the hours worked for each trainer in december and store
			// it
			HashMap<String, Float> timesheet = new HashMap<String, Float>();

			Statement stmt = dbconn.createStatement();
			String query = "SELECT * FROM Trainer T JOIN Course C ON T.TrainerID = C.TrainerID "
					+ "WHERE (EXTRACT(MONTH FROM C.StartDate) <= 12 " + "AND EXTRACT(MONTH FROM C.EndDate) <= 12)"
					+ "AND EXTRACT(YEAR FROM C.StartDate) <=  EXTRACT(YEAR FROM C.EndDate)";
			ResultSet results = stmt.executeQuery(query);

			// process the results
			while (results.next()) {
				String trainerID = results.getString("trainerID");
				Integer duration = results.getInt("duration");
				Date startDate = results.getDate("StartDate");
				Date endDate = results.getDate("EndDate");
				Integer daysAWeek = results.getString("DaysOfTheWeek").length();

				// Java.sql.Date has a ton of Deprecated methods for getting the day, month and
				// year so we get it manually
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String[] startingDate = dateFormat.format(startDate).split("-");
				String[] endingDate = dateFormat.format(endDate).split("-");
				Integer startMonth = Integer.parseInt(startingDate[1]);
				Integer startDay = Integer.parseInt(startingDate[2]);
				Integer endMonth = Integer.parseInt(endingDate[1]);
				Integer endDay = Integer.parseInt(endingDate[2]);

				// We now need to process the number of hours
				// NOTE: This assumed that december starts on a sunday and here are 4 months in
				// december and that there are 31 days in december
				float hours = 0;

				if (startMonth < 12 && endMonth < 12) {
					hours = (float) (duration * daysAWeek * 4) / 60;
				} else if (startMonth < 12 && endMonth == 12) {
					hours = (float) ((endDay / 4) * daysAWeek * duration) / 60;
				} else if (startMonth == 12 && endMonth < 12) {
					hours = (float) (((31 - startDay) / 4) * daysAWeek * duration) / 60;
				} else if (startMonth == 12 && endMonth == 12) {
					hours = (float) (((endDay - startDay) / 4) * daysAWeek * duration) / 60;
				}

				// put into map
				if (timesheet.containsKey(trainerID)) {
					timesheet.put(trainerID, timesheet.get(trainerID) + hours);
				} else {
					timesheet.put(trainerID, hours);
				}
			}

			// Display results:
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			System.out.println("===================");
			System.out.println("TrainerID -- Hours:");
			System.out.println("===================");
			for (String entry : timesheet.keySet()) {
				String timeWorked = decimalFormat.format(timesheet.get(entry));
				System.out.println(entry + " -- " + timeWorked);
			}

		} catch (SQLException e) {
			System.out.println("Error in query3.");
			System.out.println(e);
		}
	}

	private static void query4(Connection dbconn) {
		try {
			// Get the member's ID to check
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter the Members ID: ");
			String memberID = scanner.nextLine();

			// Query: SELECT DISTINCT T.name, T.phonenum
			// FROM Trainer T
			// JOIN Course C ON T.TrainerID = C.TrainerID
			// JOIN MemberCourse MC on MC.CourseID = C.CourseID
			// WHERE MC.MemberID = memberID

			Statement stmt = dbconn.createStatement();
			String query = "SELECT DISTINCT T.Name, T.PhoneNum " + "FROM Trainer T "
					+ "JOIN Course C ON T.TrainerID = C.TrainerID "
					+ "JOIN MemberCourse MC ON C.CourseID = MC.CourseID " + "WHERE MC.MemberID = " + memberID;
			ResultSet results = stmt.executeQuery(query);

			// Display the results
			System.out.println("============================");
			System.out.println("Trainer Name -- Phone Number");
			System.out.println("============================");
			while (results.next()) {
				System.out.println(results.getString("name") + " -- " + results.getString("phonenum"));
			}

		} catch (SQLException e) {
			System.out.println("Error in query4.");
			System.out.println(e);
		}
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
			exit(dbconn);

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
	private static void exit(Connection conn) {
		closeConn(conn);
		System.exit(1);
	}

}
