# Detailed UML Diagrams for Design Patterns

## 1. FACTORY PATTERN - Complete UML

### Class Diagram: Before Factory Pattern

```
┌─────────────────────────────────────────────────────────────────┐
│                       Flight                                    │
├─────────────────────────────────────────────────────────────────┤
│ ATTRIBUTES:                                                     │
│ - number: int                                                   │
│ - departure: Airport                                            │
│ - arrival: Airport                                              │
│ - aircraft: Object  ← PROBLEM: Type is Object, not typed!      │
├─────────────────────────────────────────────────────────────────┤
│ METHODS:                                                        │
│ + Flight(int, Airport, Airport, Object)                        │
│ - checkValidity(): void                                         │
│ - isAircraftValid(Airport): boolean                             │
│   └─ CONTAINS: instanceof checks for all aircraft types ×5    │
│ + getAircraft(): Object                                         │
│ + getNumber(): int                                              │
│ + getDeparture(): Airport                                       │
│ + getArrival(): Airport                                         │
│ + toString(): String                                            │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────────┐  ┌──────────────────────────┐
│    PassengerPlane        │  │      Helicopter          │
├──────────────────────────┤  ├──────────────────────────┤
│ + model: String          │  │ - model: String          │
│ + passengerCapacity: int │  │ - passengerCapacity: int │
│ + crewCapacity: int      │  ├──────────────────────────┤
├──────────────────────────┤  │ + getModel(): String     │
│ + PassengerPlane(String) │  │ + getPassengerCapacity() │
└──────────────────────────┘  │   : int                  │
                             └──────────────────────────┘

┌──────────────────────────┐
│   PassengerDrone         │
├──────────────────────────┤
│ - model: String          │
├──────────────────────────┤
│ + PassengerDrone(String) │
│ (capacity hardcoded to 4)│
└──────────────────────────┘

PROBLEMS IDENTIFIED:
❌ Flight accepts Object type - Not type-safe
❌ Multiple instanceof checks in isAircraftValid()
❌ Aircraft classes have inconsistent interfaces
❌ Cannot use polymorphism
❌ Adding new aircraft type requires code changes in Flight
```

### Class Diagram: After Factory Pattern

```
                      ┌─────────────────────────┐
                      │   <<interface>>         │
                      │     Aircraft            │
                      ├─────────────────────────┤
                      │ + getModel(): String    │
                      │ + getPassenger          │
                      │   Capacity(): int       │
                      │ + getCrewCapacity(): int│
                      │ + getAircraftType()     │
                      │   : String              │
                      └──────────┬──────────────┘
                                 △
                    ┌────────────┼────────────┐
                    │            │            │
        ┌───────────────────┐ ┌──────────┐ ┌────────────────┐
        │ PassengerPlane    │ │Helicopter│ │ PassengerDrone │
        │  implements       │ │implements│ │   implements   │
        │  Aircraft         │ │ Aircraft │ │   Aircraft     │
        ├───────────────────┤ ├──────────┤ ├────────────────┤
        │ - model: String   │ │- model:  │ │ - model:       │
        │ - passengerCap:   │ │  String  │ │   String       │
        │   int             │ │- passen  │ │ - (constants)  │
        │ - crewCapacity:   │ │  gerCap: │ │   PASSENGER: 4 │
        │   int             │ │  int     │ │   CREW: 0      │
        ├───────────────────┤ ├──────────┤ ├────────────────┤
        │ + getModel()      │ │+ getModel│ │ + getModel()   │
        │ + getPassenger... │ │+ getPass │ │ + getPassenger │
        │ + getCrewCap...   │ │+ getCrew │ │ + getCrewCap.. │
        │ + getAircraftType │ │+ getAir..│ │ + getAircraftTy│
        └───────────────────┘ └──────────┘ └────────────────┘

┌────────────────────────────────────────────────┐
│        AircraftFactory (Factory)               │
├────────────────────────────────────────────────┤
│ STATIC METHODS:                                │
│ + createAircraft(String model): Aircraft       │
│   └─ Creates appropriate Aircraft instance    │
│      based on model string                    │
│ + isPassengerPlaneModel(String): boolean      │
│ + isHelicopterModel(String): boolean          │
│ + isPassengerDroneModel(String): boolean      │
│ + getAllowedModels(): String[]                │
│ + isModelSupported(String): boolean           │
└────────────────────────────────────────────────┘
          │ creates instances of
          │
          └──> Returns Aircraft interface
               (polymorphic return type)

┌─────────────────────────────────────────────────┐
│          Flight (Refactored)                    │
├─────────────────────────────────────────────────┤
│ ATTRIBUTES:                                     │
│ - number: int                                   │
│ - departure: Airport                            │
│ - arrival: Airport                              │
│ - aircraft: Aircraft  ← NOW: Typed as Aircraft! │
├─────────────────────────────────────────────────┤
│ METHODS:                                        │
│ + Flight(int, Airport, Airport, Aircraft)      │
│ - checkValidity(): void                         │
│ - isAircraftValid(Airport): boolean             │
│   └─ NO instanceof checks! Uses aircraft.      │
│      getModel() directly                       │
│ + getAircraft(): Aircraft                       │
│ + getNumber(): int                              │
│ + getDeparture(): Airport                       │
│ + getArrival(): Airport                         │
│ + toString(): String                            │
└─────────────────────────────────────────────────┘
     │ uses
     │
     ▼ Aircraft interface
     (can be any Aircraft implementation)

BENEFITS ACHIEVED:
✅ Type-safe: Aircraft is properly typed
✅ Polymorphic: Works with any Aircraft impl
✅ Centralized: All creation in AircraftFactory
✅ Extensible: New aircraft just implement Aircraft
✅ No instanceof: Uses interface method calls
✅ Single Responsibility: Factory handles creation
```

