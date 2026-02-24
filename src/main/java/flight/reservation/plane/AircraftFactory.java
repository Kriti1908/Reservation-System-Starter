package flight.reservation.plane;

public class AircraftFactory {
    
    /**
     * Creates an aircraft instance based on the model name.
     * This centralizes all aircraft creation logic.
     * 
     * @param model The model name of the aircraft
     * @return Aircraft instance
     * @throws IllegalArgumentException if model is not recognized
     */
    public static Aircraft createAircraft(String model) {
        // PassengerPlane models
        if (model.equals("A380") || model.equals("A350") || 
            model.equals("Embraer 190") || model.equals("Antonov AN2")) {
            return new PassengerPlane(model);
        }
        
        // Helicopter models
        if (model.equals("H1") || model.equals("H2")) {
            return new Helicopter(model);
        }
        
        // PassengerDrone models
        if (model.equals("HypaHype")) {
            return new PassengerDrone(model);
        }
        
        throw new IllegalArgumentException(
            String.format("Aircraft model '%s' is not recognized", model)
        );
    }
    
    /**
     * Helper method to get the model from an aircraft instance.
     * This provides a uniform way to access the model.
     * 
     * @param aircraft The aircraft instance
     * @return The model name
     */
    public static String getModelFromAircraft(Aircraft aircraft) {
        return aircraft.getModel();
    }
}
