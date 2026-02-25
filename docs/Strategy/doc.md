# Strategy Pattern Implementation - Payment Processing

## Overview
This document details the implementation of the Strategy Pattern for the payment processing system in the Flight Reservation System. The Strategy Pattern encapsulates payment algorithms into separate, interchangeable classes, making it easy to add new payment methods without modifying existing code.

---

## Problem Statement

### Before Strategy Pattern

The `FlightOrder` class contained multiple payment processing methods with duplicate validation logic and mixed concerns:

```java
public class FlightOrder extends Order {
    // Multiple payment methods hardcoded in the class
    
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
    
    private boolean payWithCreditCard(CreditCard card, double amount) {
        if (cardIsPresentAndValid(card)) {
            System.out.println("Paying " + getPrice() + " using Credit Card.");
            double remainingAmount = card.getAmount() - getPrice();
            if (remainingAmount < 0) {
                System.out.printf("Card limit reached - Balance: %f%n", remainingAmount);
                throw new IllegalStateException("Card limit reached");
            }
            card.setAmount(remainingAmount);
            return true;
        }
        return false;
    }
    
    private boolean payWithPayPal(String email, String password, double amount) {
        if (email.equals(Paypal.DATA_BASE.get(password))) {
            System.out.println("Paying " + getPrice() + " using PayPal.");
            return true;
        }
        return false;
    }
}
```

### Issues with the Original Design

1. **Violation of Single Responsibility Principle**
   - `FlightOrder` handles both order management AND payment processing
   - Business logic mixed with payment details

2. **Code Duplication**
   - Similar validation patterns repeated across methods
   - Order closure logic duplicated in each payment method

3. **Difficult to Extend**
   - Adding new payment methods requires modifying `FlightOrder`
   - Violates Open/Closed Principle

4. **Tight Coupling**
   - Payment logic is tightly coupled to the order class
   - Difficult to test payment logic independently

5. **Inconsistent Interfaces**
   - Different payment methods have different signatures
   - Credit Card uses object, PayPal uses primitive parameters

6. **Mixed Concerns**
   - Payment validation mixed with payment processing
   - Order state management mixed with payment logic

---

## Solution: Strategy Pattern

### Design Pattern Components

The Strategy Pattern consists of three main components:

1. **Strategy Interface** (`PaymentStrategy`)
   - Defines the contract for all payment methods
   - Ensures consistent interface across all strategies

2. **Concrete Strategies** (`CreditCardPaymentStrategy`, `PayPalPaymentStrategy`)
   - Implement specific payment algorithms
   - Encapsulate payment-specific logic and validation

3. **Context** (`FlightOrder`)
   - Uses a PaymentStrategy reference
   - Delegates payment processing to the strategy

---

## Implementation Details

### Step 1: Create PaymentStrategy Interface

**File:** `src/main/java/flight/reservation/payment/PaymentStrategy.java`

```java
package flight.reservation.payment;

/**
 * Strategy interface for different payment methods.
 * Defines the contract that all payment strategies must implement.
 */
public interface PaymentStrategy {
    
    /**
     * Validates the payment information before processing.
     * 
     * @return true if payment information is valid, false otherwise
     */
    boolean validate();
    
    /**
     * Processes the payment for the given amount.
     * 
     * @param amount The amount to be paid
     * @return true if payment is successful, false otherwise
     * @throws IllegalStateException if payment fails or validation fails
     */
    boolean pay(double amount) throws IllegalStateException;
    
    /**
     * Returns the name of the payment method.
     * 
     * @return The payment method name (e.g., "Credit Card", "PayPal")
     */
    String getPaymentMethodName();
}
```

**Key Design Decisions:**

- **`validate()` method**: Separates validation from payment processing, allowing pre-validation checks
- **`pay(double amount)` method**: Accepts amount as parameter for flexibility
- **`getPaymentMethodName()` method**: Enables logging and debugging
- **Exception handling**: Uses `IllegalStateException` for consistent error handling

---

### Step 2: Implement CreditCardPaymentStrategy

**File:** `src/main/java/flight/reservation/payment/CreditCardPaymentStrategy.java`

