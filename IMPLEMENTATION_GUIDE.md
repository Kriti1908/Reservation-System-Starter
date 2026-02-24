# Design Patterns - Detailed Implementation Guide

## 1. FACTORY PATTERN - Implementation Details

### Problem Code (Current Implementation)

**Flight.java**
```java
private boolean isAircraftValid(Airport airport) {
    return Arrays.stream(airport.getAllowedAircrafts()).anyMatch(x -> {
        String model;
        if (this.aircraft instanceof PassengerPlane) {
            model = ((PassengerPlane) this.aircraft).model;
        } else if (this.aircraft instanceof Helicopter) {
            model = ((Helicopter) this.aircraft).getModel();
        } else if (this.aircraft instanceof PassengerDrone) {
            model = "HypaHype";
        } else {
            throw new IllegalArgumentException(String.format("Aircraft is not recognized"));
        }
        return x.equals(model);
    });
}
```

**ScheduledFlight.java**
```java
public int getCapacity() throws NoSuchFieldException {
    if (this.aircraft instanceof PassengerPlane) {
        return ((PassengerPlane) this.aircraft).passengerCapacity;
    }
    if (this.aircraft instanceof Helicopter) {
        return ((Helicopter) this.aircraft).getPassengerCapacity();
    }
    if (this.aircraft instanceof PassengerDrone) {
        return 4;
    }
    throw new NoSuchFieldException("this aircraft has no information about its capacity");
}
```

### Solution: Factory Pattern Implementation

#### Step 1: Create Aircraft Interface

```java
package flight.reservation.plane;

/**
 * Common interface for all aircraft types
 */
public interface Aircraft {
    
    /**
     * Get the model name of the aircraft
     */
    String getModel();
    
    /**
     * Get the passenger capacity of the aircraft
     */
    int getPassengerCapacity();
    
    /**
     * Get the crew capacity of the aircraft
     */
    int getCrewCapacity();
    
    /**
     * Get the type of aircraft (for categorization)
     */
    String getAircraftType();
}
```

#### Step 2: Refactor Existing Aircraft Classes

```java
package flight.reservation.plane;

public class PassengerPlane implements Aircraft {
    
    public String model;
    public int passengerCapacity;
    public int crewCapacity;
    
    public PassengerPlane(String model) {
        this.model = model;
        switch (model) {
            case "A380":
                this.passengerCapacity = 500;
                this.crewCapacity = 42;
                break;
            case "A350":
                this.passengerCapacity = 320;
                this.crewCapacity = 40;
                break;
            case "Embraer 190":
                this.passengerCapacity = 25;
                this.crewCapacity = 5;
                break;
            case "Antonov AN2":
                this.passengerCapacity = 15;
                this.crewCapacity = 3;
                break;
            default:
                throw new IllegalArgumentException(
                    String.format("Model type '%s' is not recognized", model));
        }
    }
    
    @Override
    public String getModel() {
        return model;
    }
    
    @Override
    public int getPassengerCapacity() {
        return passengerCapacity;
    }
    
    @Override
    public int getCrewCapacity() {
        return crewCapacity;
    }
    
    @Override
    public String getAircraftType() {
        return "PassengerPlane";
    }
}
```

```java
package flight.reservation.plane;

public class Helicopter implements Aircraft {
    private final String model;
    private final int passengerCapacity;
    private static final int CREW_CAPACITY = 2;
    
    public Helicopter(String model) {
        this.model = model;
        if (model.equals("H1")) {
            this.passengerCapacity = 4;
        } else if (model.equals("H2")) {
            this.passengerCapacity = 6;
        } else {
            throw new IllegalArgumentException(
                String.format("Model type '%s' is not recognized", model));
        }
    }
    
    @Override
    public String getModel() {
        return model;
    }
    
    @Override
    public int getPassengerCapacity() {
        return passengerCapacity;
    }
    
    @Override
    public int getCrewCapacity() {
        return CREW_CAPACITY;
    }
    
    @Override
    public String getAircraftType() {
        return "Helicopter";
    }
}
```

