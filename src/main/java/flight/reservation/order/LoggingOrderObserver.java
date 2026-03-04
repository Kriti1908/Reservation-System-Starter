package flight.reservation.order;

import flight.reservation.Passenger;

import java.util.List;

public class LoggingOrderObserver implements OrderObserver {

    @Override
    public void onOrderClosed(FlightOrder order) {
        System.out.println("[LOG] Order closed. Price: " + order.getPrice()
                + " | Passengers: " + order.getPassengers().size());
    }

    @Override
    public void onOrderPriceChanged(FlightOrder order, double oldPrice, double newPrice) {
        System.out.println("[LOG] Order price changed from " + oldPrice + " to " + newPrice + ".");
    }

    @Override
    public void onPassengersChanged(FlightOrder order, List<Passenger> passengers) {
        System.out.println("[LOG] Order passengers updated. Count: " + passengers.size());
    }
}
