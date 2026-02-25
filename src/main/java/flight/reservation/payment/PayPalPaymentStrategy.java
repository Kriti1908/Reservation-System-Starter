package flight.reservation.payment;

/**
 * Concrete strategy for PayPal payments.
 * Encapsulates all PayPal payment logic and validation.
 */
public class PayPalPaymentStrategy implements PaymentStrategy {
    
    private final String email;
    private final String password;
    
    public PayPalPaymentStrategy(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    @Override
    public boolean validate() {
        return email != null && password != null && 
               email.equals(Paypal.DATA_BASE.get(password));
    }
    
    @Override
    public boolean pay(double amount) throws IllegalStateException {
        if (!validate()) {
            throw new IllegalStateException("PayPal credentials are not valid.");
        }
        
        System.out.println("Paying " + amount + " using PayPal.");
        return true;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "PayPal";
    }
}
