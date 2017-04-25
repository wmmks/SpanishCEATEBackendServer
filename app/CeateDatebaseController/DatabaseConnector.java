package CeateDatebaseController;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by roye on 2017/4/24.
 */
public class DatabaseConnector {
    private String dbHost;
    private String dbName;
    private String userName;
    private String password;
    public DatabaseConnector ()
    {
        Properties properties=new Properties();
        try {
            properties.load(new FileInputStream("databaseConfiguration.properties"));
            dbHost=properties.getProperty("dbHost");
            dbName=properties.getProperty("dbName");
            userName=properties.getProperty("userName");
            password=properties.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(dbHost+dbName,userName,password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
