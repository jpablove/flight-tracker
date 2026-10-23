package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AircraftSQL {

    public static void newAircraft(Connection connection, String registration, String icao24) throws SQLException {
        String sql = "INSERT INTO aircraft (registration, icao24) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, registration);
            ps.setString(2, icao24);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into aircraft: " + e.getMessage(), e);
        }
    }

}