```java
package flight.reservation.plane;

public class PassengerDrone implements Aircraft {
    private final String model;
    private static final int PASSENGER_CAPACITY = 4;
    private static final int CREW_CAPACITY = 0;
    
    public PassengerDrone(String model) {
        if (model.equals("HypaHype")) {
            this.model = model;
        } else {
            throw new IllegalArgumentException(
                String.format("Model type '%s' is not recognized", model));
        }
    }
    
    @Override
    public String getModel() {
        return model;
    }
    
    @Override
    public int getPassengerCapacity() {
        return PASSENGER_CAPACITY;
    }
    
    @Override
    public int getCrewCapacity() {
        return CREW_CAPACITY;
    }
    
    @Override
    public String getAircraftType() {
        return "PassengerDrone";
    }
}
```

#### Step 3: Create Aircraft Factory

```java
package flight.reservation.plane;

/**
 * Factory for creating Aircraft instances
 * Centralizes all aircraft instantiation logic
 */
public class AircraftFactory {
    
    /**
     * Create an aircraft based on model name
     * 
     * @param model The model name of the aircraft
     * @return Aircraft instance
     * @throws IllegalArgumentException if model is not recognized
     */
    public static Aircraft createAircraft(String model) {
        // Check for passenger planes
        if (isPassengerPlaneModel(model)) {
            return new PassengerPlane(model);
        }
        
        // Check for helicopters
        if (isHelicopterModel(model)) {
            return new Helicopter(model);
        }
        
        // Check for drones
        if (isPassengerDroneModel(model)) {
            return new PassengerDrone(model);
        }
        
        throw new IllegalArgumentException(
            String.format("Aircraft model '%s' is not recognized", model));
    }
    
    /**
     * Check if model is a valid passenger plane model
     */
    private static boolean isPassengerPlaneModel(String model) {
        return model.equals("A380") || model.equals("A350") || 
               model.equals("Embraer 190") || model.equals("Antonov AN2");
    }
    
    /**
     * Check if model is a valid helicopter model
     */
    private static boolean isHelicopterModel(String model) {
        return model.equals("H1") || model.equals("H2");
    }
    
    /**
     * Check if model is a valid passenger drone model
     */
    private static boolean isPassengerDroneModel(String model) {
        return model.equals("HypaHype");
    }
    
    /**
     * Get all allowed aircraft models
     */
    public static String[] getAllowedModels() {
        return new String[]{"A380", "A350", "Embraer 190", "Antonov AN2", 
                            "H1", "H2", "HypaHype"};
    }
    
    /**
     * Check if a model is supported by the factory
     */
    public static boolean isModelSupported(String model) {
        return isPassengerPlaneModel(model) || isHelicopterModel(model) || 
               isPassengerDroneModel(model);
    }
}
```

#### Step 4: Refactor Flight Class

```java
package flight.reservation.flight;

import flight.reservation.Airport;
import flight.reservation.plane.Aircraft;
import flight.reservation.plane.AircraftFactory;
import java.util.Arrays;

public class Flight {
    private int number;
    private Airport departure;
    private Airport arrival;
    private Aircraft aircraft;
    
    public Flight(int number, Airport departure, Airport arrival, 
                  Aircraft aircraft) throws IllegalArgumentException {
        this.number = number;
        this.departure = departure;
        this.arrival = arrival;
        this.aircraft = aircraft;
        checkValidity();
    }
    
    private void checkValidity() throws IllegalArgumentException {
        if (!isAircraftValid(departure) || !isAircraftValid(arrival)) {
            throw new IllegalArgumentException(
                "Selected aircraft is not valid for the selected route.");
        }
    }
    
    private boolean isAircraftValid(Airport airport) {
        return Arrays.stream(airport.getAllowedAircrafts()).anyMatch(
            model -> model.equals(aircraft.getModel())
        );
    }
    
    public Aircraft getAircraft() {
        return aircraft;
    }
    
    public int getNumber() {
        return number;
    }
    
    public Airport getDeparture() {
        return departure;
    }
    
    public Airport getArrival() {
        return arrival;
    }
    
    @Override
    public String toString() {
        return aircraft.getModel() + "-" + number + "-" + 
               departure.getCode() + "/" + arrival.getCode();
    }
}
```

