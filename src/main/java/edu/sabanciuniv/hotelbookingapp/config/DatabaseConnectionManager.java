package edu.sabanciuniv.hotelbookingapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@Component
public class DatabaseConnectionManager {
    private static DatabaseConnectionManager instance;
    private Connection connection;
    
    private DatabaseConnectionManager() {}
    
    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:mysql://localhost:3306/hotel_booking";
            connection = DriverManager.getConnection(url, "root", "");
        }
        return connection;
    }
}
