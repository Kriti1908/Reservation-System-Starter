# Design Patterns Analysis for Flight Reservation System

## Executive Summary
This document provides a comprehensive analysis of applicable design patterns for the Flight Reservation System codebase. The analysis identifies six relevant patterns and provides detailed reasoning for their application, including class diagrams showing the structure before and after implementing each pattern.

---

## 1. FACTORY PATTERN

### Current Problem
The codebase has scattered logic for aircraft instantiation across multiple classes. The `Flight` class contains complex type checking logic using `instanceof` statements to determine aircraft properties:

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

**Issues:**
- Aircraft creation is scattered throughout the codebase
- Inconsistent model/aircraft type representations
- Multiple `instanceof` checks scattered across classes
- Difficult to add new aircraft types
- Violates Open/Closed Principle

### Design Pattern Application: Factory Pattern

The Factory Pattern provides a centralized mechanism for creating aircraft objects, encapsulating the creation logic and making it easier to extend the system with new aircraft types.

#### Benefits:
1. **Centralized Creation Logic**: All aircraft instantiation happens in one place
2. **Easy Extensibility**: Adding new aircraft types requires minimal changes
3. **Type Safety**: Returns common interface/base class for all aircraft
4. **Reduced Code Duplication**: Eliminates scattered `instanceof` checks
5. **Single Responsibility**: Aircraft creation is a single concern

#### Drawbacks:
1. Additional abstraction layer adds complexity
2. May be overkill for simple aircraft creation
3. Requires interface hierarchy

### Class Diagram: Before Factory Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                        Flight                               │
├─────────────────────────────────────────────────────────────┤
│ - number: int                                               │
│ - departure: Airport                                        │
│ - arrival: Airport                                          │
│ - aircraft: Object                                          │
├─────────────────────────────────────────────────────────────┤
│ + Flight(int, Airport, Airport, Object)                    │
│ - isAircraftValid(Airport): boolean                         │
│ + getAircraft(): Object                                     │
└─────────────────────────────────────────────────────────────┘
         │
         │ Uses (scattered instanceof checks)
         │
         ├──────────────────┬──────────────────┬──────────────────┐
         │                  │                  │                  │
    ┌────────────┐  ┌───────────────┐  ┌──────────────┐  ┌──────────────────┐
    │PassengerPlane
    │PassengerPlane│  │   Helicopter  │  │PassengerDrone│  │    (No common    │
    └────────────┘  └───────────────┘  └──────────────┘  │     interface)   │
    - model         - model: String     - model: String   │                  │
    - capacity      - capacity: int     (hardcoded)       └──────────────────┘
                    - getModel()
                    - getCapacity()
```

### Class Diagram: After Factory Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                    <<interface>>                            │
│                      Aircraft                               │
├─────────────────────────────────────────────────────────────┤
│ + getModel(): String                                        │
│ + getPassengerCapacity(): int                               │
│ + getCrewCapacity(): int                                    │
└─────────────────────────────────────────────────────────────┘
         ▲           ▲           ▲
         │           │           │
         │           │           │
    ┌────────────┐  ┌───────────────┐  ┌──────────────┐
    │PassengerPlane│  │   Helicopter  │  │PassengerDrone│
    └────────────┘  └───────────────┘  └──────────────┘
    - model         - model: String     - model: String
    - capacity      - capacity: int     - capacity: int
    + getModel()    + getModel()        + getModel()
    + getCapacity() + getCapacity()     + getCapacity()

┌─────────────────────────────────────────────────────────────┐
│                   AircraftFactory                           │
├─────────────────────────────────────────────────────────────┤
│ + createAircraft(String model): Aircraft                    │
│ + getModelFromAircraft(Aircraft): String                    │
└─────────────────────────────────────────────────────────────┘
         │ creates
         │
         ▼
    All Aircraft implementations


┌─────────────────────────────────────────────────────────────┐
│                        Flight                               │
├─────────────────────────────────────────────────────────────┤
│ - number: int                                               │
│ - departure: Airport                                        │
│ - arrival: Airport                                          │
│ - aircraft: Aircraft (interface)                            │
├─────────────────────────────────────────────────────────────┤
│ + Flight(int, Airport, Airport, Aircraft)                  │
│ - isAircraftValid(Airport): boolean                         │
│ + getAircraft(): Aircraft                                   │
└─────────────────────────────────────────────────────────────┘
         │ uses
         │
         ▼
    AircraftFactory (via static methods)
```

