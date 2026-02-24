package flight.reservation.plane;

/**
 * Adapter interface that provides a uniform way to access aircraft properties.
 * This eliminates the need for instanceof checks and provides consistent access patterns.
 */
public interface AircraftAdapter {
    /**
     * @return the model name of the aircraft
     */
    String getModel();

    /**
     * @return the passenger capacity of the aircraft
     */
    int getPassengerCapacity();

    /**
     * @return the crew capacity of the aircraft
     */
    int getCrewCapacity();

    /**
     * @return the type of aircraft (e.g., "PassengerPlane", "Helicopter", "PassengerDrone")
     */
    String getType();
}
