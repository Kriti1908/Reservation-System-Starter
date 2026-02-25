package flight.reservation.payment;

/**
 * Strategy interface for different payment methods.
 * Defines the contract that all payment strategies must implement.
 */
public interface PaymentStrategy {
    
    /**
     * Validates the payment information before processing.
     * 
     * @return true if payment information is valid, false otherwise
     */
    boolean validate();
    
    /**
     * Processes the payment for the given amount.
     * 
     * @param amount The amount to be paid
     * @return true if payment is successful, false otherwise
     * @throws IllegalStateException if payment fails or validation fails
     */
    boolean pay(double amount) throws IllegalStateException;
    
    /**
     * Returns the name of the payment method.
     * 
     * @return The payment method name (e.g., "Credit Card", "PayPal")
     */
    String getPaymentMethodName();
}
