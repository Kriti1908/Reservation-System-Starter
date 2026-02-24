package flight.reservation.flight;

public class LoggingFlightObserver implements FlightObserver {
    @Override
    public void onPassengersAdded(ScheduledFlight flight, int count) {
        System.out.println("[LOG] " + count + " passengers added to flight " + flight);
    }

    @Override
    public void onPassengersRemoved(ScheduledFlight flight, int count) {
        System.out.println("[LOG] " + count + " passengers removed from flight " + flight);
    }

    @Override
    public void onPriceChanged(ScheduledFlight flight, double oldPrice, double newPrice) {
        System.out.println("[LOG] Price changed from " + oldPrice + " to " + newPrice + " for flight " + flight);
    }

    @Override
    public void onFlightCancelled(ScheduledFlight flight) {
        System.out.println("[LOG] Flight cancelled: " + flight);
    }
}
