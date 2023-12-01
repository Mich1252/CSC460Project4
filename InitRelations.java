/* DON'T RUN THIS SCRIPT MANUALLY
 * RUN init_relations.py INSTEAD
 */

import java.io.*;
import java.sql.*;                 // For access to the SQL interaction methods

public class InitRelations {

    public static void main(String[] args) {
        final String oracleURL =   // Magic lectura -> aloe access spell
                        "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

        String username = args[0],    // Oracle DBMS username
               password = args[1];    // Oracle DBMS password

            // load the (Oracle) JDBC driver by initializing its base
            // class, 'oracle.jdbc.OracleDriver'.

        try {

                Class.forName("oracle.jdbc.OracleDriver");

        } catch (ClassNotFoundException e) {

                System.err.println("*** ClassNotFoundException:  "
                    + "Error loading Oracle JDBC driver.  \n"
                    + "\tPerhaps the driver is not on the Classpath?");
                System.exit(-1);

        }

            // make and return a database connection to the user's
            // Oracle database

        Connection dbconn = null;

        try {
                dbconn = DriverManager.getConnection
                               (oracleURL,username,password);

        } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

        }

            // Send statements to the DBMS, and get and display the results
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader("temp1528.txt"));
            String line = reader.readLine();
            while (line != null) {
                // Send the statement to DBMS
                Statement stmt = null;
                ResultSet answer = null;
                try {
                    stmt = dbconn.createStatement();
                    answer = stmt.executeQuery(line);
                    System.out.println("SUCCESS: " + line);
                    stmt.close();  
                } catch (SQLException e) {
                    System.err.println("SQLException:");
                    System.err.println("\t" + line);
                    System.err.println("\tMessage:   " + e.getMessage());
                    System.err.println("\tSQLState:  " + e.getSQLState());
                    System.err.println("\tErrorCode: " + e.getErrorCode());
                    try {
                        dbconn.close();
                    } catch (SQLException r) {
                        System.out.println("Couldn't close connection.");
                    }
                    System.exit(-1);
                }
                // ==========================
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Problem with .txt file.");
        }

        System.out.println("ALL SUCCESS!");

        // Shut down the connection to the DBMS.
        try {
            dbconn.close();
        } catch (SQLException e) {
            System.out.println("Couldn't close connection.");
        }
    }

}