---

## 2. STRATEGY PATTERN - Complete UML

### Class Diagram: Before Strategy Pattern

```
┌────────────────────────────────────────────────────────────┐
│                     FlightOrder                            │
│                  (extends Order)                           │
├────────────────────────────────────────────────────────────┤
│ ATTRIBUTES:                                                │
│ - flights: List<ScheduledFlight>                           │
│ - noFlyList: static List<String>                           │
│ - customer: Customer (inherited)                           │
│ - price: double (inherited)                                │
│ - isClosed: boolean (inherited)                            │
├────────────────────────────────────────────────────────────┤
│ PROBLEMATIC METHODS:                                       │
│                                                            │
│ + processOrderWithCreditCard(CreditCard): boolean          │
│   ├─ Check if already closed                              │
│   ├─ Validate credit card                                 │
│   ├─ Call payWithCreditCard()                             │
│   └─ Close order if success                               │
│   └─ PROBLEM: Duplicate flow logic ×2                     │
│                                                            │
│ + processOrderWithPayPal(String, String): boolean          │
│   ├─ Check if already closed                              │
│   ├─ Validate PayPal credentials                          │
│   ├─ Call payWithPayPal()                                 │
│   └─ Close order if success                               │
│   └─ PROBLEM: Same flow, different validation             │
│                                                            │
│ - payWithCreditCard(CreditCard, double): boolean           │
│   └─ Direct payment logic                                 │
│                                                            │
│ - payWithPayPal(String, String, double): boolean           │
│   └─ Direct payment logic                                 │
│   └─ PROBLEM: No abstraction, hardcoded                   │
│                                                            │
│ - cardIsPresentAndValid(CreditCard): boolean              │
│   └─ Validation logic mixed with payment                  │
│                                                            │
│ + getScheduledFlights(): List<ScheduledFlight>            │
│ + getNoFlyList(): List<String>                            │
└────────────────────────────────────────────────────────────┘

Used directly by:
- CreditCard (data only, no behavior)
- Paypal (database class, no behavior)

PROBLEMS:
❌ Duplicate validation logic (CreditCard vs PayPal)
❌ Duplicate payment flow logic
❌ Tight coupling: payment logic mixed in FlightOrder
❌ Hard to add new payment methods (e.g., ApplePay)
❌ Hard to test each payment method independently
❌ Difficult to reuse payment logic elsewhere
❌ Violates Single Responsibility Principle
❌ Violates Open/Closed Principle
```

### Class Diagram: After Strategy Pattern

