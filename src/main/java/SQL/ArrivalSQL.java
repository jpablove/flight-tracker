package SQL;

import ApiResponse.Arrival;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class ArrivalSQL {
    private ArrivalSQL() {
    }

    public static int newArrival(Connection connection, long flightId, String terminal, String gate, String baggage, Integer delay, OffsetDateTime scheduled,
                                  OffsetDateTime estimated, OffsetDateTime actual, OffsetDateTime estimatedRunway, OffsetDateTime actualRunway) throws SQLException {
        String sql = """
                     INSERT INTO arrival (flight_id, terminal, gate, baggage, delay, scheduled, estimated, actual, estimated_runway, actual_runway) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\s
                     ON CONFLICT (flight_id)\s
                     DO UPDATE SET scheduled = EXCLUDED.scheduled\s
                        RETURNING flight_id
                \s""";

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
            return getId(ps);
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into arrival: " + e.getMessage(), e);
        }
    }

    private static int getId(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into arrival: " + e.getMessage(), e);
        }
    }

    public static void createArrival(Connection c, long flightId, Arrival arrival) throws SQLException {
        OffsetDateTime arrivalScheduled = parseOdt(arrival.getScheduled());
        OffsetDateTime arrivalEstimated = parseOdt(arrival.getEstimated());
        OffsetDateTime arrivalActual = parseOdt(arrival.getActual());
        OffsetDateTime arrivalEstimatedRunway = parseOdt(arrival.getEstimated_runway());
        OffsetDateTime arrivalActualRunway = parseOdt(arrival.getActual_runway());
        try {
            ArrivalSQL.newArrival(c, flightId, arrival.getTerminal(), arrival.getGate(), arrival.getBaggage(), arrival.getDelay(),
                    arrivalScheduled, arrivalEstimated, arrivalActual, arrivalEstimatedRunway, arrivalActualRunway);
        } catch (SQLException e) {
            throw new SQLException("Error trying to create a new arrival: " + e.getMessage());
        }
    }

    private static OffsetDateTime parseOdt(String s) {
        return (s == null || s.isBlank()) ? null : OffsetDateTime.parse(s);
    }
}