### Implementation Impact:
- Eliminates all `instanceof` checks from Flight and ScheduledFlight
- Aircraft property access becomes uniform through interface
- Adding new aircraft types requires only a new factory method
- Reduces cyclomatic complexity in critical methods

---

## 2. STRATEGY PATTERN

### Current Problem
The `FlightOrder` class contains multiple payment processing methods mixed with business logic:

```java
public boolean processOrderWithCreditCard(CreditCard creditCard) throws IllegalStateException {
    if (isClosed()) { return true; }
    if (!cardIsPresentAndValid(creditCard)) {
        throw new IllegalStateException("Payment information is not set or not valid.");
    }
    boolean isPaid = payWithCreditCard(creditCard, this.getPrice());
    if (isPaid) { this.setClosed(); }
    return isPaid;
}

public boolean processOrderWithPayPal(String email, String password) throws IllegalStateException {
    if (isClosed()) { return true; }
    if (email == null || password == null || !email.equals(Paypal.DATA_BASE.get(password))) {
        throw new IllegalStateException("Payment information is not set or not valid.");
    }
    boolean isPaid = payWithPayPal(email, password, this.getPrice());
    if (isPaid) { this.setClosed(); }
    return isPaid;
}
```

**Issues:**
- Different payment methods have different validation logic
- Payment logic is tightly coupled to FlightOrder
- Difficult to add new payment methods
- Business logic (order closure) is mixed with payment details
- Violates Single Responsibility Principle

### Design Pattern Application: Strategy Pattern

The Strategy Pattern encapsulates payment algorithms in separate classes, allowing flexible selection and addition of payment methods.

#### Benefits:
1. **Separation of Concerns**: Payment logic is isolated from order logic
2. **Easy to Add Payment Methods**: New strategies can be added without modifying FlightOrder
3. **Runtime Flexibility**: Payment method can be selected at runtime
4. **Testability**: Each payment strategy can be tested independently
5. **Reduced Code Duplication**: Common payment flow is extracted
6. **Open/Closed Principle**: Open for extension (new payments), closed for modification

#### Drawbacks:
1. Creates more classes, increasing complexity
2. Slight performance overhead due to polymorphism
3. May be complex for simple scenarios

### Class Diagram: Before Strategy Pattern

```
┌──────────────────────────────────────────────────────────────┐
│                    FlightOrder (extends Order)               │
├──────────────────────────────────────────────────────────────┤
│ - flights: List<ScheduledFlight>                             │
│ - noFlyList: static List<String>                             │
├──────────────────────────────────────────────────────────────┤
│ + processOrderWithCreditCard(CreditCard): boolean            │
│ + processOrderWithCreditCardDetail(String, Date, String): bool
│ + payWithCreditCard(CreditCard, double): boolean             │
│ + processOrderWithPayPal(String, String): boolean            │
│ + payWithPayPal(String, String, double): boolean             │
│ - cardIsPresentAndValid(CreditCard): boolean                 │
│ - isOrderValid(...): boolean                                 │
└──────────────────────────────────────────────────────────────┘
         │ uses directly
         │
         ├──────────────────┬──────────────────┐
         │                  │                  │
    ┌────────────┐  ┌─────────────────┐
    │ CreditCard │  │    Paypal       │
    └────────────┘  └─────────────────┘
    (No abstraction) (Data only)

[Problem: Duplicate validation, mixed concerns, hard to extend]
```

### Class Diagram: After Strategy Pattern

