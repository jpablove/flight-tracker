package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LiveSQL {
    private LiveSQL() {
    }

    public static void newLive(Connection connection, String iata, String icao, String name, String timezone) throws SQLException {
        String sql = "INSERT INTO live (iata, icao, name, timezone) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, iata);
            ps.setString(2, icao);
            ps.setString(3, name);
            ps.setString(4, timezone);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into live: " + e.getMessage(), e);
        }
    }
}
