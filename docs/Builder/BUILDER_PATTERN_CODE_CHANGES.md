# Builder Pattern Implementation - Code Changes & PUML Diagrams

## Overview

This document provides all the code changes needed to implement the Builder Pattern for the `ScheduledFlight` class and includes comprehensive PUML class diagrams showing the refactoring.

---

## Files to Modify

| File | Location | Change Type | Impact |
|------|----------|------------|--------|
| **ScheduledFlight.java** | `src/main/java/flight/reservation/flight/ScheduledFlight.java` | **MAJOR** | Replace public constructors with private constructor and add nested Builder class |
| **Schedule.java** | `src/main/java/flight/reservation/flight/Schedule.java` | MINOR | Update `scheduleFlight()` method to use builder |
| Test files | `src/test/java/flight/reservation/*.java` | MINOR | Update test code that creates ScheduledFlight instances |

---

## Code Change 1: ScheduledFlight.java - Complete Refactoring

### Change 1.1: Replace Public Constructors with Private Constructor

**REMOVE these lines (18-29):**
```java
public ScheduledFlight(int number, Airport departure, Airport arrival, AircraftAdapter aircraft, Date departureTime) {
    super(number, departure, arrival, aircraft);
    this.departureTime = departureTime;
    this.passengers = new ArrayList<>();
}

public ScheduledFlight(int number, Airport departure, Airport arrival, AircraftAdapter aircraft, Date departureTime, double currentPrice) {
    super(number, departure, arrival, aircraft);
    this.departureTime = departureTime;
    this.passengers = new ArrayList<>();
    this.currentPrice = currentPrice;
}
```

**REPLACE WITH (after line 17, before observer management section):**
```java
// ========== BUILDER PATTERN: Private Constructor ==========
/**
 * Private constructor - only accessible via Builder.
 * This ensures ScheduledFlight objects are always properly initialized
 * through the builder's validation logic.
 *
 * @param builder The builder object containing all flight parameters
 */
private ScheduledFlight(Builder builder) {
    super(builder.number, builder.departure, builder.arrival, builder.aircraft);
    this.departureTime = builder.departureTime;
    this.passengers = builder.passengers != null ? 
                     new ArrayList<>(builder.passengers) : new ArrayList<>();
    this.currentPrice = builder.currentPrice > 0 ? 
                       builder.currentPrice : 100.0;
}
```

---

### Change 1.2: Add Static Nested Builder Class

**ADD this code at the end of the ScheduledFlight class (before closing brace):**

