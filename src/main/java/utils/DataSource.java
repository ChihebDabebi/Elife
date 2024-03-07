package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private String url = "jdbc:mysql://localhost:3306/testintegration";
    private String user = "root";
    private String passwd = "";

    private Connection connexion;

    private static DataSource instance;

    private DataSource(){
        try {
            connexion = DriverManager.getConnection(url,user,passwd);
            System.out.println("Connected to DB !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static DataSource getInstance(){
        if(instance == null){
            instance = new DataSource();
        }
        return instance;
    }

    public Connection getConnexion(){
        return connexion;
    }
}
