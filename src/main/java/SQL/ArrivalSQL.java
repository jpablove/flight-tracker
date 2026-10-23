package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class ArrivalSQL {
    public static void newArrival(Connection connection, String terminal, String gate, String baggage, int delay, OffsetDateTime scheduled,
                                  OffsetDateTime estimated, OffsetDateTime actual, OffsetDateTime estimatedRunway, OffsetDateTime actualRunway) throws SQLException {
        String sql = "INSERT INTO arrival (terminal, gate, baggage, delay, scheduled, estimated, actual, estimated_runway, actual_runway) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, terminal);
            ps.setString(2, gate);
            ps.setString(3, baggage);
            ps.setInt(4, delay);
            ps.setObject(5, scheduled);
            ps.setObject(6, estimated);
            ps.setObject(7, actual);
            ps.setObject(8, estimatedRunway);
            ps.setObject(9, actualRunway);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into arrival: " + e.getMessage(), e);
        }
    }
}
