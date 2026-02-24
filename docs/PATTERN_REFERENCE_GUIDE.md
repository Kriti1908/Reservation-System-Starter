# Design Patterns - Quick Reference & Visual Guides

## Quick Reference Table

### Pattern Overview

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│ DESIGN PATTERNS APPLICABLE TO FLIGHT RESERVATION SYSTEM                         │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│ 1. FACTORY PATTERN                                                              │
│    ├─ Problem: Aircraft creation scattered, multiple instanceof checks          │
│    ├─ Solution: Centralize aircraft creation in AircraftFactory                 │
│    ├─ Location: flight.reservation.plane.*                                      │
│    ├─ Complexity: LOW                                                            │
│    └─ Impact: HIGH - Eliminates scattered instanceof checks                      │
│                                                                                 │
│ 2. ADAPTER PATTERN                                                              │
│    ├─ Problem: Inconsistent aircraft interfaces (public fields vs getters)       │
│    ├─ Solution: Create uniform Aircraft interface                                │
│    ├─ Location: flight.reservation.plane.Aircraft interface                      │
│    ├─ Complexity: MEDIUM                                                         │
│    └─ Impact: HIGH - Enables polymorphic usage                                   │
│                                                                                 │
│ 3. STRATEGY PATTERN                                                             │
│    ├─ Problem: Hardcoded payment methods, duplicate code                         │
│    ├─ Solution: PaymentStrategy interface with implementations                   │
│    ├─ Location: flight.reservation.payment.*                                     │
│    ├─ Complexity: MEDIUM                                                         │
│    └─ Impact: HIGH - Easy to add new payment methods                             │
│                                                                                 │
│ 4. BUILDER PATTERN                                                              │
│    ├─ Problem: Complex order creation with multiple setters                      │
│    ├─ Solution: FlightOrderBuilder for fluent construction                       │
│    ├─ Location: flight.reservation.order.*                                       │
│    ├─ Complexity: MEDIUM                                                         │
│    └─ Impact: MEDIUM - Cleaner object construction                               │
│                                                                                 │
│ 5. OBSERVER PATTERN                                                             │
│    ├─ Problem: No notification mechanism for state changes                       │
│    ├─ Solution: FlightObserver interface, observer registration                  │
│    ├─ Location: flight.reservation.flight.* & order.*                            │
│    ├─ Complexity: MEDIUM-HIGH                                                    │
│    └─ Impact: MEDIUM - Event-driven architecture                                 │
│                                                                                 │
│ 6. CHAIN OF RESPONSIBILITY PATTERN                                              │
│    ├─ Problem: Hardcoded order processing workflow                               │
│    ├─ Solution: Modular processing handlers in chain                             │
│    ├─ Location: flight.reservation.order.*                                       │
│    ├─ Complexity: HIGH                                                           │
│    └─ Impact: MEDIUM - Flexible, modular processing                              │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Pattern Comparison Matrix

### Applicability Criteria

```
Pattern                  | Complexity | Impact | Priority | Use When
────────────────────────┼────────────┼────────┼──────────┼─────────────────
Factory                 | Low        | High   | 1st      | Creating multiple 
                        |            |        |          | aircraft types
────────────────────────┼────────────┼────────┼──────────┼─────────────────
Adapter                 | Medium     | High   | 2nd      | Need uniform
                        |            |        |          | interface
────────────────────────┼────────────┼────────┼──────────┼─────────────────
Strategy                | Medium     | High   | 3rd      | Multiple payment
                        |            |        |          | methods
────────────────────────┼────────────┼────────┼──────────┼─────────────────
Builder                 | Medium     | Medium | 4th      | Complex object
                        |            |        |          | construction
────────────────────────┼────────────┼────────┼──────────┼─────────────────
Observer                | Med-High   | Medium | 5th      | Need
                        |            |        |          | notifications
────────────────────────┼────────────┼────────┼──────────┼─────────────────
Chain of Resp.          | High       | Medium | 6th      | Flexible
                        |            |        |          | workflows
```

---

## Visual Pattern Relationships

### Current State (Problem Overview)