```
┌──────────────────────────────────────────────────────────────┐
│                   <<interface>>                              │
│                 PaymentStrategy                              │
├──────────────────────────────────────────────────────────────┤
│ + validatePayment(): boolean                                 │
│ + pay(double amount): boolean                                │
└──────────────────────────────────────────────────────────────┘
         ▲
         │ implements
         │
    ┌────────────────────────┬────────────────────────┐
    │                        │                        │
┌──────────────────────┐  ┌────────────────────────┐
│CreditCardPayment     │  │   PayPalPayment       │
│  implements          │  │   implements          │
│  PaymentStrategy     │  │   PaymentStrategy     │
├──────────────────────┤  ├────────────────────────┤
│ - creditCard: CC     │  │ - email: String        │
│ - order: FlightOrder │  │ - password: String     │
│                      │  │ - order: FlightOrder   │
├──────────────────────┤  ├────────────────────────┤
│ + validatePayment(): │  │ + validatePayment():   │
│     boolean          │  │     boolean            │
│ + pay(double): bool  │  │ + pay(double): bool    │
└──────────────────────┘  └────────────────────────┘

┌──────────────────────────────────────────────────────────────┐
│                    FlightOrder (extends Order)               │
├──────────────────────────────────────────────────────────────┤
│ - flights: List<ScheduledFlight>                             │
│ - paymentStrategy: PaymentStrategy                           │
├──────────────────────────────────────────────────────────────┤
│ + setPaymentStrategy(PaymentStrategy): void                  │
│ + processPayment(): boolean                                  │
│ - isOrderValid(...): boolean                                 │
└──────────────────────────────────────────────────────────────┘
         │ uses (composition)
         │
         ▼
    PaymentStrategy (interface)
```

### Implementation Impact:
- Eliminates duplicate validation code
- Payment logic is now isolated and testable
- New payment methods (e.g., Apple Pay, Google Pay) can be added as new classes
- Order processing becomes simpler and more maintainable
- Follows Open/Closed Principle

---

## 3. BUILDER PATTERN

### Current Problem
The `FlightOrder` creation process is complex and scattered across the `Customer.createOrder()` method:

```java
public FlightOrder createOrder(List<String> passengerNames, List<ScheduledFlight> flights, double price) {
    if (!isOrderValid(passengerNames, flights)) {
        throw new IllegalStateException("Order is not valid");
    }
    FlightOrder order = new FlightOrder(flights);
    order.setCustomer(this);
    order.setPrice(price);
    List<Passenger> passengers = passengerNames
            .stream()
            .map(Passenger::new)
            .collect(Collectors.toList());
    order.setPassengers(passengers);
    order.getScheduledFlights().forEach(scheduledFlight -> scheduledFlight.addPassengers(passengers));
    orders.add(order);
    return order;
}
```

**Issues:**
- Multiple setter calls required after object creation
- Creation logic is scattered across classes
- No clear sequence for complex object construction
- Difficult to create orders with different configurations
- Validation is spread across different classes
- Fluent interface would be more intuitive

### Design Pattern Application: Builder Pattern

The Builder Pattern separates order construction from its representation, making complex object creation clearer and more flexible.

#### Benefits:
1. **Clearer Syntax**: Fluent interface for building orders
2. **Validation Centralization**: All validation happens during construction
3. **Immutability Support**: Can make Order immutable after building
4. **Multiple Configurations**: Different order types can be built flexibly
5. **Reduced Setter Calls**: No need for multiple setters
6. **Better Readability**: Code intention is clearer

#### Drawbacks:
1. Increased number of classes
2. More code verbosity initially
3. Memory overhead for builder object

### Class Diagram: Before Builder Pattern

```
┌──────────────────────────────────────┐
│           Customer                   │
├──────────────────────────────────────┤
│ - name: String                       │
│ - email: String                      │
│ - orders: List<Order>                │
├──────────────────────────────────────┤
│ + createOrder(...): FlightOrder      │
│ - isOrderValid(...): boolean         │
└──────────────────────────────────────┘
         │ creates (scattered logic)
         │
         ▼
┌──────────────────────────────────────┐
│         FlightOrder                  │
├──────────────────────────────────────┤
│ - flights: List<ScheduledFlight>     │
│ - customer: Customer                 │
│ - passengers: List<Passenger>        │
│ - price: double                      │
│ - isClosed: boolean                  │
├──────────────────────────────────────┤
│ + setCustomer(Customer): void        │
│ + setPrice(double): void             │
│ + setPassengers(List): void          │
│ + getScheduledFlights(): List        │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│        ScheduledFlight               │
├──────────────────────────────────────┤
│ + addPassengers(List): void          │
└──────────────────────────────────────┘
```

