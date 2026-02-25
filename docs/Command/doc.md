# Chain of Responsibility Pattern in Command Module

## Overview
This implementation introduces the Chain of Responsibility pattern for processing flight orders. The chain consists of several handlers, each responsible for a specific step in the order processing pipeline. The chain is built and executed in the `ChainFlightOrder` class.

## Class Structure

- **OrderProcessingHandler (abstract)**: Base class for all handlers. Each handler implements `doHandle(context)` and delegates to the next handler if successful.
- **ValidationHandler**: Validates the order context (e.g., checks if the order is not null).
- **PaymentHandler**: Handles payment using the provided `PaymentStrategy`.
- **ClosureHandler**: (Optional) Marks the order as closed or performs closure logic.
- **ConfirmationHandler**: (Optional) Sends confirmation or performs post-processing.
- **OrderContext**: Holds the order and payment strategy, and tracks success state.
- **ChainFlightOrder**: Extends `FlightOrder` and wires up the handler chain. Provides `processOrderWithChain()` to process the order using the chain.

## How It Works
1. **Build the Chain**: `ChainFlightOrder.buildChain()` wires up the handlers in order: Validation → Payment → Closure → Confirmation.
2. **Process the Order**: `processOrderWithChain()` creates an `OrderContext` and passes it through the chain. Each handler processes its step and passes to the next if successful.
3. **Extensibility**: New handlers can be added easily by extending `OrderProcessingHandler` and updating the chain wiring.

## Example Usage
```java
ChainFlightOrder order = new ChainFlightOrder(flights);
order.setPaymentStrategy(new CreditCardPaymentStrategy(creditCard));
boolean success = order.processOrderWithChain(order.getPaymentStrategy());
```

## Files Added
- `order/handler/OrderProcessingHandler.java`
- `order/handler/ValidationHandler.java`
- `order/handler/PaymentHandler.java`
- `order/handler/ClosureHandler.java`
- `order/handler/ConfirmationHandler.java`
- `order/OrderContext.java`
- `order/ChainFlightOrder.java`

## References
See `docs/Command/after.uml` for the UML diagram of the chain.
