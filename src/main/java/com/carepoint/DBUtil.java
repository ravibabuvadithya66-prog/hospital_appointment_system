package com.carepoint;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public final class DBUtil {
 private static final String URL="jdbc:mysql://localhost:3306/hospital_appointment_system?useSSL=false&serverTimezone=UTC";
 private static final String USER="root";
 private static final String PASSWORD="Ravi@122"; // Change to your MySQL password if needed.
 private DBUtil(){}
 public static Connection getConnection() throws SQLException { return DriverManager.getConnection(URL,USER,PASSWORD); }
}