### Class Diagram: After Builder Pattern

```
┌──────────────────────────────────────┐
│         Customer                     │
├──────────────────────────────────────┤
│ - name: String                       │
│ - email: String                      │
│ - orders: List<Order>                │
├──────────────────────────────────────┤
│ + createOrderBuilder(): OrderBuilder │
│ - isOrderValid(...): boolean         │
└──────────────────────────────────────┘
         │ creates builder
         │
         ▼
┌──────────────────────────────────────────────┐
│              OrderBuilder                    │
├──────────────────────────────────────────────┤
│ - flights: List<ScheduledFlight>             │
│ - passengerNames: List<String>               │
│ - price: double                              │
│ - customer: Customer                         │
├──────────────────────────────────────────────┤
│ + withFlights(List): OrderBuilder            │
│ + withPassengers(List): OrderBuilder         │
│ + withPrice(double): OrderBuilder            │
│ + withCustomer(Customer): OrderBuilder       │
│ + build(): FlightOrder                       │
│ - validate(): void                           │
│ - createPassengers(): List<Passenger>        │
│ - registerWithScheduledFlights(): void       │
└──────────────────────────────────────────────┘
         │ builds
         │
         ▼
┌──────────────────────────────────────┐
│         FlightOrder                  │
├──────────────────────────────────────┤
│ - flights: List<ScheduledFlight>     │
│ - customer: Customer                 │
│ - passengers: List<Passenger>        │
│ - price: double                      │
│ - isClosed: boolean                  │
├──────────────────────────────────────┤
│ [All set during construction]         │
│ No direct setters needed              │
└──────────────────────────────────────┘
```

### Implementation Impact:
- Creates more intuitive syntax: `customer.createOrderBuilder().withFlights(...).withPassengers(...).withPrice(...).build()`
- All validation happens in `build()` method
- Reduces complexity of order creation
- Makes order construction flexible and extensible

---

## 4. ADAPTER PATTERN

### Current Problem
The different aircraft types have inconsistent interfaces:
- `PassengerPlane`: public fields (`model`, `passengerCapacity`, `crewCapacity`)
- `Helicopter`: private fields with getter methods
- `PassengerDrone`: only model, hardcoded capacity (4)

```java
// Inconsistent access patterns in ScheduledFlight
public int getCapacity() throws NoSuchFieldException {
    if (this.aircraft instanceof PassengerPlane) {
        return ((PassengerPlane) this.aircraft).passengerCapacity; // Direct field access
    }
    if (this.aircraft instanceof Helicopter) {
        return ((Helicopter) this.aircraft).getPassengerCapacity(); // Getter method
    }
    if (this.aircraft instanceof PassengerDrone) {
        return 4; // Hardcoded
    }
    throw new NoSuchFieldException("this aircraft has no information about its capacity");
}
```

**Issues:**
- Inconsistent aircraft interfaces
- Mix of direct field access and getter methods
- Multiple `instanceof` checks scattered throughout
- Difficult to use different aircraft types interchangeably
- Violates Liskov Substitution Principle

### Design Pattern Application: Adapter Pattern

The Adapter Pattern creates a uniform interface for different aircraft types, allowing them to be used interchangeably.

#### Benefits:
1. **Uniform Interface**: All aircraft can be accessed the same way
2. **No Type Checking**: Eliminates `instanceof` checks
3. **Integration**: Legacy aircraft classes can be used without modification
4. **Substitutability**: Aircraft can be used polymorphically
5. **Reduced Complexity**: Consistent access patterns throughout code

#### Drawbacks:
1. Additional wrapper classes needed
2. Slight performance overhead (method calls)
3. More objects in memory

### Class Diagram: Before Adapter Pattern