```java
package flight.reservation.payment;

/**
 * Concrete strategy for Credit Card payments.
 * Encapsulates all Credit Card payment logic and validation.
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {
    
    private final CreditCard creditCard;
    
    public CreditCardPaymentStrategy(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
    
    @Override
    public boolean validate() {
        return creditCard != null && creditCard.isValid();
    }
    
    @Override
    public boolean pay(double amount) throws IllegalStateException {
        if (!validate()) {
            throw new IllegalStateException("Credit card information is not valid.");
        }
        
        System.out.println("Paying " + amount + " using Credit Card.");
        
        double remainingAmount = creditCard.getAmount() - amount;
        if (remainingAmount < 0) {
            System.out.printf("Card limit reached - Balance: %f%n", remainingAmount);
            throw new IllegalStateException("Card limit reached");
        }
        
        creditCard.setAmount(remainingAmount);
        return true;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }
}
```

**Key Features:**

- **Encapsulation**: All Credit Card logic is contained within this class
- **Validation**: Checks card validity before processing
- **Balance Checking**: Verifies sufficient funds before payment
- **State Management**: Updates card balance after successful payment
- **Clear Error Messages**: Provides specific error messages for different failure scenarios

---

### Step 3: Implement PayPalPaymentStrategy

**File:** `src/main/java/flight/reservation/payment/PayPalPaymentStrategy.java`

```java
package flight.reservation.payment;

/**
 * Concrete strategy for PayPal payments.
 * Encapsulates all PayPal payment logic and validation.
 */
public class PayPalPaymentStrategy implements PaymentStrategy {
    
    private final String email;
    private final String password;
    
    public PayPalPaymentStrategy(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    @Override
    public boolean validate() {
        return email != null && password != null && 
               email.equals(Paypal.DATA_BASE.get(password));
    }
    
    @Override
    public boolean pay(double amount) throws IllegalStateException {
        if (!validate()) {
            throw new IllegalStateException("PayPal credentials are not valid.");
        }
        
        System.out.println("Paying " + amount + " using PayPal.");
        return true;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "PayPal";
    }
}
```

**Key Features:**

- **Credential Validation**: Verifies email and password against database
- **Simplified Payment**: PayPal doesn't require balance checking
- **Consistent Interface**: Implements same interface as Credit Card strategy
- **Independent Logic**: PayPal-specific logic is isolated from other payment methods

---

### Step 4: Refactor FlightOrder Class

**File:** `src/main/java/flight/reservation/order/FlightOrder.java`

```java
package flight.reservation.order;

import flight.reservation.payment.PaymentStrategy;
import flight.reservation.payment.CreditCardPaymentStrategy;
import flight.reservation.payment.PayPalPaymentStrategy;
// ...other imports...

public class FlightOrder extends Order {
    private final List<ScheduledFlight> flights;
    static List<String> noFlyList = Arrays.asList("Peter", "Johannes");
    private PaymentStrategy paymentStrategy;  // NEW: Strategy field

    public FlightOrder(List<ScheduledFlight> flights) {
        this.flights = flights;
    }

    /**
     * NEW METHOD: Sets the payment strategy for this order.
     * 
     * @param paymentStrategy The payment strategy to use
     */
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    /**
     * NEW METHOD: Processes the payment using the configured payment strategy.
     * This is the new, preferred way to process payments.
     * 
     * @return true if payment is successful
     * @throws IllegalStateException if order is closed, no strategy set, or payment fails
     */
    public boolean processPayment() throws IllegalStateException {
        if (isClosed()) {
            // Payment is already proceeded
            return true;
        }
        
        if (paymentStrategy == null) {
            throw new IllegalStateException("No payment strategy has been set.");
        }
        
        if (!paymentStrategy.validate()) {
            throw new IllegalStateException("Payment information is not valid.");
        }
        
        boolean isPaid = paymentStrategy.pay(this.getPrice());
        if (isPaid) {
            this.setClosed();
        }
        return isPaid;
    }

    /**
     * DEPRECATED: Use setPaymentStrategy() followed by processPayment() instead.
     * Kept for backward compatibility.
     */
    @Deprecated
    public boolean processOrderWithCreditCard(CreditCard creditCard) throws IllegalStateException {
        setPaymentStrategy(new CreditCardPaymentStrategy(creditCard));
        return processPayment();
    }

    /**
     * DEPRECATED: Use setPaymentStrategy() followed by processPayment() instead.
     * Kept for backward compatibility.
     */
    @Deprecated
    public boolean processOrderWithPayPal(String email, String password) throws IllegalStateException {
        setPaymentStrategy(new PayPalPaymentStrategy(email, password));
        return processPayment();
    }
    
    // ...other methods remain unchanged...
}
```

