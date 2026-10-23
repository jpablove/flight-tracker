package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CodeshareSQL {
    public static void newCodeshare(Connection connection, String operatingFlightNumber) throws SQLException {
        String sql = "INSERT INTO codeshare (operating_flight_number) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, operatingFlightNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into codeshare: " + e.getMessage(), e);
        }
    }
}
