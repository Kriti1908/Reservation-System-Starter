package flight.reservation.order.handler;

import flight.reservation.order.OrderContext;

public abstract class OrderProcessingHandler {
    private OrderProcessingHandler nextHandler;

    public OrderProcessingHandler setNextHandler(OrderProcessingHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public boolean handle(OrderContext context) {
        boolean shouldContinue = doHandle(context);
        if (!shouldContinue || nextHandler == null) {
            return context != null && context.isSuccess();
        }
        return nextHandler.handle(context);
    }

    protected abstract boolean doHandle(OrderContext context);
}