```
                    ┌──────────────────────────┐
                    │   <<interface>>          │
                    │  PaymentStrategy         │
                    ├──────────────────────────┤
                    │ + validatePayment()      │
                    │   : boolean              │
                    │ + pay(double amount)     │
                    │   : boolean              │
                    └───────────┬──────────────┘
                                △
                ┌───────────────┼───────────────┐
                │               │               │
    ┌──────────────────────┐ ┌────────────────────────┐
    │ CreditCardPayment    │ │ PayPalPaymentStrategy  │
    │     Strategy         │ │                        │
    │  implements          │ │  implements            │
    │  PaymentStrategy     │ │  PaymentStrategy       │
    ├──────────────────────┤ ├────────────────────────┤
    │ ATTRIBUTES:          │ │ ATTRIBUTES:            │
    │ - creditCard:        │ │ - email: String        │
    │   CreditCard         │ │ - password: String     │
    │ - order: FlightOrder │ │ - order: FlightOrder   │
    ├──────────────────────┤ ├────────────────────────┤
    │ METHODS:             │ │ METHODS:               │
    │ + validatePayment(): │ │ + validatePayment():   │
    │     boolean          │ │     boolean            │
    │   └─ Check card      │ │   └─ Check in DB       │
    │      validity        │ │      credentials       │
    │ + pay(double):       │ │ + pay(double):         │
    │     boolean          │ │     boolean            │
    │   └─ Deduct from     │ │   └─ Process PayPal    │
    │      card amount     │ │      transaction       │
    └──────────────────────┘ └────────────────────────┘

               (Future: Easily add new strategies)
                    │
        ┌───────────┼───────────┐
        │           │           │
    ┌─────────┐ ┌─────────┐ ┌──────────┐
    │ ApplePay│ │GooglePay│ │ BitCoin  │
    │Strategy │ │Strategy │ │ Strategy │
    └─────────┘ └─────────┘ └──────────┘

┌──────────────────────────────────────────────────┐
│         FlightOrder (Refactored)                 │
│           (extends Order)                        │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ - flights: List<ScheduledFlight>                 │
│ - paymentStrategy: PaymentStrategy  ← NEW!      │
│   (uses composition, not inheritance)            │
├──────────────────────────────────────────────────┤
│ METHODS:                                         │
│                                                  │
│ + setPaymentStrategy(PaymentStrategy): void     │
│   └─ Set strategy at runtime                    │
│                                                  │
│ + processPayment(): boolean  ← SIMPLIFIED!      │
│   ├─ Check if already closed                    │
│   ├─ Call paymentStrategy.pay(price)            │
│   └─ Close order if success                     │
│   └─ BENEFIT: Single flow, reusable!            │
│                                                  │
│ + processOrderWithCreditCard(CreditCard)        │
│   └─ Create strategy and call processPayment() │
│   └─ BENEFIT: Thin convenience method           │
│                                                  │
│ + processOrderWithPayPal(String, String)        │
│   └─ Create strategy and call processPayment() │
│   └─ BENEFIT: Thin convenience method           │
│                                                  │
│ + processOrderWithCreditCardDetail(...)         │
│   └─ Create CreditCard and call with CC         │
│                                                  │
│ + getScheduledFlights(): List<ScheduledFlight>  │
│ + getNoFlyList(): List<String>                  │
└──────────────────────────────────────────────────┘
        │ uses
        │
        ▼
    PaymentStrategy (interface)
        │ can be any implementation
        ├─> CreditCardPaymentStrategy
        ├─> PayPalPaymentStrategy
        └─> Any future strategy

BENEFITS ACHIEVED:
✅ No code duplication
✅ Flexible: Can change strategy at runtime
✅ Extensible: New payment methods as new classes
✅ Testable: Each strategy independently testable
✅ Single Responsibility: Each class has one reason
✅ Open/Closed Principle: Open for extension
✅ Loose Coupling: FlightOrder depends on abstraction
```

---

## 3. BUILDER PATTERN - Complete UML

### Class Diagram: Before Builder Pattern

