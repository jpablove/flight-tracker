import ApiResponse.*;
import SQL.AircraftSQL;
import SQL.AirlineSQL;
import SQL.AirportSQL;
import SQL.FlightSQL;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import static SQL.ArrivalSQL.createArrival;
import static SQL.DepartureSQL.createDeparture;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.connection();
        Connection c = databaseConnection.getConnection();

        String apiKey = getApiKey();
        String url = "https://api.aviationstack.com/v1/flights?access_key=" + apiKey;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info(() -> "Status code: " + response.statusCode());
            logger.info(() -> "Response: " + response.body());

            ObjectMapper objectMapper = new ObjectMapper();
            FlightResponse flightResponse = objectMapper.readValue(response.body(), FlightResponse.class);
            List<Flight> flightList = flightResponse.getData();

            insertIntoDB(c, flightList);
        } finally {
            databaseConnection.closeConnection(c);
        }
    }

    private static String getApiKey() throws IOException {
        Properties config = new Properties();
        String apiKey;

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
            apiKey = config.getProperty("API_KEY");
        } catch (IOException e) {
            throw new IOException("Configuration file not loaded: " + e.getMessage());
        }
        return apiKey;
    }

    private static void insertIntoDB(Connection c, List<Flight> flightList) throws SQLException {
        for (Flight f : flightList) {
            try {
                c.setAutoCommit(false);

                Airline airline = f.getAirline();
                Departure departure = f.getDeparture();
                Arrival arrival = f.getArrival();
                Aircraft aircraft = f.getAircraft();

                String flightDate = f.getFlight_date();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(flightDate, formatter);

                String flightStatus = f.getFlight_status();

                Integer airlineId = null;
                if (airline.getIcao() == null && airline.getIata() == null) {
                    logger.warning(() -> "Airline ignored because it does not have iata, nor icao code: " + airline.getName());
                } else {
                    airlineId = AirlineSQL.newAirline(c, airline.getIata(), airline.getIcao(), airline.getName());
                }

                Integer aircraftId = null;
                if (aircraft != null) {
                    aircraftId = AircraftSQL.newAircraft(c, aircraft.getRegistration(), aircraft.getIcao24());
                }
                String flightNumber = String.valueOf(f.getFlightDetalis().getNumber());

                Integer departureAirportId = AirportSQL.newAirport(c, departure.getIata(), departure.getIcao(), departure.getAirport(), departure.getTimezone());
                Integer arrivalAirportId = AirportSQL.newAirport(c, arrival.getIata(), arrival.getIcao(), arrival.getAirport(), arrival.getTimezone());

                long flightId = FlightSQL.newFlight(c, date, flightStatus, airlineId, flightNumber, departureAirportId, arrivalAirportId, aircraftId);

                createDeparture(c, flightId, departure);
                createArrival(c, flightId, arrival);

                c.commit();
            } catch (Exception e) {
                handleRollback(c);
                logger.warning("Error with the flight " + f.getFlight_date() + " " + f.getFlightDetalis().getNumber() + " " + f.getFlightDetalis().getIata() + ": " + e.getMessage());
            }
        }
    }

    private static void handleRollback(Connection c) throws SQLException {
        try {
            c.rollback();
        } catch (SQLException e) {
            throw new SQLException("Error trying to rollback: " + e.getMessage());
        }
    }
}