```
┌─────────────────────┐
│  PassengerPlane     │
├─────────────────────┤
│ + model: String     │
│ + passenger: int    │
│ + crewCapacity: int │
└─────────────────────┘

┌─────────────────────┐
│   Helicopter        │
├─────────────────────┤
│ - model: String     │
│ - capacity: int     │
├─────────────────────┤
│ + getModel()        │
│ + getCapacity()     │
└─────────────────────┘

┌─────────────────────┐
│ PassengerDrone      │
├─────────────────────┤
│ - model: String     │
├─────────────────────┤
│ (capacity hardcoded)│
└─────────────────────┘

[Problem: Inconsistent interfaces, scattered instanceof checks]

ScheduledFlight uses all three with different patterns
```

### Class Diagram: After Adapter Pattern

```
┌────────────────────────────────────┐
│     <<interface>>                  │
│      AircraftAdapter               │
├────────────────────────────────────┤
│ + getModel(): String               │
│ + getPassengerCapacity(): int      │
│ + getCrewCapacity(): int           │
│ + getType(): String                │
└────────────────────────────────────┘
           ▲
           │ implements
           │
    ┌──────────────┬──────────────┬──────────────┐
    │              │              │              │
┌───────────────┐ ┌────────────┐ ┌───────────────┐
│PassengerPlane │ │ Helicopter │ │PassengerDrone │
│   Adapter     │ │  Adapter   │ │   Adapter     │
├───────────────┤ ├────────────┤ ├───────────────┤
│ - aircraft:   │ │ - aircraft:│ │ - aircraft:   │
│   PPl         │ │   Heli     │ │   Drone       │
├───────────────┤ ├────────────┤ ├───────────────┤
│ + getModel()  │ │+ getModel()│ │ + getModel()  │
│ + getPass...()│ │+ getPass..()│ │ + getPass...()│
│ + getCrewCap()│ │+ getCrewCap()│ │ + getCrewCap()│
└───────────────┘ └────────────┘ └───────────────┘
        │                │               │
        ▼                ▼               ▼
  ┌─────────────┐  ┌──────────┐  ┌──────────────┐
  │PassengerPlane│ │Helicopter│  │PassengerDrone│
  │ (original)  │ │(original)│  │  (original)  │
  └─────────────┘  └──────────┘  └──────────────┘

ScheduledFlight & Flight use AircraftAdapter interface
No instanceof checks needed
```

### Implementation Impact:
- Eliminates all `instanceof` checks from `ScheduledFlight`
- `getCapacity()` and `getCrewMemberCapacity()` become simple interface calls
- New aircraft types need only an adapter class
- Code becomes polymorphic and more maintainable

---

## 5. OBSERVER PATTERN

### Current Problem
When a flight is booked, multiple state changes occur (passenger added, capacity reduced, price might change), but there's no mechanism to notify interested parties. Current code:

```java
public FlightOrder createOrder(List<String> passengerNames, List<ScheduledFlight> flights, double price) {
    // ... validation ...
    FlightOrder order = new FlightOrder(flights);
    // ... setting properties ...
    order.getScheduledFlights().forEach(scheduledFlight -> scheduledFlight.addPassengers(passengers));
    // No observers/listeners informed
    orders.add(order);
    return order;
}
```

**Issues:**
- No mechanism to track state changes in flights
- Difficult to add side effects (e.g., notifications, analytics, logging)
- Tightly coupled components
- No event-driven capabilities

### Design Pattern Application: Observer Pattern

The Observer Pattern allows multiple objects to be notified of state changes in flights and orders.

#### Benefits:
1. **Loose Coupling**: Components communicate through events, not direct references
2. **Extensibility**: New observers can be added without modifying Flight/Order classes
3. **Separation of Concerns**: Business logic separated from side effects
4. **Notification System**: Multiple listeners can react to the same event
5. **Flexible Reactions**: Different observers can respond differently to same event

#### Drawbacks:
1. Memory overhead for observer references
2. Harder to debug event flow
3. Memory leak risks if observers not unregistered properly
4. Events may be triggered unexpectedly

### Class Diagram: Before Observer Pattern