#### Step 5: Refactor ScheduledFlight Class

```java
package flight.reservation.flight;

import flight.reservation.Airport;
import flight.reservation.Passenger;
import flight.reservation.plane.Aircraft;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduledFlight extends Flight {
    private final List<Passenger> passengers;
    private final Date departureTime;
    private double currentPrice = 100;
    
    public ScheduledFlight(int number, Airport departure, Airport arrival, 
                           Aircraft aircraft, Date departureTime) {
        super(number, departure, arrival, aircraft);
        this.departureTime = departureTime;
        this.passengers = new ArrayList<>();
    }
    
    public ScheduledFlight(int number, Airport departure, Airport arrival, 
                           Aircraft aircraft, Date departureTime, double currentPrice) {
        super(number, departure, arrival, aircraft);
        this.departureTime = departureTime;
        this.passengers = new ArrayList<>();
        this.currentPrice = currentPrice;
    }
    
    /**
     * Get crew member capacity - NO MORE instanceof CHECKS!
     */
    public int getCrewMemberCapacity() throws NoSuchFieldException {
        try {
            return getAircraft().getCrewCapacity();
        } catch (Exception e) {
            throw new NoSuchFieldException(
                "Aircraft has no information about its crew capacity");
        }
    }
    
    /**
     * Get passenger capacity - NO MORE instanceof CHECKS!
     */
    public int getCapacity() throws NoSuchFieldException {
        try {
            return getAircraft().getPassengerCapacity();
        } catch (Exception e) {
            throw new NoSuchFieldException(
                "Aircraft has no information about its capacity");
        }
    }
    
    public int getAvailableCapacity() throws NoSuchFieldException {
        return this.getCapacity() - this.passengers.size();
    }
    
    public void addPassengers(List<Passenger> passengers) {
        this.passengers.addAll(passengers);
    }
    
    public void removePassengers(List<Passenger> passengers) {
        this.passengers.removeAll(passengers);
    }
    
    public Date getDepartureTime() {
        return departureTime;
    }
    
    public List<Passenger> getPassengers() {
        return passengers;
    }
    
    public double getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
}
```

### Benefits of Factory Pattern Implementation

1. **No More Type Checking**: The `instanceof` checks are completely eliminated
2. **Centralized Creation**: All aircraft creation happens through the factory
3. **Easy to Extend**: Add new aircraft types with minimal changes
4. **Consistent Interface**: All aircraft implement the same `Aircraft` interface
5. **Better Testability**: Can mock the aircraft interface for testing

### Before and After Comparison

**Before (5 instanceof checks scattered across code):**
```java
if (this.aircraft instanceof PassengerPlane) {
    model = ((PassengerPlane) this.aircraft).model;
} else if (this.aircraft instanceof Helicopter) {
    model = ((Helicopter) this.aircraft).getModel();
} else if (this.aircraft instanceof PassengerDrone) {
    model = "HypaHype";
}
```

**After (Simple interface call):**
```java
return model.equals(aircraft.getModel());
```

---

## 2. ADAPTER PATTERN - Implementation Details

### Problem Analysis

Different aircraft classes have inconsistent interfaces:

| Class | Model Access | Capacity Access | Crew Access |
|-------|-------------|-----------------|-------------|
| PassengerPlane | Public field `.model` | Public field `.passengerCapacity` | Public field `.crewCapacity` |
| Helicopter | Method `.getModel()` | Method `.getPassengerCapacity()` | Hardcoded |
| PassengerDrone | Private field | Hardcoded (4) | Hardcoded (0) |

### Solution: Adapter Pattern (if not using Factory Pattern)

If you want to keep legacy classes unchanged, use adapters:

