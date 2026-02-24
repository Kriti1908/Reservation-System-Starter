package flight.reservation.flight;

import flight.reservation.Airport;
import flight.reservation.Passenger;
import flight.reservation.plane.AircraftAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduledFlight extends Flight {

    private final List<Passenger> passengers;
    private final List<FlightObserver> observers = new ArrayList<>();
    private final Date departureTime;
    private double currentPrice = 100;

    public ScheduledFlight(int number, Airport departure, Airport arrival, AircraftAdapter aircraft, Date departureTime) {
        super(number, departure, arrival, aircraft);
        this.departureTime = departureTime;
        this.passengers = new ArrayList<>();
    }

    public ScheduledFlight(int number, Airport departure, Airport arrival, AircraftAdapter aircraft, Date departureTime, double currentPrice) {
        super(number, departure, arrival, aircraft);
        this.departureTime = departureTime;
        this.passengers = new ArrayList<>();
        this.currentPrice = currentPrice;
    }

    // Observer management

    public void addObserver(FlightObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(FlightObserver observer) {
        observers.remove(observer);
    }

    // Flight operations

    /**
     * Gets the crew member capacity using the adapter interface.
     * No instanceof checks needed - uniform interface access.
     */
    public int getCrewMemberCapacity() {
        return aircraft.getCrewCapacity();
    }

    /**
     * Gets the passenger capacity using the adapter interface.
     * No instanceof checks needed - uniform interface access.
     */
    public int getCapacity() {
        return aircraft.getPassengerCapacity();
    }

    public int getAvailableCapacity() {
        return this.getCapacity() - this.passengers.size();
    }

    public void addPassengers(List<Passenger> passengers) {
        this.passengers.addAll(passengers);
        notifyPassengersAdded(passengers.size());
    }

    public void removePassengers(List<Passenger> passengers) {
        int count = 0;
        for (Passenger p : passengers) {
            if (this.passengers.remove(p)) count++;
        }
        if (count > 0) notifyPassengersRemoved(count);
    }

    public void setCurrentPrice(double currentPrice) {
        double oldPrice = this.currentPrice;
        this.currentPrice = currentPrice;
        notifyPriceChanged(oldPrice, currentPrice);
    }

    public void cancelFlight() {
        notifyFlightCancelled();
    }

    // Getters

    public Date getDepartureTime() {
        return departureTime;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    // Private notification methods

    private void notifyPassengersAdded(int count) {
        for (FlightObserver observer : observers) {
            observer.onPassengersAdded(this, count);
        }
    }

    private void notifyPassengersRemoved(int count) {
        for (FlightObserver observer : observers) {
            observer.onPassengersRemoved(this, count);
        }
    }

    private void notifyPriceChanged(double oldPrice, double newPrice) {
        for (FlightObserver observer : observers) {
            observer.onPriceChanged(this, oldPrice, newPrice);
        }
    }

    private void notifyFlightCancelled() {
        for (FlightObserver observer : observers) {
            observer.onFlightCancelled(this);
        }
    }
}