```
┌──────────────────────────────────────┐
│       ScheduledFlight                │
├──────────────────────────────────────┤
│ - passengers: List<Passenger>        │
│ - currentPrice: double               │
├──────────────────────────────────────┤
│ + addPassengers(List): void          │
│ + removePassengers(List): void       │
│ + setCurrentPrice(double): void      │
│ [No notification mechanism]          │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│         FlightOrder                  │
├──────────────────────────────────────┤
│ - isClosed: boolean                  │
├──────────────────────────────────────┤
│ + setClosed(): void                  │
│ [No notification mechanism]          │
└──────────────────────────────────────┘

[Problem: No event notification, hard to implement listeners]
```

### Class Diagram: After Observer Pattern

```
┌────────────────────────────────────┐
│   <<interface>>                    │
│   FlightObserver                   │
├────────────────────────────────────┤
│ + onPassengersAdded(event): void   │
│ + onPassengersRemoved(event): void │
│ + onPriceChanged(event): void      │
│ + onFlightCancelled(event): void   │
└────────────────────────────────────┘
           ▲
           │ implements
           │
    ┌──────────────┬──────────────┬─────────────┐
    │              │              │             │
┌──────────┐  ┌────────┐  ┌────────────┐  ┌───────────┐
│Analytics │  │Logging │  │Notification│  │Inventory  │
│Observer  │  │Observer│  │ Observer   │  │Observer   │
└──────────┘  └────────┘  └────────────┘  └───────────┘

┌─────────────────────────────────────────────────┐
│          ScheduledFlight                        │
├─────────────────────────────────────────────────┤
│ - passengers: List<Passenger>                   │
│ - currentPrice: double                          │
│ - observers: List<FlightObserver>               │
├─────────────────────────────────────────────────┤
│ + addObserver(FlightObserver): void             │
│ + removeObserver(FlightObserver): void          │
│ + addPassengers(List): void                     │
│ + removePassengers(List): void                  │
│ + setCurrentPrice(double): void                 │
│ - notifyObservers(event): void                  │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│          FlightOrder                            │
├─────────────────────────────────────────────────┤
│ - isClosed: boolean                             │
│ - observers: List<OrderObserver>                │
├─────────────────────────────────────────────────┤
│ + addObserver(OrderObserver): void              │
│ + removeObserver(OrderObserver): void           │
│ + setClosed(): void                             │
│ - notifyObservers(event): void                  │
└─────────────────────────────────────────────────┘
```

### Implementation Impact:
- Flight and Order classes can notify observers of state changes
- New functionality (notifications, analytics, logging) can be added as observers
- Components remain loosely coupled
- Event-driven architecture enables reactive programming

---

## 6. COMMAND PATTERN / CHAIN OF RESPONSIBILITY

### Current Problem
The order processing workflow (validation → payment → confirmation) is hardcoded and tightly coupled:

```java
public boolean processOrderWithCreditCard(CreditCard creditCard) throws IllegalStateException {
    if (isClosed()) { return true; }
    if (!cardIsPresentAndValid(creditCard)) {
        throw new IllegalStateException("Payment information is not set or not valid.");
    }
    boolean isPaid = payWithCreditCard(creditCard, this.getPrice());
    if (isPaid) { this.setClosed(); }
    return isPaid;
}
```

**Issues:**
- Order processing steps are hardcoded
- Difficult to add new processing steps
- Cannot reorder or conditionally skip steps
- Difficult to undo operations
- Validation and payment logic are mixed

### Design Pattern Application: Chain of Responsibility Pattern

The Chain of Responsibility Pattern passes requests along a chain of handlers, allowing flexible order processing workflows.

#### Benefits:
1. **Flexible Workflow**: Processing steps can be dynamically configured
2. **Step Isolation**: Each step is independent and reusable
3. **Easy Extensibility**: New processing steps can be added without modifying existing ones
4. **Step Reordering**: Steps can be reordered as needed
5. **Conditional Processing**: Steps can conditionally pass or stop the chain

#### Drawbacks:
1. More complex to understand
2. Processing not guaranteed if chain incomplete
3. Performance overhead with multiple handlers
4. Harder to debug processing flow