```java
package flight.reservation.plane;

/**
 * Adapter to make PassengerPlane compatible with a uniform interface
 */
public class PassengerPlaneAdapter {
    private final PassengerPlane plane;
    
    public PassengerPlaneAdapter(PassengerPlane plane) {
        this.plane = plane;
    }
    
    public String getModel() {
        return plane.model;
    }
    
    public int getPassengerCapacity() {
        return plane.passengerCapacity;
    }
    
    public int getCrewCapacity() {
        return plane.crewCapacity;
    }
}
```

However, **Factory Pattern is preferred** because:
- It combines Factory + Adapter benefits
- Cleaner code with single interface
- Better separation of concerns
- Factory knows how to create correct instances

---

## 3. STRATEGY PATTERN - Implementation Details

### Problem Code

```java
public boolean processOrderWithCreditCard(CreditCard creditCard) {
    if (isClosed()) { return true; }
    if (!cardIsPresentAndValid(creditCard)) {
        throw new IllegalStateException("Payment information is not set or not valid.");
    }
    boolean isPaid = payWithCreditCard(creditCard, this.getPrice());
    if (isPaid) { this.setClosed(); }
    return isPaid;
}

public boolean processOrderWithPayPal(String email, String password) {
    if (isClosed()) { return true; }
    if (email == null || password == null || !email.equals(Paypal.DATA_BASE.get(password))) {
        throw new IllegalStateException("Payment information is not set or not valid.");
    }
    boolean isPaid = payWithPayPal(email, password, this.getPrice());
    if (isPaid) { this.setClosed(); }
    return isPaid;
}
```

**Issues Identified:**
- Duplicate validation logic
- Duplicate payment flow (check if closed, validate, pay, close)
- Different payment methods tied directly to FlightOrder
- Difficult to add new payment methods
- Hard to test each payment method independently

### Solution: Strategy Pattern

#### Step 1: Create Payment Strategy Interface

```java
package flight.reservation.payment;

/**
 * Strategy interface for different payment methods
 */
public interface PaymentStrategy {
    
    /**
     * Validate the payment information
     * @return true if payment information is valid
     */
    boolean validatePayment();
    
    /**
     * Process the payment for given amount
     * @param amount The amount to pay
     * @return true if payment successful
     * @throws IllegalStateException if payment fails
     */
    boolean pay(double amount) throws IllegalStateException;
}
```

#### Step 2: Implement Credit Card Strategy

```java
package flight.reservation.payment;

/**
 * Concrete strategy for credit card payment
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {
    
    private final CreditCard creditCard;
    
    public CreditCardPaymentStrategy(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
    
    @Override
    public boolean validatePayment() {
        return creditCard != null && creditCard.isValid();
    }
    
    @Override
    public boolean pay(double amount) throws IllegalStateException {
        if (!validatePayment()) {
            throw new IllegalStateException(
                "Credit card information is not set or not valid.");
        }
        
        double remainingAmount = creditCard.getAmount() - amount;
        if (remainingAmount < 0) {
            System.out.printf("Card limit reached - Balance: %f%n", remainingAmount);
            throw new IllegalStateException("Card limit reached");
        }
        
        System.out.println("Paying " + amount + " using Credit Card.");
        creditCard.setAmount(remainingAmount);
        return true;
    }
}
```

#### Step 3: Implement PayPal Strategy

```java
package flight.reservation.payment;

/**
 * Concrete strategy for PayPal payment
 */
public class PayPalPaymentStrategy implements PaymentStrategy {
    
    private final String email;
    private final String password;
    
    public PayPalPaymentStrategy(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    @Override
    public boolean validatePayment() {
        return email != null && password != null && 
               email.equals(Paypal.DATA_BASE.get(password));
    }
    
    @Override
    public boolean pay(double amount) throws IllegalStateException {
        if (!validatePayment()) {
            throw new IllegalStateException(
                "PayPal credentials are not set or not valid.");
        }
        
        System.out.println("Paying " + amount + " using PayPal.");
        return true;
    }
}
```

#### Step 4: Refactor FlightOrder to Use Strategy

