package ApiResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Flight {
    @JsonProperty("flight_date")
    private String flight_date;

    @JsonProperty("flight_status")
    private String flight_status;

    @JsonProperty("departure")
    private Departure departure;

    @JsonProperty("arrival")
    private Arrival arrival;

    @JsonProperty("airline")
    private Airline airline;

    @JsonProperty("flight")
    private FlightDetalis flightDetalis;

    @JsonProperty("aircraft")
    private Aircraft aircraft;

    @JsonProperty("live")
    private String live;

    public Flight(){}

    public Flight(String flight_date, String flight_status, Departure departure, Arrival arrival, Airline airline, FlightDetalis flightDetalis, Aircraft aircraft, String live) {
        this.flight_date = flight_date;
        this.flight_status = flight_status;
        this.departure = departure;
        this.arrival = arrival;
        this.airline = airline;
        this.flightDetalis = flightDetalis;
        this.aircraft = aircraft;
        this.live = live;
    }

    public String getFlight_date() {
        return flight_date;
    }

    public void setFlight_date(String flight_date) {
        this.flight_date = flight_date;
    }

    public String getFlight_status() {
        return flight_status;
    }

    public void setFlight_status(String flight_status) {
        this.flight_status = flight_status;
    }

    public Departure getDeparture() {
        return departure;
    }

    public void setDeparture(Departure departure) {
        this.departure = departure;
    }

    public Arrival getArrival() {
        return arrival;
    }

    public void setArrival(Arrival arrival) {
        this.arrival = arrival;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public FlightDetalis getFlightDetalis() {
        return flightDetalis;
    }

    public void setFlightDetalis(FlightDetalis flightDetalis) {
        this.flightDetalis = flightDetalis;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }
}