```
┌──────────────────────────────────────────────────┐
│              Customer                            │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ - name: String                                   │
│ - email: String                                  │
│ - orders: List<Order>                            │
├──────────────────────────────────────────────────┤
│ PROBLEMATIC METHOD:                              │
│                                                  │
│ + createOrder(List<String>, List<...>, double)  │
│   : FlightOrder                                  │
│   │                                              │
│   ├─ Validate order (complex logic)              │
│   ├─ Create FlightOrder with flights             │
│   ├─ setCustomer(this)                           │
│   ├─ setPrice(price)                             │
│   ├─ Create List<Passenger> from names           │
│   ├─ setPassengers(passengers)                   │
│   ├─ Call scheduledFlight.addPassengers() ×N    │
│   ├─ orders.add(order)                           │
│   └─ return order                                │
│   └─ PROBLEM: Mixed responsibilities!           │
│                                                  │
│ - isOrderValid(...): boolean                     │
│   └─ Validation scattered across classes        │
│                                                  │
└──────────────────────────────────────────────────┘
         │ creates
         │
         ▼
┌──────────────────────────────────────────────────┐
│           FlightOrder                            │
│         (extends Order)                          │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ - flights: List<ScheduledFlight>                 │
│ - customer: Customer (inherited)                 │
│ - passengers: List<Passenger> (inherited)        │
│ - price: double (inherited)                      │
│ - isClosed: boolean (inherited)                  │
├──────────────────────────────────────────────────┤
│ PROBLEMS:                                        │
│ ❌ Multiple setters required after creation     │
│ ❌ No way to enforce valid state                │
│ ❌ No fluent interface                           │
│ ❌ Order of operations matters but not enforced │
│ ❌ Difficult to create variations               │
│                                                  │
│ + FlightOrder(List<ScheduledFlight>)            │
│ + setPrice(double): void                         │
│ + setCustomer(Customer): void                    │
│ + setPassengers(List): void                      │
│ + getScheduledFlights(): List                    │
│ + getPrice(): double                             │
│ + getCustomer(): Customer                        │
│ + getPassengers(): List                          │
│ ... and payment methods ...                      │
└──────────────────────────────────────────────────┘

PROBLEM ANALYSIS:
1. Object created in invalid state
   └─ missing customer, price, passengers
2. Multiple setter calls needed (error-prone)
3. Side effects in Customer.createOrder()
4. Validation scattered across multiple classes
5. Order creation logic not reusable
6. Hard to test order creation separately
```

### Class Diagram: After Builder Pattern

```
┌──────────────────────────────────────────────────┐
│          FlightOrderBuilder                      │
│         (Builder)                                │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ - flights: List<ScheduledFlight>                 │
│ - passengerNames: List<String>                   │
│ - price: double                                  │
│ - customer: Customer                             │
├──────────────────────────────────────────────────┤
│ METHODS:                                         │
│                                                  │
│ + withFlights(List): FlightOrderBuilder          │
│   ├─ Set flights                                 │
│   └─ return this (fluent interface)              │
│                                                  │
│ + withPassengers(List<String>): Builder          │
│   ├─ Set passenger names                         │
│   └─ return this (fluent interface)              │
│                                                  │
│ + withPrice(double): FlightOrderBuilder          │
│   ├─ Set price                                   │
│   └─ return this (fluent interface)              │
│                                                  │
│ + withCustomer(Customer): FlightOrderBuilder     │
│   ├─ Set customer                                │
│   └─ return this (fluent interface)              │
│                                                  │
│ + build(): FlightOrder  ← KEY METHOD            │
│   ├─ Validate all required fields set            │
│   ├─ Create Passenger objects from names         │
│   ├─ Create FlightOrder with all data            │
│   ├─ Register passengers with flights            │
│   ├─ Return fully initialized FlightOrder        │
│   └─ throw exception if invalid                  │
│                                                  │
│ - validate(): void                               │
│   ├─ Check customer != null                      │
│   ├─ Check flights not empty                     │
│   ├─ Check passenger names not empty             │
│   ├─ Check price > 0                             │
│   └─ throw exception if invalid                  │
│                                                  │
│ - createPassengers(): List<Passenger>            │
│   └─ Convert passenger names to objects          │
│                                                  │
│ - registerWithScheduledFlights(): void           │
│   └─ Add passengers to each scheduled flight     │
│                                                  │
└──────────────────────────────────────────────────┘
         │ builds
         │ (returns fully configured)
         │
         ▼
┌──────────────────────────────────────────────────┐
│           FlightOrder                            │
│         (Simple class)                           │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ - flights: List<ScheduledFlight> (final)         │
│ - customer: Customer (final) ← immutable!       │
│ - passengers: List<Passenger> (final)            │
│ - price: double (final)                          │
│ - isClosed: boolean                              │
├──────────────────────────────────────────────────┤
│ METHODS:                                         │
│ + getFlights(): List                             │
│ + getCustomer(): Customer                        │
│ + getPassengers(): List                          │
│ + getPrice(): double                             │
│ + isClosed(): boolean                            │
│ + setClosed(): void                              │
│ ... and payment methods ...                      │
│                                                  │
│ NOTE: No setters! (Immutable after build)       │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│           Customer (Refactored)                  │
├──────────────────────────────────────────────────┤
│ + createOrderBuilder(): FlightOrderBuilder       │
│   └─ Return new builder instance                 │
│                                                  │
│ ... other methods ...                            │
└──────────────────────────────────────────────────┘

USAGE EXAMPLE:

// Before: Complex and error-prone
FlightOrder order = new FlightOrder(flights);
order.setCustomer(customer);
order.setPrice(180.0);
List<Passenger> passengers = new ArrayList<>();
for (String name : passengerNames) {
    passengers.add(new Passenger(name));
}
order.setPassengers(passengers);
for (ScheduledFlight sf : flights) {
    sf.addPassengers(passengers);
}
customers.add(order);

// After: Clean and fluent
FlightOrder order = customer.createOrderBuilder()
    .withFlights(flights)
    .withPassengers(Arrays.asList("Amanda", "Max"))
    .withPrice(180.0)
    .build();  ← All validation happens here


BENEFITS ACHIEVED:
✅ Fluent interface: Readable chain of method calls
✅ Single responsibility: Builder handles creation
✅ All validation in one place: build() method
✅ Immutable after creation: Fields are final
✅ Cleaner code: No scattered setters
✅ Extensible: Easy to add optional parameters
✅ Testable: Can test builder independently
```

