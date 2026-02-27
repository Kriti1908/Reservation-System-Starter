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

    // Builder pattern
    private ScheduledFlight(Builder builder) {
        super(builder.number, builder.departure, builder.arrival, builder.aircraft);
        this.departureTime = builder.departureTime;
        this.passengers = builder.passengers != null ? 
                        new ArrayList<>(builder.passengers) : new ArrayList<>();
        this.currentPrice = builder.currentPrice > 0 ? 
                        builder.currentPrice : 100.0;
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
    
    public static class Builder {
        // Required fields
        private int number;
        private Airport departure;
        private Airport arrival;
        private AircraftAdapter aircraft;
        private Date departureTime;

        // Optional fields
        private List<Passenger> passengers;
        private double currentPrice = 100.0;

        /**
         * Set the flight number (required).
         *
         * @param number The unique flight number
         * @return This builder instance for method chaining
         */
        public Builder withNumber(int number) {
            this.number = number;
            return this;
        }

        /**
         * Set the departure airport (required).
         *
         * @param departure The departure airport
         * @return This builder instance for method chaining
         */
        public Builder withDeparture(Airport departure) {
            this.departure = departure;
            return this;
        }

        /**
         * Set the arrival airport (required).
         *
         * @param arrival The arrival airport
         * @return This builder instance for method chaining
         */
        public Builder withArrival(Airport arrival) {
            this.arrival = arrival;
            return this;
        }

        /**
         * Set the aircraft adapter (required).
         *
         * @param aircraft The aircraft adapter for this flight
         * @return This builder instance for method chaining
         */
        public Builder withAircraft(AircraftAdapter aircraft) {
            this.aircraft = aircraft;
            return this;
        }

        /**
         * Set the departure date and time (required).
         *
         * @param departureTime The date and time of departure
         * @return This builder instance for method chaining
         */
        public Builder withDepartureTime(Date departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        /**
         * Set the list of passengers (optional).
         * If not set, the flight will start with an empty passenger list.
         *
         * @param passengers The list of passengers for this flight
         * @return This builder instance for method chaining
         */
        public Builder withPassengers(List<Passenger> passengers) {
            this.passengers = passengers;
            return this;
        }

        /**
         * Set the current price per ticket (optional).
         * If not set, defaults to 100.0
         *
         * @param currentPrice The ticket price for this flight
         * @return This builder instance for method chaining
         * @throws IllegalArgumentException if price is negative
         */
        public Builder withCurrentPrice(double currentPrice) {
            if (currentPrice < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            this.currentPrice = currentPrice;
            return this;
        }

        /**
         * Build and validate the ScheduledFlight instance.
         * 
         * This method performs comprehensive validation of all required fields
         * and creates a new ScheduledFlight object through the private constructor.
         *
         * @return A fully initialized and validated ScheduledFlight instance
         * @throws IllegalStateException if any required field is null
         * @throws IllegalArgumentException if aircraft is not valid for the airports
         */
        public ScheduledFlight build() {
            // Validate all required fields
            if (departure == null) {
                throw new IllegalStateException("Departure airport must be set");
            }
            if (arrival == null) {
                throw new IllegalStateException("Arrival airport must be set");
            }
            if (aircraft == null) {
                throw new IllegalStateException("Aircraft must be set");
            }
            if (departureTime == null) {
                throw new IllegalStateException("Departure time must be set");
            }
            
            // Validate price
            if (currentPrice < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            return new ScheduledFlight(this);
        }
    }
}