```java
// ========== BUILDER PATTERN: Static Nested Builder Class ==========
/**
 * Builder for constructing ScheduledFlight objects with a fluent interface.
 * 
 * This builder provides flexible construction with required and optional parameters.
 * All validation happens in the build() method to ensure objects are always in a valid state.
 * 
 * REQUIRED FIELDS:
 * - number: Flight number
 * - departure: Departure airport
 * - arrival: Arrival airport
 * - aircraft: Aircraft adapter
 * - departureTime: Date and time of departure
 * 
 * OPTIONAL FIELDS:
 * - passengers: List of passengers (defaults to empty list)
 * - currentPrice: Flight price (defaults to 100.0)
 * 
 * USAGE EXAMPLE:
 * ScheduledFlight flight = new ScheduledFlight.Builder()
 *     .withNumber(1)
 *     .withDeparture(jfkAirport)
 *     .withArrival(berlinAirport)
 *     .withAircraft(helicopterAdapter)
 *     .withDepartureTime(departureDate)
 *     .withCurrentPrice(250.0)
 *     .withPassengers(passengerList)
 *     .build();
 */
public static class Builder {
    // Required fields
    private int number;
    private Airport departure;
    private Airport arrival;
    private AircraftAdapter aircraft;
    private Date departureTime;

    // Optional fields
    private List<Passenger> passengers;
    private double currentPrice = 100.0;

    /**
     * Set the flight number (required).
     *
     * @param number The unique flight number
     * @return This builder instance for method chaining
     */
    public Builder withNumber(int number) {
        this.number = number;
        return this;
    }

    /**
     * Set the departure airport (required).
     *
     * @param departure The departure airport
     * @return This builder instance for method chaining
     */
    public Builder withDeparture(Airport departure) {
        this.departure = departure;
        return this;
    }

    /**
     * Set the arrival airport (required).
     *
     * @param arrival The arrival airport
     * @return This builder instance for method chaining
     */
    public Builder withArrival(Airport arrival) {
        this.arrival = arrival;
        return this;
    }

    /**
     * Set the aircraft adapter (required).
     *
     * @param aircraft The aircraft adapter for this flight
     * @return This builder instance for method chaining
     */
    public Builder withAircraft(AircraftAdapter aircraft) {
        this.aircraft = aircraft;
        return this;
    }

    /**
     * Set the departure date and time (required).
     *
     * @param departureTime The date and time of departure
     * @return This builder instance for method chaining
     */
    public Builder withDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
        return this;
    }

    /**
     * Set the list of passengers (optional).
     * If not set, the flight will start with an empty passenger list.
     *
     * @param passengers The list of passengers for this flight
     * @return This builder instance for method chaining
     */
    public Builder withPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
        return this;
    }

    /**
     * Set the current price per ticket (optional).
     * If not set, defaults to 100.0
     *
     * @param currentPrice The ticket price for this flight
     * @return This builder instance for method chaining
     * @throws IllegalArgumentException if price is negative
     */
    public Builder withCurrentPrice(double currentPrice) {
        if (currentPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.currentPrice = currentPrice;
        return this;
    }

    /**
     * Build and validate the ScheduledFlight instance.
     * 
     * This method performs comprehensive validation of all required fields
     * and creates a new ScheduledFlight object through the private constructor.
     *
     * @return A fully initialized and validated ScheduledFlight instance
     * @throws IllegalStateException if any required field is null
     * @throws IllegalArgumentException if aircraft is not valid for the airports
     */
    public ScheduledFlight build() {
        // Validate all required fields
        if (departure == null) {
            throw new IllegalStateException("Departure airport must be set");
        }
        if (arrival == null) {
            throw new IllegalStateException("Arrival airport must be set");
        }
        if (aircraft == null) {
            throw new IllegalStateException("Aircraft must be set");
        }
        if (departureTime == null) {
            throw new IllegalStateException("Departure time must be set");
        }
        
        // Validate price
        if (currentPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        // Note: Aircraft compatibility validation happens in Flight parent constructor
        // which is called from the private constructor below.
        // No need for duplicate validation here.

        // Create and return the ScheduledFlight using the private constructor
        return new ScheduledFlight(this);
    }
}
```

---

## Code Change 2: Schedule.java - Update scheduleFlight() Method

**LOCATION**: `src/main/java/flight/reservation/flight/Schedule.java`

### Change 2.1: Update scheduleFlight() Method

**BEFORE (around line 18):**
```java
public void scheduleFlight(Flight flight, Date date) {
    ScheduledFlight scheduledFlight = new ScheduledFlight(flight.getNumber(), flight.getDeparture(), flight.getArrival(), flight.getAircraft(), date);
    scheduledFlights.add(scheduledFlight);
}
```

**AFTER:**
```java
public void scheduleFlight(Flight flight, Date date) {
    ScheduledFlight scheduledFlight = new ScheduledFlight.Builder()
        .withNumber(flight.getNumber())
        .withDeparture(flight.getDeparture())
        .withArrival(flight.getArrival())
        .withAircraft(flight.getAircraft())
        .withDepartureTime(date)
        .build();
    scheduledFlights.add(scheduledFlight);
}
```

---

## Code Change 3: Update Test Files

### Change 3.1: ScenarioTest.java - Update Test Cases

**BEFORE (lines where ScheduledFlight is created):**
```java
schedule.scheduleFlight(flight, departure);
```

This should work as-is since Schedule.scheduleFlight() is updated. However, if there are direct instantiations:

**BEFORE:**
```java
ScheduledFlight scheduledFlight = new ScheduledFlight(1, startAirport, destinationAirport, new Helicopter("H1"), departure);
```

**AFTER:**
```java
ScheduledFlight scheduledFlight = new ScheduledFlight.Builder()
    .withNumber(1)
    .withDeparture(startAirport)
    .withArrival(destinationAirport)
    .withAircraft(helicopterAdapter)  // Note: Use adapter, not raw aircraft
    .withDepartureTime(departure)
    .build();
```

