package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AirportSQL {
    public static int newAirport(Connection connection, String iata, String icao, String name, String timezone) throws SQLException {
        String sql = "INSERT INTO airport (iata, icao, name, timezone) VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT (iata) " +
                    "DO UPDATE SET name = EXCLUDED.name " +
                    "RETURNING airport_id";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, iata);
            ps.setString(2, icao);
            ps.setObject(3, name);
            ps.setString(4, timezone);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            } catch (SQLException e) {
                throw new SQLException("Error trying to insert into airport: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into airport: " + e.getMessage(), e);
        }
    }
}
