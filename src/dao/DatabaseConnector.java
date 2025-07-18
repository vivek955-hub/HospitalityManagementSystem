package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    // Move these to a config file or environment variables in real projects
    private static final String URL      = "jdbc:mysql://localhost:3306/hospitality_db?useSSL=false";
    private static final String USER     = "root";
    private static final String PASSWORD = "Vivek@3777";

    /** Returns a new connection or null if something goes wrong. */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the MySQL driver (optional with modern JDBC, but harmless)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open the connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Cannot connect to database: " + e.getMessage());
        }
        return conn;
    }

    /** Closes the connection quietly. */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /** Quick test. */
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Connection successful!");
            close(conn);
        } else {
            System.out.println("Connection failed.");
        }
    }
}
