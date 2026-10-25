package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class DepartureSQL {
    public static int newDeparture(Connection connection, long flightId, String terminal, String gate, Integer delay, OffsetDateTime scheduled,
                                    OffsetDateTime estimated, OffsetDateTime actual, OffsetDateTime estimatedRunway, OffsetDateTime actualRunway) throws SQLException {
        String sql = """
                INSERT INTO departure (flight_id, terminal, gate, delay, scheduled, estimated, actual, estimated_runway, actual_runway) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT (flight_id) 
                        DO UPDATE SET   terminal      = EXCLUDED.terminal,\n 
                                        gate = EXCLUDED.gate,\n 
                                        delay        = EXCLUDED.delay,\n 
                                        scheduled         = EXCLUDED.scheduled,\n
                                        estimated = EXCLUDED.estimated,\n
                                        actual = EXCLUDED.actual,\n
                                        estimated_runway = EXCLUDED.estimated_runway,\n
                                        actual_runway = EXCLUDED.actual_runway 
                        RETURNING flight_id
                    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, flightId);
            ps.setString(2, terminal);
            ps.setString(3, gate);
            ps.setObject(4, delay);
            ps.setObject(5, scheduled);
            ps.setObject(6, estimated);
            ps.setObject(7, actual);
            ps.setObject(8, estimatedRunway);
            ps.setObject(9, actualRunway);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            } catch (SQLException e) {
                throw new SQLException("Error trying to insert into departure: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into departure: " + e.getMessage(), e);
        }
    }
}
