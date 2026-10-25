package SQL;

import ApiResponse.Departure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class DepartureSQL {
    private DepartureSQL() {
    }

    public static int newDeparture(Connection connection, long flightId, String terminal, String gate, Integer delay, OffsetDateTime scheduled,
                                    OffsetDateTime estimated, OffsetDateTime actual, OffsetDateTime estimatedRunway, OffsetDateTime actualRunway) throws SQLException {
        String sql = """
                INSERT INTO departure (flight_id, terminal, gate, delay, scheduled, estimated, actual, estimated_runway, actual_runway) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT (flight_id) \s
                        DO UPDATE SET   terminal      = EXCLUDED.terminal,
                                        gate = EXCLUDED.gate,\s
                                        delay        = EXCLUDED.delay,\s
                                        scheduled         = EXCLUDED.scheduled,
                                        estimated = EXCLUDED.estimated,
                                        actual = EXCLUDED.actual,
                                        estimated_runway = EXCLUDED.estimated_runway,
                                        actual_runway = EXCLUDED.actual_runway\s
                        RETURNING flight_id
                   \s""";

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
            return getId(ps);
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into departure: " + e.getMessage(), e);
        }
    }

    private static int getId(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into departure: " + e.getMessage(), e);
        }
    }

    public static void createDeparture(Connection c, long flightId, Departure departure) throws SQLException {
        OffsetDateTime scheduled = parseOdt(departure.getScheduled());
        OffsetDateTime estimated = parseOdt(departure.getEstimated());
        OffsetDateTime actual = parseOdt(departure.getActual());
        OffsetDateTime estimatedRunway = parseOdt(departure.getEstimated_runway());
        OffsetDateTime actualRunway = parseOdt(departure.getActual_runway());
        try {
            DepartureSQL.newDeparture(c, flightId, departure.getTerminal(), departure.getGate(), departure.getDelay(),
                    scheduled, estimated, actual, estimatedRunway, actualRunway);
        } catch (SQLException e) {
            throw new SQLException("Error trying to create a new departure: " + e.getMessage());
        }
    }

    private static OffsetDateTime parseOdt(String s) {
        return (s == null || s.isBlank()) ? null : OffsetDateTime.parse(s);
    }
}