---

## 4. ADAPTER PATTERN - Complete UML

### Class Diagram: Before Adapter Pattern

```
┌──────────────────────────────────────────────────┐
│         PassengerPlane                           │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES (PUBLIC FIELDS):                      │
│ + model: String       ← PUBLIC                   │
│ + passengerCapacity: int ← PUBLIC                │
│ + crewCapacity: int   ← PUBLIC                   │
├──────────────────────────────────────────────────┤
│ + PassengerPlane(String)                         │
│                                                  │
│ Access Pattern: plane.model                      │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         Helicopter                               │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES (PRIVATE FIELDS):                     │
│ - model: String       ← PRIVATE                  │
│ - passengerCapacity: int ← PRIVATE               │
├──────────────────────────────────────────────────┤
│ METHODS (GETTERS):                               │
│ + getModel(): String                             │
│ + getPassengerCapacity(): int                    │
│ (crewCapacity is hardcoded, no getter)           │
│                                                  │
│ Access Pattern: heli.getModel()                  │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         PassengerDrone                           │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ - model: String                                  │
├──────────────────────────────────────────────────┤
│ METHODS:                                         │
│ (No getters!)                                    │
│ Capacity: Hardcoded to 4 in ScheduledFlight     │
│ Crew: Hardcoded to 0                             │
│                                                  │
│ Access Pattern: Directly in if-else logic        │
└──────────────────────────────────────────────────┘

ScheduledFlight.getCapacity():
┌──────────────────────────────────────────────────┐
│ public int getCapacity() throws ...              │
│ {                                                │
│   if (aircraft instanceof PassengerPlane) {      │
│     // Access public field directly              │
│     return ((PassengerPlane) aircraft)           │
│         .passengerCapacity;  ← inconsistent!     │
│   }                                              │
│   if (aircraft instanceof Helicopter) {          │
│     // Call getter method                        │
│     return ((Helicopter) aircraft)               │
│         .getPassengerCapacity();  ← inconsistent │
│   }                                              │
│   if (aircraft instanceof PassengerDrone) {      │
│     // Hardcoded value                           │
│     return 4;  ← inconsistent!                   │
│   }                                              │
│ }                                                │
│ PROBLEMS:                                        │
│ ❌ 3 different access patterns                   │
│ ❌ 5+ instanceof checks scattered                │
│ ❌ Cannot use polymorphically                    │
│ ❌ Adding new aircraft type requires changes     │
│ ❌ Violates Liskov Substitution Principle        │
└──────────────────────────────────────────────────┘
```

### Class Diagram: After Adapter Pattern

