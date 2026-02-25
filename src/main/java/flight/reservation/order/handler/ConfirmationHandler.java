package flight.reservation.order.handler;

import flight.reservation.order.OrderContext;

public class ConfirmationHandler extends OrderProcessingHandler {

    @Override
    protected boolean doHandle(OrderContext context) {
        if (context.isSuccess()) {
            System.out.println("Order " + context.getOrder().getId() + " processed successfully.");
        }
        return context.isSuccess();
    }
}
