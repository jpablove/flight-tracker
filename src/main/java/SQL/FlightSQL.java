package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class FlightSQL {

    public static void newFlight (Connection conn, LocalDate flight_date, String flight_status, Integer airlineId, String flightNumber,
                                  Integer departureAirportId, Integer arrivalAirportId, Integer aircraftId) throws SQLException {
        String sql = "INSERT INTO flight (flight_date, flight_status, airline_id, flight_number,\n" +
                "                    departure_airport_id, arrival_airport_id, aircraft_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, flight_date);
            ps.setString(2, flight_status);
            ps.setInt(3, airlineId);
            ps.setString(4, flightNumber);
            ps.setInt(5, departureAirportId);
            ps.setInt(6, arrivalAirportId);
            ps.setInt(7, aircraftId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into flight: " + e.getMessage(), e);
        }
    }


}
