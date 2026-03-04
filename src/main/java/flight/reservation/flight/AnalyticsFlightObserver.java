package flight.reservation.flight;

public class AnalyticsFlightObserver implements FlightObserver {

    private int totalPassengersAdded = 0;
    private int totalPassengersRemoved = 0;
    private int totalCancellations = 0;

    @Override
    public void onPassengersAdded(ScheduledFlight flight, int count) {
        totalPassengersAdded += count;
        System.out.println("[ANALYTICS] Passengers added: " + count
                + " | Total added: " + totalPassengersAdded
                + " | Flight: " + flight.getNumber());
    }

    @Override
    public void onPassengersRemoved(ScheduledFlight flight, int count) {
        totalPassengersRemoved += count;
        System.out.println("[ANALYTICS] Passengers removed: " + count
                + " | Total removed: " + totalPassengersRemoved
                + " | Flight: " + flight.getNumber());
    }

    @Override
    public void onPriceChanged(ScheduledFlight flight, double oldPrice, double newPrice) {
        double delta = newPrice - oldPrice;
        System.out.println("[ANALYTICS] Price changed by " + delta
                + " (from " + oldPrice + " to " + newPrice + ")"
                + " | Flight: " + flight.getNumber());
    }

    @Override
    public void onFlightCancelled(ScheduledFlight flight) {
        totalCancellations++;
        System.out.println("[ANALYTICS] Flight cancelled: " + flight.getNumber()
                + " | Total cancellations: " + totalCancellations);
    }

    public int getTotalPassengersAdded() {
        return totalPassengersAdded;
    }

    public int getTotalPassengersRemoved() {
        return totalPassengersRemoved;
    }

    public int getTotalCancellations() {
        return totalCancellations;
    }
}
