# Chain of Responsibility Pattern in Order Processing

## Overview
This implementation introduces a Chain of Responsibility for the payment-processing phase of a `FlightOrder`.
The chain is assembled in `ChainFlightOrder` and executes handlers in sequence.

## Class Structure
- `OrderProcessingHandler` (abstract): Defines `handle(context)` and `doHandle(context)`, and forwards to the next handler.
- `ValidationHandler`: Validates context, order state, payment strategy presence, and `PaymentStrategy.validate()`.
- `PaymentHandler`: Executes `PaymentStrategy.pay(order.getPrice())` and stores the result in the context.
- `ClosureHandler`: Closes the order when payment succeeds.
- `ConfirmationHandler`: Runs confirmation/post-processing after success.
- `OrderContext`: Carries `FlightOrder`, `PaymentStrategy`, and success state through the chain.
- `ChainFlightOrder`: Extends `FlightOrder`, wires the chain, and exposes `processOrderWithChain(PaymentStrategy)`.

## Handler Flow
1. Build chain in `ChainFlightOrder`: `ValidationHandler -> PaymentHandler -> ClosureHandler -> ConfirmationHandler`.
2. Call `processOrderWithChain(paymentStrategy)`.
3. Create `OrderContext` and pass it to `chainHead.handle(context)`.
4. Each handler decides whether to continue by returning `true` or stop by returning `false`.

## Behavior Notes
- If the order is already closed, validation marks success and stops the chain.
- If payment strategy is missing or invalid, an `IllegalStateException` is thrown.
- `PaymentStrategy` in this codebase uses `validate()` (not `validatePayment()`).

## Example Usage
```java
ChainFlightOrder order = new ChainFlightOrder(flights);
boolean success = order.processOrderWithChain(new CreditCardPaymentStrategy(creditCard));
```

## Implemented Files
- `src/main/java/flight/reservation/order/handler/OrderProcessingHandler.java`
- `src/main/java/flight/reservation/order/handler/ValidationHandler.java`
- `src/main/java/flight/reservation/order/handler/PaymentHandler.java`
- `src/main/java/flight/reservation/order/handler/ClosureHandler.java`
- `src/main/java/flight/reservation/order/handler/ConfirmationHandler.java`
- `src/main/java/flight/reservation/order/OrderContext.java`
- `src/main/java/flight/reservation/order/ChainFlightOrder.java`

## Reference
See `docs/Command/after.uml`.
