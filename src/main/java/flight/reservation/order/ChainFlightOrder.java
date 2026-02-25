package flight.reservation.order;

import flight.reservation.flight.ScheduledFlight;
import flight.reservation.order.handler.ClosureHandler;
import flight.reservation.order.handler.ConfirmationHandler;
import flight.reservation.order.handler.OrderProcessingHandler;
import flight.reservation.order.handler.PaymentHandler;
import flight.reservation.order.handler.ValidationHandler;
import flight.reservation.payment.PaymentStrategy;

import java.util.List;

public class ChainFlightOrder extends FlightOrder {
    private final OrderProcessingHandler chainHead;

    public ChainFlightOrder(List<ScheduledFlight> flights) {
        super(flights);
        this.chainHead = buildChain();
    }

    private OrderProcessingHandler buildChain() {
        OrderProcessingHandler validationHandler = new ValidationHandler();
        validationHandler
            .setNextHandler(new PaymentHandler())
            .setNextHandler(new ClosureHandler())
            .setNextHandler(new ConfirmationHandler());
        return validationHandler;
    }

    public boolean processOrderWithChain(PaymentStrategy paymentStrategy) {
        setPaymentStrategy(paymentStrategy);
        OrderContext context = new OrderContext(this, paymentStrategy);
        return chainHead.handle(context);
    }
}