### Change 3.2: ScheduleTest.java - Similar Updates

Replace any direct ScheduledFlight instantiations with the builder pattern.

---

## Usage Examples

### Example 1: Creating a Basic ScheduledFlight

**BEFORE:**
```java
ScheduledFlight flight = new ScheduledFlight(1, jfk, berlin, helicopterAdapter, departureDate);
flight.setCurrentPrice(250.0);
```

**AFTER:**
```java
ScheduledFlight flight = new ScheduledFlight.Builder()
    .withNumber(1)
    .withDeparture(jfk)
    .withArrival(berlin)
    .withAircraft(helicopterAdapter)
    .withDepartureTime(departureDate)
    .withCurrentPrice(250.0)
    .build();
```

### Example 2: Creating with Passengers

**BEFORE:**
```java
ScheduledFlight flight = new ScheduledFlight(2, london, paris, planeAdapter, departureDate, 200.0);
List<Passenger> passengers = Arrays.asList(
    new Passenger("John Doe"),
    new Passenger("Jane Smith")
);
flight.addPassengers(passengers);
```

**AFTER:**
```java
List<Passenger> passengers = Arrays.asList(
    new Passenger("John Doe"),
    new Passenger("Jane Smith")
);

ScheduledFlight flight = new ScheduledFlight.Builder()
    .withNumber(2)
    .withDeparture(london)
    .withArrival(paris)
    .withAircraft(planeAdapter)
    .withDepartureTime(departureDate)
    .withCurrentPrice(200.0)
    .withPassengers(passengers)
    .build();
```

### Example 3: With Only Required Fields

**BEFORE:**
```java
ScheduledFlight flight = new ScheduledFlight(3, sf, tokyo, droneAdapter, departureDate);
// Uses default price of 100.0
```

**AFTER:**
```java
ScheduledFlight flight = new ScheduledFlight.Builder()
    .withNumber(3)
    .withDeparture(sf)
    .withArrival(tokyo)
    .withAircraft(droneAdapter)
    .withDepartureTime(departureDate)
    .build();
    // Uses default price of 100.0
```

---

## Summary of Code Changes

| Component | Changes | Lines Affected |
|-----------|---------|-----------------|
| **ScheduledFlight.java** | Remove 2 public constructors, add private constructor, add nested Builder class | 18-29 (remove), then add ~200 lines |
| **Schedule.java** | Update `scheduleFlight()` to use builder | ~5 lines |
| **Test files** | Update instantiations to use builder | Variable (all ScheduledFlight creations) |

---

## Validation & Testing

### Unit Tests to Add

1. **Test valid construction with all fields**
2. **Test valid construction with required fields only**
3. **Test missing required field (each field)**
4. **Test invalid price (negative)**
5. **Test default values**
6. **Test method chaining**

### Integration Tests

- Run existing test suite to ensure no regressions
- Verify all test cases pass with new builder approach

---

## Benefits Achieved

✅ **Fluent Interface**: Readable, chainable method calls  
✅ **Flexibility**: Optional fields can be easily added later  
✅ **Validation**: All validation centralized in `build()`  
✅ **Clarity**: Intent is clear from method names  
✅ **Immutability**: Objects are fully initialized on creation  
✅ **Extensibility**: Adding new optional fields requires only new `with*()` method  
✅ **No Constructor Overloading**: One builder replaces multiple constructors  
✅ **Type Safety**: Prevents invalid object creation  

---

## Implementation Checklist

- [ ] Backup current ScheduledFlight.java
- [ ] Replace constructors with private constructor
- [ ] Add nested Builder class with all fields
- [ ] Implement all `with*()` methods
- [ ] Implement validation in `build()` method
- [ ] Update Schedule.scheduleFlight()
- [ ] Find and update all ScheduledFlight instantiations in codebase
- [ ] Update all test files
- [ ] Compile code and verify no errors
- [ ] Run unit tests
- [ ] Run integration tests
- [ ] Manual testing of affected workflows
- [ ] Update documentation

---

## Complete File: ScheduledFlight.java with Builder

See the reference file: `ScheduledFlight_RefactoredWithBuilder.java`
