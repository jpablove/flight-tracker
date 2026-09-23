import ApiResponse.Flight;
import ApiResponse.FlightResponse;
import ApiResponse.Pagination;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final String apiKey = "ecfe5ee6666bdaa8f562069bb83bcbe7";
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