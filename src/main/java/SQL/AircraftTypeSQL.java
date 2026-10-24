package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AircraftTypeSQL {
    public static void newAircraftType(Connection connection, String iataCode, String icaoCode) throws SQLException {
        String sql = "INSERT INTO aircraft_type (iata_code, icao_code) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, iataCode);
            ps.setString(2, icaoCode);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into aircraft_type: " + e.getMessage(), e);
        }
    }
}
