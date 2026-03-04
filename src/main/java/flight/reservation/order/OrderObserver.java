package flight.reservation.order;

import flight.reservation.Passenger;

import java.util.List;

public interface OrderObserver {
    void onOrderClosed(FlightOrder order);
    void onOrderPriceChanged(FlightOrder order, double oldPrice, double newPrice);
    void onPassengersChanged(FlightOrder order, List<Passenger> passengers);
}