```
                    ┌──────────────────────────┐
                    │   <<interface>>          │
                    │   Aircraft               │
                    ├──────────────────────────┤
                    │ + getModel(): String     │
                    │ + getPassenger           │
                    │   Capacity(): int        │
                    │ + getCrewCapacity(): int │
                    │ + getAircraftType()      │
                    │   : String               │
                    └────────┬─────────────────┘
                             △
            ┌────────────────┼────────────────┐
            │                │                │
┌──────────────────────┐ ┌──────────────┐ ┌───────────────┐
│ PassengerPlane       │ │ Helicopter   │ │PassengerDrone │
│ implements Aircraft  │ │ implements   │ │ implements    │
│                      │ │ Aircraft     │ │ Aircraft      │
├──────────────────────┤ ├──────────────┤ ├───────────────┤
│ - model: String      │ │ - model:     │ │ - model:      │
│ - psgCapacity: int   │ │   String     │ │   String      │
│ - crewCapacity: int  │ │ - psgCap:    │ │ - PSG_CAP: 4  │
│                      │ │   int        │ │ - CREW_CAP: 0 │
├──────────────────────┤ ├──────────────┤ ├───────────────┤
│ + getModel()         │ │ + getModel() │ │ + getModel()  │
│ + getPassenger       │ │ + getPass... │ │ + getPass...  │
│   Capacity()         │ │ + getCrewCap │ │ + getCrewCap()│
│ + getCrewCapacity()  │ │ + getAircraf │ │ + getAircraft │
│ + getAircraftType()  │ │   tType()    │ │   Type()      │
└──────────────────────┘ └──────────────┘ └───────────────┘

┌────────────────────────────────────────────────────┐
│          ScheduledFlight (Refactored)              │
├────────────────────────────────────────────────────┤
│ ATTRIBUTES:                                        │
│ - aircraft: Aircraft ← Interface type, not Object! │
│                                                    │
│ METHODS:                                           │
│                                                    │
│ public int getCapacity() throws ...               │
│ {                                                  │
│   // ONE pattern for all aircraft!                │
│   return aircraft.getPassengerCapacity();         │
│   // ✅ Works for PassengerPlane                  │
│   // ✅ Works for Helicopter                      │
│   // ✅ Works for PassengerDrone                  │
│   // ✅ Works for any future Aircraft             │
│ }                                                  │
│                                                    │
│ public int getCrewMemberCapacity() throws ...     │
│ {                                                  │
│   // ONE pattern for all aircraft!                │
│   return aircraft.getCrewCapacity();              │
│   // ✅ Same for all types                        │
│ }                                                  │
│                                                    │
└────────────────────────────────────────────────────┘
         │ uses
         │
         ▼ Aircraft interface
         (Uniform access pattern)

BENEFITS ACHIEVED:
✅ Single access pattern: interface.method()
✅ No instanceof checks needed
✅ Polymorphic usage enabled
✅ Adding new aircraft just needs new class
✅ Liskov Substitution Principle satisfied
✅ Each aircraft class responsible for own properties
✅ Better encapsulation
```

---

## 5. OBSERVER PATTERN - Complete UML