```java
package flight.reservation.order;

import flight.reservation.Customer;
import flight.reservation.flight.ScheduledFlight;
import flight.reservation.payment.PaymentStrategy;
import flight.reservation.payment.CreditCard;
import flight.reservation.payment.CreditCardPaymentStrategy;
import flight.reservation.payment.PayPalPaymentStrategy;
import java.util.Date;
import java.util.Arrays;
import java.util.List;

public class FlightOrder extends Order {
    
    private final List<ScheduledFlight> flights;
    private static List<String> noFlyList = Arrays.asList("Peter", "Johannes");
    private PaymentStrategy paymentStrategy;
    
    public FlightOrder(List<ScheduledFlight> flights) {
        this.flights = flights;
    }
    
    /**
     * Set the payment strategy to be used for this order
     */
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    
    /**
     * Process the order using the configured payment strategy
     */
    public boolean processPayment() throws IllegalStateException {
        if (isClosed()) {
            return true; // Already paid
        }
        
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment strategy not set");
        }
        
        try {
            boolean isPaid = paymentStrategy.pay(this.getPrice());
            if (isPaid) {
                this.setClosed();
            }
            return isPaid;
        } catch (IllegalStateException e) {
            throw e;
        }
    }
    
    /**
     * Convenience method: Process with credit card
     */
    public boolean processOrderWithCreditCard(CreditCard creditCard) 
            throws IllegalStateException {
        setPaymentStrategy(new CreditCardPaymentStrategy(creditCard));
        return processPayment();
    }
    
    /**
     * Convenience method: Process with PayPal
     */
    public boolean processOrderWithPayPal(String email, String password) 
            throws IllegalStateException {
        setPaymentStrategy(new PayPalPaymentStrategy(email, password));
        return processPayment();
    }
    
    /**
     * Convenience method: Process with credit card details
     */
    public boolean processOrderWithCreditCardDetail(
            String number, Date expirationDate, String cvv) 
            throws IllegalStateException {
        CreditCard creditCard = new CreditCard(number, expirationDate, cvv);
        return processOrderWithCreditCard(creditCard);
    }
    
    public List<ScheduledFlight> getScheduledFlights() {
        return flights;
    }
    
    public static List<String> getNoFlyList() {
        return noFlyList;
    }
}
```

### Benefits of Strategy Pattern

1. **Reduced Code Duplication**: Payment flow logic is in one place
2. **Easy to Add Payments**: New payment methods are just new strategy classes
3. **Better Testing**: Each strategy can be tested independently
4. **Open/Closed Principle**: Open for extension, closed for modification
5. **Flexibility**: Payment method can be changed at runtime

### Before and After Code Comparison

**Before (100+ lines with duplication):**
```java
public boolean processOrderWithCreditCard(...) {
    if (isClosed()) { return true; }
    if (!cardIsPresentAndValid(...)) { throw ...; }
    boolean isPaid = payWithCreditCard(...);
    if (isPaid) { this.setClosed(); }
    return isPaid;
}

public boolean processOrderWithPayPal(...) {
    if (isClosed()) { return true; }
    if (email == null || ...) { throw ...; }
    boolean isPaid = payWithPayPal(...);
    if (isPaid) { this.setClosed(); }
    return isPaid;
}
```

**After (Clean and reusable):**
```java
public boolean processPayment() throws IllegalStateException {
    if (isClosed()) { return true; }
    boolean isPaid = paymentStrategy.pay(this.getPrice());
    if (isPaid) { this.setClosed(); }
    return isPaid;
}
```

---

## Summary of Patterns

| Pattern | Purpose | Complexity | Impact |
|---------|---------|-----------|--------|
| Factory | Centralize object creation | Low | High - Eliminates isinstance checks |
| Strategy | Encapsulate algorithms | Medium | High - Makes system extensible |
| Builder | Construct complex objects | Medium | Medium - Cleaner object creation |
| Adapter | Unified interface for incompatible objects | Medium | Medium - Improves polymorphism |
| Observer | Notify multiple objects of state changes | Medium-High | Medium - Enables event-driven design |
| Chain of Responsibility | Pass requests along chain | High | Medium - Flexible workflows |
