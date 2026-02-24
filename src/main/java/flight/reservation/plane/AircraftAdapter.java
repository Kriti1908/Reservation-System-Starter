package flight.reservation.plane;

public interface AircraftAdapter extends Aircraft {

    /**
     * @return the type of aircraft (e.g., "PassengerPlane", "Helicopter", "PassengerDrone")
     */
    String getType();
}