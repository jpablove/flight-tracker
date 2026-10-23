import ApiResponse.*;
import SQL.AirlineSQL;
import SQL.FlightSQL;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            /*
            for (Flight f : flightList) {
                c.setAutoCommit(false);

                Airline airline = f.getAirline();
                Departure departure = f.getDeparture();
                Arrival arrival = f.getArrival();
                Aircraft aircraft = f.getAircraft();

                String flightDate = f.getFlight_date();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(flightDate, formatter);

                String flightStatus = f.getFlight_status();
                int airlineId = AirlineSQL.newAirline(c, airline.getIata(), airline.getIcao(), airline.getName());
                String flightNumber = ;
                String departureAirportId = ;
                String arrivalAirportId = ;
                String aircraftId = ;

                FlightSQL.newFlight(c, date, flightStatus, airlineId, flightNumber, Integer.parseInt(departureAirportId), Integer.parseInt(arrivalAirportId), Integer.parseInt(aircraftId));
            }

             */

            writeFlightsInfo(flightList);

        } catch (Exception e) {
            System.out.printf("There was an error: %s\n", e.getMessage());
        }




        /*
            @TODO Delete everything trying to read from flightsInfo. It is not neccessary as I can the values I need from the API because I process them.

         */

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