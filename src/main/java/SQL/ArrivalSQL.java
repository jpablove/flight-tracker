package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class ArrivalSQL {
    public static int newArrival(Connection connection, long flightId, String terminal, String gate, String baggage, Integer delay, OffsetDateTime scheduled,
                                  OffsetDateTime estimated, OffsetDateTime actual, OffsetDateTime estimatedRunway, OffsetDateTime actualRunway) throws SQLException {
        String sql = """
                        INSERT INTO arrival (flight_id, terminal, gate, baggage, delay, scheduled, estimated, actual, estimated_runway, actual_runway) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) 
                        ON CONFLICT (flight_id) 
                        DO UPDATE SET scheduled = EXCLUDED.scheduled 
                        RETURNING flight_id
                    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, flightId);
            ps.setString(2, terminal);
            ps.setString(3, gate);
            ps.setString(4, baggage);
            ps.setObject(5, delay);
            ps.setObject(6, scheduled);
            ps.setObject(7, estimated);
            ps.setObject(8, actual);
            ps.setObject(9, estimatedRunway);
            ps.setObject(10, actualRunway);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            } catch (SQLException e) {
                throw new SQLException("Error trying to insert into arrival: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into arrival: " + e.getMessage(), e);
        }
    }
}
