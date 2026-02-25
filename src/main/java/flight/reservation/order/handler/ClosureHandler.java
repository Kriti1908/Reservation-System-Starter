package flight.reservation.order.handler;

import flight.reservation.order.OrderContext;

public class ClosureHandler extends OrderProcessingHandler {

    @Override
    protected boolean doHandle(OrderContext context) {
        if (context.isSuccess()) {
            context.getOrder().setClosed();
        }
        return context.isSuccess();
    }
}
