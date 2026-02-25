package flight.reservation.payment;

/**
 * Concrete strategy for Credit Card payments.
 * Encapsulates all Credit Card payment logic and validation.
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {
    
    private final CreditCard creditCard;
    
    public CreditCardPaymentStrategy(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
    
    @Override
    public boolean validate() {
        return creditCard != null && creditCard.isValid();
    }
    
    @Override
    public boolean pay(double amount) throws IllegalStateException {
        if (!validate()) {
            throw new IllegalStateException("Credit card information is not valid.");
        }
        
        System.out.println("Paying " + amount + " using Credit Card.");
        
        double remainingAmount = creditCard.getAmount() - amount;
        if (remainingAmount < 0) {
            System.out.printf("Card limit reached - Balance: %f%n", remainingAmount);
            throw new IllegalStateException("Card limit reached");
        }
        
        creditCard.setAmount(remainingAmount);
        return true;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }
}
