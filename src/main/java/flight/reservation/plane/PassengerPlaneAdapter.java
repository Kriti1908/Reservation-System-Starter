package flight.reservation.plane;

/**
 * Adapter for PassengerPlane that implements the AircraftAdapter interface.
 * Wraps a PassengerPlane and provides uniform access to its properties.
 */
public class PassengerPlaneAdapter implements AircraftAdapter {
    private final PassengerPlane plane;

    public PassengerPlaneAdapter(PassengerPlane plane) {
        this.plane = plane;
    }

    @Override
    public String getModel() {
        return plane.model;
    }

    @Override
    public int getPassengerCapacity() {
        return plane.passengerCapacity;
    }

    @Override
    public int getCrewCapacity() {
        return plane.crewCapacity;
    }

    @Override
    public String getType() {
        return "PassengerPlane";
    }

    /**
     * @return the underlying PassengerPlane object
     */
    public PassengerPlane getPlane() {
        return plane;
    }
}
