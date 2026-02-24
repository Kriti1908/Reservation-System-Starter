package flight.reservation.plane;

/**
 * Adapter for Helicopter that implements the AircraftAdapter interface.
 * Wraps a Helicopter and provides uniform access to its properties.
 */
public class HelicopterAdapter implements AircraftAdapter {
    private final Helicopter helicopter;

    public HelicopterAdapter(Helicopter helicopter) {
        this.helicopter = helicopter;
    }

    @Override
    public String getModel() {
        return helicopter.getModel();
    }

    @Override
    public int getPassengerCapacity() {
        return helicopter.getPassengerCapacity();
    }

    @Override
    public int getCrewCapacity() {
        // Helicopters typically have 1 pilot
        return 1;
    }

    @Override
    public String getType() {
        return "Helicopter";
    }

    /**
     * @return the underlying Helicopter object
     */
    public Helicopter getHelicopter() {
        return helicopter;
    }
}
