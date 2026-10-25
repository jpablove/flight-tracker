package ApiResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlightDetalis {
    @JsonProperty("number")
    private int number;

    @JsonProperty("iata")
    private String iata;

    @JsonProperty("icao")
    private String icao;

    @JsonProperty("codeshared")
    private Codeshared codeshared;

    public FlightDetalis(){}

    public FlightDetalis(int number, String iata, String icao, Codeshared codeshared) {
        this.number = number;
        this.iata = iata;
        this.icao = icao;
        this.codeshared = codeshared;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public Codeshared getCodeshared() {
        return codeshared;
    }

    public void setCodeshared(Codeshared codeshared) {
        this.codeshared = codeshared;
    }

    @Override
    public String toString() {
        return "FlightDetalis{" +
                "number=" + number +
                ", iata='" + iata + '\'' +
                ", icao='" + icao + '\'' +
                ", codeshared=" + codeshared +
                "};";
    }
}