```
┌──────────────────────────────────────────────────────────────────┐
│                    Flight Reservation System                     │
│                      (Current Problems)                          │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Aircraft Management (Factory/Adapter Problem)                   │
│  ─────────────────────────────────────────────────               │
│  - PassengerPlane: public fields                                 │
│  - Helicopter: private fields + getters                          │
│  - PassengerDrone: hardcoded values                              │
│  ├─ 5 instanceof checks scattered across code                    │
│  ├─ Difficult to add new aircraft types                          │
│  └─ Type-unsafe casts                                            │
│                                                                  │
│  Payment Processing (Strategy Problem)                           │
│  ──────────────────────────────────────                          │
│  - CreditCard payment mixed in FlightOrder                       │
│  - PayPal payment mixed in FlightOrder                           │
│  ├─ Duplicate validation logic                                   │
│  ├─ Duplicate payment flow                                       │
│  ├─ Hard to add new payment methods                              │
│  └─ Tight coupling                                               │
│                                                                  │
│  Order Creation (Builder Problem)                                │
│  ─────────────────────────────────                               │
│  - Scattered in Customer.createOrder()                           │
│  ├─ Multiple setter calls required                               │
│  ├─ Complex validation logic                                     │
│  ├─ Side effects (passenger registration)                        │
│  └─ Hard to create variations                                    │
│                                                                  │
│  Order Processing (Chain of Responsibility Problem)              │
│  ───────────────────────────────────────────────────             │
│  - Hardcoded payment flow                                        │
│  - processOrder() method does everything                         │
│  ├─ Can't reorder steps                                          │
│  ├─ Can't conditionally skip steps                               │
│  └─ Hard to add new processing steps                             │
│                                                                  │
│  State Changes (Observer Problem)                                │
│  ────────────────────────────────                                │
│  - No notification mechanism                                     │
│  ├─ Can't track flight status changes                            │
│  ├─ Can't notify about order changes                             │
│  └─ Hard to add side effects (logging, analytics)                │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Solution State (After Patterns Applied)

```
┌──────────────────────────────────────────────────────────────────┐
│                 Flight Reservation System                        │
│              (After Design Patterns Applied)                     │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Aircraft Management (Factory + Adapter)                         │
│  ─────────────────────────────────────────                       │
│  ┌─────────────────────────────────────┐                         │
│  │ AircraftFactory (static methods)    │                         │
│  │ + createAircraft(String): Aircraft  │                         │
│  └─────────────────────────────────────┘                         │
│           │                                                      │
│           ├─> PassengerPlane          (implements Aircraft)     │
│           ├─> Helicopter              (implements Aircraft)     │
│           └─> PassengerDrone          (implements Aircraft)     │
│                                                                  │
│  ✓ No instanceof checks needed                                   │
│  ✓ Easy to add new aircraft types                                │
│  ✓ Uniform interface throughout                                  │
│                                                                  │
│  Payment Processing (Strategy)                                   │
│  ──────────────────────────────                                  │
│  ┌──────────────────────────────────────┐                        │
│  │ PaymentStrategy (interface)          │                        │
│  │ + validatePayment(): boolean         │                        │
│  │ + pay(double): boolean               │                        │
│  └──────────────────────────────────────┘                        │
│           │                                                      │
│           ├─> CreditCardPaymentStrategy                         │
│           └─> PayPalPaymentStrategy                             │
│                                                                  │
│  ✓ No duplicate code                                             │
│  ✓ Easy to add Apple Pay, Google Pay, etc.                       │
│  ✓ Each payment method testable independently                    │
│                                                                  │
│  Order Creation (Builder)                                        │
│  ────────────────────────                                        │
│  ┌──────────────────────────────────────┐                        │
│  │ FlightOrderBuilder                   │                        │
│  │ + withFlights(List): Builder         │                        │
│  │ + withPassengers(List): Builder      │                        │
│  │ + withPrice(double): Builder         │                        │
│  │ + build(): FlightOrder               │                        │
│  └──────────────────────────────────────┘                        │
│                                                                  │
│  ✓ Fluent interface for order creation                           │
│  ✓ All validation in build() method                              │
│  ✓ Easy to create order variations                               │
│                                                                  │
│  Order Processing (Chain of Responsibility)                      │
│  ────────────────────────────────────────────                    │
│  ValidationHandler → PaymentHandler → ClosureHandler             │
│           ↓                ↓               ↓                     │
│        validates       processes        closes                  │
│       (passes to)      (passes to)      (confirms)               │
│                                                                  │
│  ✓ Modular processing steps                                      │
│  ✓ Easy to add new steps (FraudDetection, etc.)                  │
│  ✓ Steps can be reordered or skipped                             │
│                                                                  │
│  State Changes (Observer)                                        │
│  ────────────────────────                                        │
│  ┌──────────────────────────────────────┐                        │
│  │ FlightObserver (interface)           │                        │
│  │ + onPassengersAdded()                │                        │
│  │ + onPriceChanged()                   │                        │
│  │ + onFlightCancelled()                │                        │
│  └──────────────────────────────────────┘                        │
│           │                                                      │
│           ├─> AnalyticsObserver                                  │
│           ├─> NotificationObserver                               │
│           ├─> LoggingObserver                                    │
│           └─> InventoryObserver                                  │
│                                                                  │
│  ✓ Flight/Order notify observers of changes                      │
│  ✓ Easy to add new observers (Slack notifications, etc.)         │
│  ✓ Loose coupling between components                             │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Pattern Application Flowchart

