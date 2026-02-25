package flight.reservation.order.handler;

import flight.reservation.order.OrderContext;

public class ValidationHandler extends OrderProcessingHandler {

    @Override
    protected boolean doHandle(OrderContext context) {
        if (context == null || context.getOrder() == null) {
            throw new IllegalStateException("Order context is missing.");
        }

        if (context.getOrder().isClosed()) {
            context.setSuccess(true);
            return false;
        }

        if (context.getPaymentStrategy() == null) {
            throw new IllegalStateException("No payment strategy has been set.");
        }

        if (!context.getPaymentStrategy().validate()) {
            throw new IllegalStateException("Payment information is not valid.");
        }

        return true;
    }
}
