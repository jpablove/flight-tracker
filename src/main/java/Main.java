import ApiResponse.*;
import SQL.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.connection();
        Connection c = databaseConnection.getConnection();

        String apiKey = getApiKey();


        String URL = "https://api.aviationstack.com/v1/flights?access_key=" + apiKey;


        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.printf("Status code: %d\n", response.statusCode());
            System.out.printf("Response: %s\n", response.body());

            ObjectMapper objectMapper = new ObjectMapper();
            FlightResponse flightResponse = objectMapper.readValue(response.body(), FlightResponse.class);
            List<Flight> flightList = flightResponse.getData();


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
                    Integer airlineId = AirlineSQL.newAirline(c, airline.getIata(), airline.getIcao(), airline.getName());
                    Integer aircraftId = null;
                    if (aircraft != null) {
                        aircraftId = AircraftSQL.newAircraft(c, aircraft.getRegistration(), aircraft.getIcao24());
                    }
                    String flightNumber = String.valueOf(f.getFlightDetalis().getNumber());

                    Integer departureAirportId = AirportSQL.newAirport(c, departure.getIata(), departure.getIcao(), departure.getAirport(), departure.getTimezone());
                    Integer arrivalAirportId = AirportSQL.newAirport(c, arrival.getIata(), arrival.getIcao(), arrival.getAirport(), arrival.getTimezone());

                    long flightId = FlightSQL.newFlight(c, date, flightStatus, airlineId, flightNumber, departureAirportId, arrivalAirportId, aircraftId);

                    OffsetDateTime scheduled = parseOdt(departure.getScheduled());
                    OffsetDateTime estimated = parseOdt(departure.getEstimated());
                    OffsetDateTime actual = parseOdt(departure.getActual());
                    OffsetDateTime estimatedRunway = parseOdt(departure.getEstimated_runway());
                    OffsetDateTime actualRunway = parseOdt(departure.getActual_runway());
                    DepartureSQL.newDeparture(c, flightId, departure.getTerminal(), departure.getGate(), departure.getDelay(),
                            scheduled, estimated, actual, estimatedRunway, actualRunway);

                    OffsetDateTime arrivalScheduled = parseOdt(arrival.getScheduled());
                    OffsetDateTime arrivalEstimated = parseOdt(arrival.getEstimated());
                    OffsetDateTime arrivalActual = parseOdt(arrival.getActual());
                    OffsetDateTime arrivalEstimatedRunway = parseOdt(arrival.getEstimated_runway());
                    OffsetDateTime arrivalActualRunway = parseOdt(arrival.getActual_runway());
                    ArrivalSQL.newArrival(c, flightId, arrival.getTerminal(), arrival.getGate(), arrival.getBaggage(), arrival.getDelay(),
                            arrivalScheduled, arrivalEstimated, arrivalActual, arrivalEstimatedRunway, arrivalActualRunway);


                    c.commit();
                } catch (Exception e) {
                    c.rollback();
                    System.err.printf("Error with the flight %s, %s, %s: %s\n", f.getFlight_date(), f.getFlightDetalis().getNumber(), f.getFlightDetalis().getIata(), e.getMessage());
                }
            }

            writeFlightsInfo(flightList);

        } catch (Exception e) {
            System.out.printf("There was an error: %s\n", e.getMessage());
        }

        databaseConnection.closeConnection(c);
    }

    private static String getApiKey() {
        Properties config = new Properties();
        String apiKey = "";

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
            apiKey = config.getProperty("API_KEY");
        } catch (IOException e) {
            System.err.printf("Configuration file not loaded: %s\n", e.getMessage());
        }
        return apiKey;
    }

    private static OffsetDateTime parseOdt(String s) {
        return (s == null || s.isBlank()) ? null : OffsetDateTime.parse(s);
    }

    private static void writeFlightsInfo (List<Flight> list) {
        try (FileWriter fileWriter = new FileWriter("C:\\Users\\valen\\projects\\flight-tracker\\src\\main\\resources\\flightsInfo");
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            for (Flight flight : list) {
                bufferedWriter.write(flight.toString());
                bufferedWriter.newLine();
            }
            System.out.println("Write successful");
        } catch (IOException e) {
            System.out.printf("\nError: %s", e.getMessage());
        }
    }
}