```
                ┌─────────────────────────────┐
                │   <<interface>>             │
                │   FlightObserver            │
                ├─────────────────────────────┤
                │ + onPassengersAdded(event)  │
                │ + onPassengersRemoved(event)│
                │ + onPriceChanged(event)     │
                │ + onFlightCancelled(event)  │
                └─────────┬───────────────────┘
                          △
        ┌─────────────────┼─────────────────┬──────────┐
        │                 │                 │          │
┌─────────────────┐ ┌────────────┐ ┌──────────────┐ ┌───────────┐
│ Analytics       │ │  Logging   │ │Notification  │ │ Inventory │
│ Observer        │ │  Observer  │ │  Observer    │ │ Observer  │
├─────────────────┤ ├────────────┤ ├──────────────┤ ├───────────┤
│ + onPassengers  │ │ + onPasseng│ │ + onPasseng..│ │ + onPass..│
│   Added()       │ │   ers...() │ │ () // Send   │ │ () // Upd │
│ // Track stats  │ │ // Log to  │ │    email     │ │ // Upd inv│
│                 │ │    file    │ │ + onPrice... │ │           │
│ + onPrice...()  │ │ + onPrice..│ │ () // Notify │ │ + onPrice │
│ // Track price  │ │ () // Log  │ │ + onFlight..│ │ () // Upd  │
│   changes       │ │    prices  │ │ () // Alert │ │   stock   │
└─────────────────┘ └────────────┘ └──────────────┘ └───────────┘

┌──────────────────────────────────────────────────┐
│        ScheduledFlight (with Observer)           │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ - passengers: List<Passenger>                    │
│ - currentPrice: double                           │
│ - observers: List<FlightObserver>  ← NEW!       │
├──────────────────────────────────────────────────┤
│ METHODS:                                         │
│                                                  │
│ + addObserver(FlightObserver): void             │
│   └─ Register observer to notifications         │
│                                                  │
│ + removeObserver(FlightObserver): void          │
│   └─ Unregister observer                        │
│                                                  │
│ + addPassengers(List): void                     │
│   ├─ Original logic: add passengers              │
│   └─ NEW: notifyObservers(onPassengersAdded)   │
│                                                  │
│ + removePassengers(List): void                  │
│   ├─ Original logic: remove passengers           │
│   └─ NEW: notifyObservers(onPassengersRemoved) │
│                                                  │
│ + setCurrentPrice(double): void                 │
│   ├─ Original logic: set price                   │
│   └─ NEW: notifyObservers(onPriceChanged)      │
│                                                  │
│ - notifyObservers(event): void                  │
│   └─ For each observer: call appropriate method │
│                                                  │
│ ... other methods ...                            │
└──────────────────────────────────────────────────┘

USAGE PATTERN:

1. Create observers
   AnalyticsObserver analytics = new AnalyticsObserver();
   NotificationObserver notifier = new NotificationObserver();

2. Register with flight
   flight.addObserver(analytics);
   flight.addObserver(notifier);

3. When flight state changes
   flight.addPassengers(passengers);
   │
   ├─> observers.get(0).onPassengersAdded(event)
   │   └─ AnalyticsObserver updates statistics
   │
   └─> observers.get(1).onPassengersAdded(event)
       └─ NotificationObserver sends email

BENEFITS ACHIEVED:
✅ Loose coupling: Flight doesn't know about observers
✅ Dynamic subscription: Observers registered at runtime
✅ Multiple observers: One change notifies many
✅ Separation of concerns: Each observer independent
✅ Easy to add observers: New observer = new class
✅ No modification needed: Flight code unchanged
```

---

## 6. CHAIN OF RESPONSIBILITY PATTERN - Complete UML

```
              ┌──────────────────────────────┐
              │   <<abstract>>               │
              │ OrderProcessingHandler       │
              ├──────────────────────────────┤
              │ ATTRIBUTES:                  │
              │ # nextHandler: Handler       │
              │ # order: FlightOrder         │
              ├──────────────────────────────┤
              │ METHODS:                     │
              │ + setNextHandler(Handler)    │
              │ + handle(OrderContext)       │
              │   ├─ Check if can handle     │
              │   ├─ Call doHandle()         │
              │   └─ Pass to next if ok      │
              │ # doHandle(Context): bool    │
              │   (abstract - implement)    │
              └────────────┬─────────────────┘
                           △
        ┌──────────────────┼──────────────────┐
        │                  │                  │
┌──────────────────┐ ┌──────────────┐ ┌──────────────┐
│Validation        │ │  Payment     │ │  Closure     │
│Handler           │ │  Handler     │ │  Handler     │
├──────────────────┤ ├──────────────┤ ├──────────────┤
│ # doHandle():    │ │ # doHandle()│ │ # doHandle()│
│   boolean        │ │   : boolean │ │   : boolean │
│   ├─ Check order │ │   ├─ Check  │ │   ├─ Close  │
│   │   validity   │ │   │   payment│ │   │  order  │
│   ├─ Check no-fly│ │   │ method   │ │   ├─ Send   │
│   │   list       │ │   │   set    │ │   │  confirm │
│   ├─ Check cap.  │ │   ├─ Validate│ │   ├─ Update │
│   │              │ │   │   payment│ │   │  status │
│   └─ return true │ │   │   info   │ │   └─ return │
│      (or false)  │ │   ├─ Process│ │      true    │
│                  │ │   │   payment│ │              │
│                  │ │   └─ return │ │              │
│                  │ │      boolean│ │              │
└──────────────────┘ └──────────────┘ └──────────────┘

┌──────────────────────────────────────────────────┐
│         FlightOrder (with Chain)                 │
├──────────────────────────────────────────────────┤
│ + processOrder(): boolean                        │
│   ├─ Create validationHandler                    │
│   ├─ Create paymentHandler                       │
│   ├─ Create closureHandler                       │
│   ├─ Chain them:                                 │
│   │  validation → payment → closure              │
│   └─ Start chain: validation.handle(order)       │
│                                                  │
│ Processing Flow:                                 │
│                                                  │
│ order.processOrder()                             │
│       │                                          │
│       ▼                                          │
│ ValidationHandler.handle()                       │
│   ├─ Is order valid?                             │
│   │  └─ YES: Continue to next                    │
│   │         doHandle() returns true              │
│   │         Pass to nextHandler                  │
│   │  └─ NO: Stop chain, return false             │
│   │                                              │
│   ▼                                              │
│ PaymentHandler.handle()                          │
│   ├─ Has payment strategy?                       │
│   │  └─ YES: Continue                            │
│   │         Process payment                      │
│   │         Pass to nextHandler                  │
│   │  └─ NO: Stop, return false                   │
│   │                                              │
│   ▼                                              │
│ ClosureHandler.handle()                          │
│   ├─ Mark order closed                           │
│   ├─ Send confirmation                           │
│   └─ return true (success!)                      │
│                                                  │
│ ✅ All steps completed successfully              │
└──────────────────────────────────────────────────┘

BENEFITS ACHIEVED:
✅ Modular: Each handler single responsibility
✅ Flexible: Can add/remove/reorder handlers
✅ Conditional: Handlers can stop chain
✅ Testable: Test each handler independently
✅ Extensible: New handler = new class
✅ Reusable: Handlers can be reused elsewhere
```