```
START: Analyzing Reservation System
│
├─ Question: Multiple aircraft types with different interfaces?
│  │
│  └─ YES: Apply FACTORY + ADAPTER Patterns
│         ├─ Create Aircraft interface
│         ├─ Create AircraftFactory
│         └─ Refactor aircraft classes
│
├─ Question: Multiple payment methods needed?
│  │
│  └─ YES: Apply STRATEGY Pattern
│         ├─ Create PaymentStrategy interface
│         ├─ Create strategy implementations
│         └─ Refactor FlightOrder
│
├─ Question: Complex object construction?
│  │
│  └─ YES: Apply BUILDER Pattern
│         ├─ Create OrderBuilder
│         ├─ Add fluent interface
│         └─ Centralize validation
│
├─ Question: Need to track state changes?
│  │
│  └─ YES: Apply OBSERVER Pattern
│         ├─ Create observer interfaces
│         ├─ Implement observers
│         └─ Add notification in Flight/Order
│
├─ Question: Flexible processing workflows?
│  │
│  └─ YES: Apply CHAIN OF RESPONSIBILITY
│         ├─ Create processing handlers
│         ├─ Build handler chain
│         └─ Use strategy for validation
│
└─ END: Patterns Applied Successfully
```

---

## Implementation Timeline & Dependencies

```
WEEK 1: Foundation Patterns
├─ Day 1-2: Factory Pattern
│  └─ Benefit: Eliminates isinstance checks
├─ Day 3-4: Adapter Pattern (interfaces)
│  └─ Benefit: Uniform aircraft interface
└─ Day 5: Refactor Flight & ScheduledFlight
   └─ Benefit: No more type casting

WEEK 2: Business Logic Patterns
├─ Day 1-2: Strategy Pattern (Payment)
│  └─ Benefit: Easy to add payment methods
├─ Day 3-4: Builder Pattern (Orders)
│  └─ Benefit: Cleaner order construction
└─ Day 5: Refactor FlightOrder & Customer
   └─ Benefit: More maintainable code

WEEK 3: Advanced Patterns
├─ Day 1-3: Observer Pattern
│  └─ Benefit: Event notifications
├─ Day 4-5: Chain of Responsibility
│  └─ Benefit: Flexible workflows
└─ Integration Testing
   └─ Ensure all patterns work together

DEPENDENCIES:
├─ Factory → Adapter (Airport validation becomes simpler)
├─ Adapter → Strategy (No aircraft type checking needed)
├─ Strategy → Builder (Payment strategy set in builder)
├─ Builder → Observer (Observers notified on build)
└─ Observer → Chain of Responsibility (Handlers can observe events)
```

---

## Code Metrics Improvement Estimate

### Before Patterns

```
Metric                          | Value
────────────────────────────────┼──────────
Cyclomatic Complexity           | 18
Type Casts (instanceof checks)   | 15+
Code Duplication                | 25% (payment)
Number of Classes               | 10
Lines of Code (Complex methods) | 200+
Test Coverage Difficulty        | HIGH
```

### After Patterns

```
Metric                          | Value   | Improvement
────────────────────────────────┼─────────┼──────────────
Cyclomatic Complexity           | 8       | ↓ 56%
Type Casts (instanceof checks)   | 0       | ↓ 100%
Code Duplication                | 0%      | ↓ 100%
Number of Classes               | 18      | (More, but simpler)
Lines of Code (Complex methods) | 80+     | ↓ 60%
Test Coverage Difficulty        | LOW     | ↑ 100%
```

