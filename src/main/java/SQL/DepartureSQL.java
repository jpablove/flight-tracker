package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class DepartureSQL {
    public static void newDeparture(Connection connection, String terminal, String gate, int delay, OffsetDateTime scheduled,
                                    OffsetDateTime estimated, OffsetDateTime actual, OffsetDateTime estimatedRunway, OffsetDateTime actualRunway) throws SQLException {
        String sql = "INSERT INTO departure (terminal, gate, delay, scheduled, estimated, actual, estimated_runway, actual_runway) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, terminal);
            ps.setString(2, gate);
            ps.setInt(3, delay);
            ps.setObject(4, scheduled);
            ps.setObject(5, estimated);
            ps.setObject(6, actual);
            ps.setObject(7, estimatedRunway);
            ps.setObject(8, actualRunway);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into departure: " + e.getMessage(), e);
        }
    }
}
