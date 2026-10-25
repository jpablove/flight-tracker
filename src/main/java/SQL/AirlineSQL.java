package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AirlineSQL {
    private AirlineSQL() {
    }

    public static int newAirline(Connection connection, String iata, String icao, String name) throws SQLException {
        String sql = """
            INSERT INTO airline (iata, icao, name) VALUES (?, ?, ?)
                 ON CONFLICT (iata) DO UPDATE SET icao = EXCLUDED.icao,\s
                                                  name = EXCLUDED.name
            RETURNING airline_id
                \s""";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, iata);
            ps.setObject(2, icao);
            ps.setObject(3, name);
            return getId(ps);
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into airline: " + e.getMessage(), e);
        }
    }

    private static int getId(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into airline: " + e.getMessage(), e);
        }
    }
}
