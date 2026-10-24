package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AircraftSQL {

    public static int newAircraft(Connection connection, String registration, String icao24) throws SQLException {
        String sql = "INSERT INTO aircraft (registration, icao24) VALUES (?, ?) " +
                        "ON CONFLICT (icao24) " +
                        "DO UPDATE SET registration = EXCLUDED.registration " +
                        "RETURNING aircraft_id";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, registration);
            ps.setObject(2, icao24);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            } catch (SQLException e) {
                throw new SQLException("Error trying to insert into aircraft: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into aircraft: " + e.getMessage(), e);
        }
    }

}
