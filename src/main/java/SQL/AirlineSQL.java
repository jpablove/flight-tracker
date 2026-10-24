package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AirlineSQL {
    public static int newAirline(Connection connection, String iata, String icao, String name) throws SQLException {
        String sql = """
            INSERT INTO airline (iata, icao, name) VALUES (?, ?, ?)
            ON CONFLICT (iata) DO UPDATE SET name = airline.name
            RETURNING airline_id
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, iata);
            ps.setString(2, icao);
            ps.setString(3, name);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            } catch (SQLException e) {
                throw new SQLException("Error trying to insert into airline: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into airline: " + e.getMessage(), e);
        }
    }
}
