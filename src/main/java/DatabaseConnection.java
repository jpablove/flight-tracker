import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String DB_SERVER = "localhost";
    private static final int DB_PORT = 5432;
    private static final String DB_NAME = "postgres";
    private static final String DB_USER = "postgres";
    private static String DB_PASS;
    private static Connection conn;

    public DatabaseConnection() {}

    public void connection () throws Exception {
        getDB_PASS();
        Class.forName("org.postgresql.Driver");

        String url = "jdbc:postgresql://" + DB_SERVER + ":" + DB_PORT + "/" + DB_NAME;
        try {
            conn = DriverManager.getConnection(url, DB_USER, DB_PASS);
            if (conn != null) System.out.println("CONNECTED TO DATABASE");
        } catch (SQLException e) {
            System.err.printf("Connection error: %s\n", e.getMessage());
        }
    }

    private static void getDB_PASS() {
        Properties config = new Properties();

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
            DB_PASS = config.getProperty("DB_PASSWORD");
        } catch (IOException e) {
            System.err.printf("Configuration file not loaded: %s\n", e.getMessage());
        }
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.printf("Error closing the connection: %s\n", e.getMessage());
        }
    }

    public Connection getConnection() {
        return this.conn;
    }
}
