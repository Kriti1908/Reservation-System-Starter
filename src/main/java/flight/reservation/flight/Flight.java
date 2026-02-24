package flight.reservation.flight;

import flight.reservation.Airport;
import flight.reservation.plane.AircraftAdapter;

import java.util.Arrays;

public class Flight {

    private int number;
    private Airport departure;
    private Airport arrival;
    protected AircraftAdapter aircraft;

    public Flight(int number, Airport departure, Airport arrival, AircraftAdapter aircraft) throws IllegalArgumentException {
        this.number = number;
        this.departure = departure;
        this.arrival = arrival;
        this.aircraft = aircraft;
        checkValidity();
    }

    private void checkValidity() throws IllegalArgumentException {
        if (!isAircraftValid(departure) || !isAircraftValid(arrival)) {
            throw new IllegalArgumentException("Selected aircraft is not valid for the selected route.");
        }
    }

    private boolean isAircraftValid(Airport airport) {
        String model = aircraft.getModel();
        return Arrays.stream(airport.getAllowedAircrafts()).anyMatch(x -> x.equals(model));
    }

    public AircraftAdapter getAircraft() {
        return aircraft;
    }

    public int getNumber() {
        return number;
    }

    public Airport getDeparture() {
        return departure;
    }

    public Airport getArrival() {
        return arrival;
    }

    @Override
    public String toString() {
        return aircraft.getType() + "-" + aircraft.getModel() + "-" + number + "-" + departure.getCode() + "/" + arrival.getCode();
    }

}
