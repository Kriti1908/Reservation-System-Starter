package flight.reservation.flight;

public class NotificationFlightObserver implements FlightObserver {

    private final String recipientEmail;

    public NotificationFlightObserver(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    @Override
    public void onPassengersAdded(ScheduledFlight flight, int count) {
        System.out.println("[NOTIFICATION -> " + recipientEmail + "] "
                + count + " passenger(s) have been added to flight " + flight.getNumber() + ".");
    }

    @Override
    public void onPassengersRemoved(ScheduledFlight flight, int count) {
        System.out.println("[NOTIFICATION -> " + recipientEmail + "] "
                + count + " passenger(s) have been removed from flight " + flight.getNumber() + ".");
    }

    @Override
    public void onPriceChanged(ScheduledFlight flight, double oldPrice, double newPrice) {
        System.out.println("[NOTIFICATION -> " + recipientEmail + "] "
                + "Price for flight " + flight.getNumber()
                + " has changed from " + oldPrice + " to " + newPrice + ".");
    }

    @Override
    public void onFlightCancelled(ScheduledFlight flight) {
        System.out.println("[NOTIFICATION -> " + recipientEmail + "] "
                + "Flight " + flight.getNumber() + " has been cancelled.");
    }
}