---

## Patterns Relationship Diagram

```
┌─────────────────────────────────────────────────────────────┐
│              Design Patterns Ecosystem                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  CREATION PATTERNS                                          │
│  ═════════════════════                                      │
│  ┌─────────────────────┐                                    │
│  │  Factory Pattern    │ ← Creates Aircraft objects         │
│  │  Creates: Aircraft  │                                    │
│  └──────────┬──────────┘                                    │
│             │                                               │
│             ▼                                               │
│        Aircraft implementations                             │
│                                                             │
│  ┌─────────────────────┐                                    │
│  │  Builder Pattern    │ ← Creates Order objects            │
│  │  Creates: Orders    │                                    │
│  └──────────┬──────────┘                                    │
│             │                                               │
│             ▼                                               │
│        FlightOrder instances                                │
│                                                             │
│  ────────────────────────────────────────────────────────   │
│                                                             │
│  STRUCTURAL PATTERNS                                        │
│  ═════════════════════════                                  │
│  ┌─────────────────────┐                                    │
│  │ Adapter Pattern     │ ← Uniforms aircraft interfaces     │
│  │ Interfaces: Aircraft│                                    │
│  └──────────┬──────────┘                                    │
│             │                                               │
│             └──> Enables polymorphic usage                  │
│                                                             │
│  ────────────────────────────────────────────────────────   │
│                                                             │
│  BEHAVIORAL PATTERNS                                        │
│  ═════════════════════════                                  │
│  ┌─────────────────────────┐                                │
│  │ Strategy Pattern        │ ← Payment algorithms           │
│  │ Strategies: PaymentXxx  │                                │
│  └──────────┬──────────────┘                                │
│             │                                               │
│             ├─> CreditCardPaymentStrategy                   │
│             ├─> PayPalPaymentStrategy                       │
│             └─> Future: ApplePayStrategy, etc.              │
│                                                             │
│  ┌─────────────────────────┐                                │
│  │ Observer Pattern        │ ← State change notifications   │
│  │ Observers: FlightXxx    │                                │
│  └──────────┬──────────────┘                                │
│             │                                               │
│             ├─> AnalyticsObserver                           │
│             ├─> NotificationObserver                        │
│             ├─> LoggingObserver                             │
│             └─> Future: SlackObserver, etc.                 │
│                                                             │
│  ┌─────────────────────────┐                                │
│  │ Chain of Responsibility │ ← Processing pipelines         │
│  │ Handlers: XxxHandler    │                                │
│  └──────────┬──────────────┘                                │
│             │                                               │
│             ├─> ValidationHandler                           │
│             ├─> PaymentHandler                              │
│             ├─> ClosureHandler                              │
│             └─> Future: FraudDetectionHandler, etc.         │
│                                                             │
│  ────────────────────────────────────────────────────────   │
│                                                             │
│  INTEGRATION:                                               │
│  ════════════                                               │
│  Builder uses Strategy for payment setup                    │
│  │                                                          │
│  Handler chain observes order events                        │
│  │                                                          │
│  Observers work with any aircraft (Adapter)                 │
│  │                                                          │
│  Factory ensures consistent object creation                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```
