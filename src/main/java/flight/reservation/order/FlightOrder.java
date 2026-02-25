package flight.reservation.order;

import flight.reservation.Customer;
import flight.reservation.flight.ScheduledFlight;
import flight.reservation.payment.CreditCard;
import flight.reservation.payment.CreditCardPaymentStrategy;
import flight.reservation.payment.PayPalPaymentStrategy;
import flight.reservation.payment.PaymentStrategy;
import flight.reservation.payment.Paypal;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FlightOrder extends Order {
    private final List<ScheduledFlight> flights;
    static List<String> noFlyList = Arrays.asList("Peter", "Johannes");
    private PaymentStrategy paymentStrategy;

    public FlightOrder(List<ScheduledFlight> flights) {
        this.flights = flights;
    }

    public static List<String> getNoFlyList() {
        return noFlyList;
    }

    public List<ScheduledFlight> getScheduledFlights() {
        return flights;
    }

    /**
     * Sets the payment strategy for this order.
     * 
     * @param paymentStrategy The payment strategy to use
     */
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    /**
     * Processes the payment using the configured payment strategy.
     * 
     * @return true if payment is successful
     * @throws IllegalStateException if order is already closed, no payment strategy is set, or payment fails
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
     * Convenience method for processing order with credit card details.
     * Creates a CreditCardPaymentStrategy and processes payment.
     * 
     * @deprecated Use setPaymentStrategy() followed by processPayment() instead
     */
    @Deprecated
    public boolean processOrderWithCreditCardDetail(String number, Date expirationDate, String cvv) throws IllegalStateException {
        CreditCard creditCard = new CreditCard(number, expirationDate, cvv);
        return processOrderWithCreditCard(creditCard);
    }

    /**
     * Convenience method for processing order with credit card.
     * Creates a CreditCardPaymentStrategy and processes payment.
     * 
     * @deprecated Use setPaymentStrategy() followed by processPayment() instead
     */
    @Deprecated
    public boolean processOrderWithCreditCard(CreditCard creditCard) throws IllegalStateException {
        setPaymentStrategy(new CreditCardPaymentStrategy(creditCard));
        return processPayment();
    }

    /**
     * Convenience method for processing order with PayPal.
     * Creates a PayPalPaymentStrategy and processes payment.
     * 
     * @deprecated Use setPaymentStrategy() followed by processPayment() instead
     */
    @Deprecated
    public boolean processOrderWithPayPal(String email, String password) throws IllegalStateException {
        setPaymentStrategy(new PayPalPaymentStrategy(email, password));
        return processPayment();
    }

    private boolean isOrderValid(Customer customer, List<String> passengerNames, List<ScheduledFlight> flights) {
        boolean valid = true;
        valid = valid && !noFlyList.contains(customer.getName());
        valid = valid && passengerNames.stream().noneMatch(passenger -> noFlyList.contains(passenger));
        valid = valid && flights.stream().allMatch(scheduledFlight -> 
            scheduledFlight.getAvailableCapacity() >= passengerNames.size()
        );
        return valid;
    }
}
