package jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class CustomConnector {
    public Connection getConnection(String url) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url);
        }catch (Exception e){
            e.printStackTrace();
        }

        return connection;
    }

    public Connection getConnection(String url, String user, String password){
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
        }catch (Exception e){
            e.printStackTrace();
        }

        return connection;
    }
}
