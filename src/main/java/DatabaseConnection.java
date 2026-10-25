import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final String DB_SERVER = "localhost";
    private static final int DB_PORT = 5432;
    private static final String DB_NAME = "postgres";
    private static final String DB_USER = "postgres";
    private static String DB_PASS;
    private static Connection conn;
    Logger logger = Logger.getLogger(getClass().getName());

    public DatabaseConnection() {}

    private static void getdbPass(Logger logger) {
        Properties config = new Properties();

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
            DB_PASS = config.getProperty("DB_PASSWORD");
        } catch (IOException e) {
            logger.info("Configuration file not loaded: " + e.getMessage());
        }
    }

    public void connection() throws SQLException {
        getdbPass(logger);
        String url = "jdbc:postgresql://" + DB_SERVER + ":" + DB_PORT + "/" + DB_NAME;

        try {
            conn = DriverManager.getConnection(url, DB_USER, DB_PASS);
            if (conn != null) logger.info("CONNECTED TO DATABASE");
        } catch (SQLException e) {
            logger.info("Connection error: " + e.getMessage());
        }
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.info("Error closing the connection: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return DatabaseConnection.conn;
    }
}
