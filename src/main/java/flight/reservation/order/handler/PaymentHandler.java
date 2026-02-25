package flight.reservation.order.handler;

import flight.reservation.order.OrderContext;

public class PaymentHandler extends OrderProcessingHandler {

    @Override
    protected boolean doHandle(OrderContext context) {
        boolean isPaid = context.getPaymentStrategy().pay(context.getOrder().getPrice());
        context.setSuccess(isPaid);
        return isPaid;
    }
}
