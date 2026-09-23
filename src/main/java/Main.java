import ApiResponse.Flight;
import ApiResponse.FlightResponse;
import ApiResponse.Pagination;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties config = new Properties();
        String apiKey = "";
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
            apiKey = config.getProperty("API_KEY");
        } catch (IOException e) {
            System.err.printf("Configuration file not loaded: %s", e.getMessage());
        }

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

            // Start of the test where I verify if I can get back the data correctly
            ObjectMapper objectMapper = new ObjectMapper();
            FlightResponse flightResponse = objectMapper.readValue(response.body(), FlightResponse.class);
            Pagination pagination = flightResponse.getPagination();
            System.out.println(pagination.toString());

            List<Flight> flightList = flightResponse.getData();
            Flight flight = flightList.get(0);
            System.out.println(flight.getFlight_date());
            // End

        } catch (Exception e) {
            System.out.printf("There was an error: %s\n", e.getMessage());
        }
    }
}