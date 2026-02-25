package flight.reservation.order;

import flight.reservation.payment.PaymentStrategy;

public class OrderContext {
    private final FlightOrder order;
    private final PaymentStrategy paymentStrategy;
    private boolean success;

    public OrderContext(FlightOrder order, PaymentStrategy paymentStrategy) {
        this.order = order;
        this.paymentStrategy = paymentStrategy;
        this.success = false;
    }

    public FlightOrder getOrder() {
        return order;
    }

    public PaymentStrategy getPaymentStrategy() {
        return paymentStrategy;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
