package SQL;

import java.sql.*;
import java.time.LocalDate;

public class FlightSQL {
    private FlightSQL() {
    }


    public static int newFlight(Connection conn, LocalDate flightDate, String flightStatus, Integer airlineId, String flightNumber,
                                  Integer departureAirportId, Integer arrivalAirportId, Integer aircraftId) throws SQLException {
        String sql = """
                        INSERT INTO flight (flight_date, flight_status, airline_id, flight_number,
                                            departure_airport_id, arrival_airport_id, aircraft_id)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT (flight_date, airline_id, flight_number, departure_airport_id)
                        DO UPDATE SET flight_status      = EXCLUDED.flight_status,
                                      arrival_airport_id = EXCLUDED.arrival_airport_id,
                                      aircraft_id        = COALESCE(EXCLUDED.aircraft_id, flight.aircraft_id),
                                      updated_at         = now()
                        RETURNING flight_id
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, flightDate);
            ps.setString(2, flightStatus);
            ps.setObject(3, airlineId, Types.INTEGER);
            ps.setString(4, flightNumber);
            ps.setInt(5, departureAirportId);
            ps.setInt(6, arrivalAirportId);
            ps.setObject(7, aircraftId);
            return getId(ps);
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into flight: " + e.getMessage(), e);
        }
    }

    private static int getId(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new SQLException("Error trying to insert into flight: " + e.getMessage(), e);
        }
    }
}
