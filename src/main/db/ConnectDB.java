package main.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    public static Connection getConnect() {
        Connection conn = null;
        String url = "jdbc:sqlserver://LAPTOP-TO5HNKIC:1433;database=LTM;";
        String username = "sa";
        String password = "0376645847";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, username, password);
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    public static void main(String[] args) {
        if(getConnect() != null) {
            System.out.println("OK");
        }
    }
}