**Key Changes:**

1. **Added `paymentStrategy` field**: Stores the selected payment strategy
2. **New `setPaymentStrategy()` method**: Allows runtime strategy selection
3. **New `processPayment()` method**: Unified payment processing using strategy
4. **Deprecated old methods**: Maintains backward compatibility while encouraging new usage
5. **Simplified logic**: Order closure logic is centralized in one place

---

## Usage Examples

### Old Way (Deprecated but still works)

```java
// Create order
FlightOrder order = customer.createOrder(passengerNames, flights, 100.0);

// Process with credit card
CreditCard card = new CreditCard("1234567890", expiryDate, "123");
boolean success = order.processOrderWithCreditCard(card);

// Or process with PayPal
boolean success = order.processOrderWithPayPal("user@example.com", "password");
```

### New Way (Strategy Pattern)

```java
// Create order
FlightOrder order = customer.createOrder(passengerNames, flights, 100.0);

// Option 1: Credit Card Payment
CreditCard card = new CreditCard("1234567890", expiryDate, "123");
order.setPaymentStrategy(new CreditCardPaymentStrategy(card));
boolean success = order.processPayment();

// Option 2: PayPal Payment
order.setPaymentStrategy(new PayPalPaymentStrategy("user@example.com", "password"));
boolean success = order.processPayment();

// Option 3: Switch payment methods at runtime
if (!success) {
    // Try different payment method
    order.setPaymentStrategy(new PayPalPaymentStrategy("backup@example.com", "password2"));
    success = order.processPayment();
}
```

---

## Benefits Achieved

### 1. Separation of Concerns
- Payment logic is now separate from order management
- Each strategy class has a single responsibility
- `FlightOrder` focuses on order management, not payment details

### 2. Open/Closed Principle
- Adding new payment methods doesn't require modifying `FlightOrder`
- New strategies can be added by creating new classes

**Example: Adding Apple Pay**

```java
public class ApplePayPaymentStrategy implements PaymentStrategy {
    private final String applePayToken;
    
    public ApplePayPaymentStrategy(String token) {
        this.applePayToken = token;
    }
    
    @Override
    public boolean validate() {
        return applePayToken != null && applePayToken.length() > 0;
    }
    
    @Override
    public boolean pay(double amount) {
        // Apple Pay payment logic
        System.out.println("Paying " + amount + " using Apple Pay.");
        return true;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Apple Pay";
    }
}

// Usage
order.setPaymentStrategy(new ApplePayPaymentStrategy(token));
order.processPayment();
```

### 3. Improved Testability
- Each payment strategy can be tested independently
- Mock strategies can be easily created for testing

**Example: Mock Strategy for Testing**

```java
public class MockPaymentStrategy implements PaymentStrategy {
    private boolean shouldSucceed;
    
    public MockPaymentStrategy(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }
    
    @Override
    public boolean validate() { return true; }
    
    @Override
    public boolean pay(double amount) { return shouldSucceed; }
    
    @Override
    public String getPaymentMethodName() { return "Mock"; }
}

// In tests
@Test
public void testSuccessfulPayment() {
    order.setPaymentStrategy(new MockPaymentStrategy(true));
    assertTrue(order.processPayment());
}

@Test
public void testFailedPayment() {
    order.setPaymentStrategy(new MockPaymentStrategy(false));
    assertFalse(order.processPayment());
}
```

### 4. Runtime Flexibility
- Payment method can be selected or changed at runtime
- Supports fallback payment methods

### 5. Reduced Code Duplication
- Validation and payment logic are not duplicated
- Common payment flow is centralized

### 6. Better Error Handling
- Each strategy provides specific error messages
- Validation is separated from payment processing

---

## Design Trade-offs

### Advantages
**Flexibility**: Easy to add new payment methods  
**Maintainability**: Changes to one payment method don't affect others  
**Testability**: Each strategy can be tested in isolation  
**Clean Code**: Separates payment logic from order logic  
**Runtime Selection**: Payment method can be changed dynamically  

### Disadvantages
**More Classes**: Increases number of classes in the system  
**Slight Complexity**: Adds an abstraction layer  
**Client Awareness**: Client must know about different strategies  

---