---

## Common Pitfalls & Solutions

### Pitfall 1: Over-Engineering

**Problem**: Applying patterns where they're not needed

**Solution**: 
- Apply patterns only where they solve real problems
- Start with simplest solution, refactor when needed
- Don't implement all 6 patterns at once

### Pitfall 2: Pattern Misuse

**Problem**: Using wrong pattern for the problem

**Solution**:
- Factory is for **creation**, not polymorphism
- Strategy is for **algorithms**, not state
- Observer is for **loose coupling**, not tight binding

### Pitfall 3: Memory Overhead

**Problem**: Too many objects, memory leaks in Observer

**Solution**:
- Properly unregister observers when done
- Use weak references for long-lived observers
- Profile memory usage

### Pitfall 4: Performance Impact

**Problem**: Excessive method calls reduce performance

**Solution**:
- Most impact is negligible in Java (JIT compilation)
- Profile before optimizing
- Use caching if needed

### Pitfall 5: Testing Complexity

**Problem**: Patterns make testing harder

**Solution**:
- Patterns actually improve testability
- Mock strategies, observers easily
- Each handler/strategy testable independently

---

## Maintenance & Evolution

### Adding New Aircraft Type (Post-Factory)

```java
// Before Factory Pattern:
// 1. Create new Aircraft class
// 2. Update Flight.isAircraftValid() with instanceof
// 3. Update ScheduledFlight.getCapacity() with instanceof
// 4. Update ScheduledFlight.getCrewMemberCapacity() with instanceof
// (Multiple places to change - error-prone)

// After Factory Pattern:
// 1. Create new Aircraft class implementing Aircraft interface
// 2. Add factory method to AircraftFactory
// (Single place to change - clean!)
```

### Adding New Payment Method (Post-Strategy)

```java
// Before Strategy Pattern:
// 1. Add processOrderWithNewPayment() to FlightOrder
// 2. Add payWithNewPayment() to FlightOrder
// 3. Add validation logic to FlightOrder
// 4. Duplicate common flow logic
// (Multiple changes, code duplication)

// After Strategy Pattern:
// 1. Create NewPaymentStrategy implements PaymentStrategy
// 2. Implement validatePayment() and pay()
// (Single new class, reuses framework!)
```

### Adding Notification Requirement (Post-Observer)

```java
// Before Observer Pattern:
// Need to modify Flight and Order classes
// Add notification calls everywhere state changes
// Hard to maintain, easy to miss notifications

// After Observer Pattern:
// 1. Create NewNotificationObserver
// 2. Register with Flight/Order
// (Single new class, no modifications needed!)
```

---

## Success Metrics

Track these metrics to measure pattern implementation success:

```
1. Code Quality
   ├─ Cyclomatic Complexity: Should decrease ↓
   ├─ Lines per Method: Should decrease ↓
   ├─ Code Duplication: Should decrease ↓
   └─ Test Coverage: Should increase ↑

2. Maintainability
   ├─ Time to add new feature: Should decrease ↓
   ├─ Number of files changed: Should decrease ↓
   ├─ Breaking changes: Should decrease ↓
   └─ Bug fix time: Should decrease ↓

3. Performance
   ├─ Execution Time: Monitor (usually negligible)
   ├─ Memory Usage: Should be similar or better
   ├─ Method Call Overhead: Acceptable (JIT optimized)
   └─ GC Pressure: Should not increase significantly

4. Developer Experience
   ├─ Code Readability: Should improve ↑
   ├─ IDE Assistance: Better autocomplete
   ├─ Debugging: Easier to trace (each class simpler)
   └─ Onboarding: New developers understand faster ↑
```

---

## Recommended Reading & Resources

For deeper understanding of patterns:

1. **Gang of Four** - "Design Patterns: Elements of Reusable Object-Oriented Software"
2. **Head First Design Patterns** - Easy-to-understand with examples
3. **Refactoring Guru** - Interactive pattern tutorials
4. **Clean Code** by Robert C. Martin - Principles underlying patterns
5. **Domain-Driven Design** - Advanced pattern strategies

Java-specific:
- Spring Framework (Factory, Strategy patterns in core)
- Java Collections Framework (Adapter pattern examples)
- Observer pattern in Swing/AWT components
