package flight.reservation.flight;

public interface FlightObserver {
    void onPassengersAdded(ScheduledFlight flight, int count);
    void onPassengersRemoved(ScheduledFlight flight, int count);
    void onPriceChanged(ScheduledFlight flight, double oldPrice, double newPrice);
    void onFlightCancelled(ScheduledFlight flight);
}
