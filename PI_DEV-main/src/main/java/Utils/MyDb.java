package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDb {
    private String url = "jdbc:mysql://localhost:3306/pi_dev";
    private String user = "root";
    private String password = "";
    private static Connection conn;
    private static MyDb instance;

    public static MyDb getInstance() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pi_dev", "root", "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new MyDb();
    }

    public static void setInstance(Connection conn) {

    }

    public Connection getConn() {
        return conn;
    }

    private MyDb() {
        try {
            this.conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(" ___ Connection Failed ___");
        }


    }


}