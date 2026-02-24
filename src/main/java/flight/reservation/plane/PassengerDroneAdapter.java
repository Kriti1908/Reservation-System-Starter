package flight.reservation.plane;

/**
 * Adapter for PassengerDrone that implements the AircraftAdapter interface.
 * Wraps a PassengerDrone and provides uniform access to its properties.
 */
public class PassengerDroneAdapter implements AircraftAdapter {
    private final PassengerDrone drone;
    private static final int DRONE_PASSENGER_CAPACITY = 4;
    private static final int DRONE_CREW_CAPACITY = 0;

    public PassengerDroneAdapter(PassengerDrone drone) {
        this.drone = drone;
    }

    @Override
    public String getModel() {
        return drone.getModel();
    }

    @Override
    public int getPassengerCapacity() {
        return DRONE_PASSENGER_CAPACITY;
    }

    @Override
    public int getCrewCapacity() {
        return DRONE_CREW_CAPACITY;
    }

    @Override
    public String getType() {
        return "PassengerDrone";
    }

    /**
     * @return the underlying PassengerDrone object
     */
    public PassengerDrone getDrone() {
        return drone;
    }
}