### Class Diagram: Before Chain of Responsibility

```
┌──────────────────────────────────────┐
│         FlightOrder                  │
├──────────────────────────────────────┤
│ + processOrderWithCreditCard(): bool │
│ + processOrderWithPayPal(): bool     │
│ - cardIsPresentAndValid(): bool      │
│ - payWithCreditCard(): bool          │
│ - payWithPayPal(): bool              │
│ [All logic hardcoded together]       │
└──────────────────────────────────────┘

[Problem: Hardcoded flow, not extensible]
```

### Class Diagram: After Chain of Responsibility

```
┌────────────────────────────────────┐
│   <<abstract>>                     │
│   OrderProcessingHandler           │
├────────────────────────────────────┤
│ # nextHandler: Handler             │
│ # order: FlightOrder               │
├────────────────────────────────────┤
│ + setNextHandler(Handler): void    │
│ + handle(OrderContext): boolean    │
│ # doHandle(OrderContext): boolean  │
└────────────────────────────────────┘
           ▲
           │ extends
           │
    ┌──────────────┬──────────────┬──────────────┐
    │              │              │              │
┌─────────────┐┌──────────┐ ┌──────────┐ ┌────────────┐
│ Validation  ││ Payment  │ │ Closure  │ │ Confirmation
│  Handler    ││ Handler  │ │ Handler  │ │  Handler
├─────────────┤├──────────┤ ├──────────┤ ├────────────┤
│ + doHandle()││+ doHandle│ │+ doHandle│ │+ doHandle()│
└─────────────┘└──────────┘ └──────────┘ └────────────┘
       │              │            │            │
       └──────────────┴────────────┴────────────┘
              Chain flow

FlightOrder creates and configures the chain:
chainHead → ValidationHandler → PaymentHandler → ClosureHandler
```

### Implementation Impact:
- Processing steps become modular and reusable
- New steps (e.g., fraud detection, approval) can be added as new handlers
- Steps can be added/removed/reordered dynamically
- Each handler has single responsibility
- Code becomes more maintainable and testable

---

## Summary Table: Patterns and Their Impact

| Pattern | Problem Addressed | Key Benefit | Implementation Complexity |
|---------|-----------------|-------------|--------------------------|
| **Factory** | Scattered aircraft instantiation | Centralized creation, eliminates instanceof | Low |
| **Strategy** | Hardcoded payment methods | Flexible payment processing | Medium |
| **Builder** | Complex order creation | Cleaner object construction | Medium |
| **Adapter** | Inconsistent aircraft interfaces | Uniform interface, polymorphism | Medium |
| **Observer** | No state change notifications | Event-driven architecture | Medium-High |
| **Chain of Responsibility** | Hardcoded processing workflow | Flexible, modular processing | High |

---

## Implementation Priority

Based on impact and complexity:

1. **High Priority**: Factory Pattern & Adapter Pattern
   - Low complexity, high impact
   - Eliminates scattered instanceof checks
   - Improves type safety and extensibility

2. **Medium Priority**: Strategy Pattern & Builder Pattern
   - Medium complexity, high impact
   - Improves code organization
   - Makes system more extensible

3. **Lower Priority**: Observer Pattern & Chain of Responsibility
   - Higher complexity, situational benefit
   - Implement when advanced requirements emerge
   - Useful for enterprise features (notifications, workflows)

---

## Recommended Implementation Order

1. Implement **Factory Pattern** for aircraft creation
2. Implement **Adapter Pattern** for uniform aircraft interface
3. Refactor with **Strategy Pattern** for payment processing
4. Implement **Builder Pattern** for order creation
5. Add **Observer Pattern** for notifications and analytics
6. Implement **Chain of Responsibility** for complex workflows

This order minimizes dependencies and allows incremental improvements to the codebase.

---

## Conclusion

The Flight Reservation System codebase has multiple areas where design patterns can significantly improve maintainability, extensibility, and code quality. By systematically applying these patterns, the system can become more robust, testable, and easier to extend with new features. The patterns address fundamental design issues such as type checking, tight coupling, complex object creation, and hardcoded workflows.
