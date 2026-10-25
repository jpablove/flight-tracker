package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AirportSQL {
    private AirportSQL() {
    }

    public static int newAirport(Connection connection, String iata, String icao, String name, String timezone) throws SQLException {
        String sql = "INSERT INTO airport (iata, icao, name, timezone) VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT (iata) " +
                "DO UPDATE SET  icao = EXCLUDED.icao, " +
                "               name = EXCLUDED.name, " +
                    "               timezone = EXCLUDED.timezone " +
                    "RETURNING airport_id";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, iata);
            ps.setObject(2, icao);
            ps.setObject(3, name);
            ps.setObject(4, timezone);
            return getId(ps);
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into airport: " + e.getMessage(), e);
        }
    }

    private static int getId(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into airport: " + e.getMessage(), e);
        }
    }
}
