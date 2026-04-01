
interface IPaymentProcessor {
    void processPayment(double amount);
}

+
class PayPalPaymentProcessor implements IPaymentProcessor {
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment: $" + amount);
    }
}


class StripePaymentService {
    public void makeTransaction(double totalAmount) {
        System.out.println("Stripe transaction: $" + totalAmount);
    }
}


class StripePaymentAdapter implements IPaymentProcessor {
    private StripePaymentService stripeService;
    public StripePaymentAdapter(StripePaymentService service) {
        this.stripeService = service;
    }
    public void processPayment(double amount) {
        stripeService.makeTransaction(amount);
    }
}

class SquarePaymentService {
    public void charge(double amount) {
        System.out.println("Square charge: $" + amount);
    }
}


class SquarePaymentAdapter implements IPaymentProcessor {
    private SquarePaymentService squareService;
    public SquarePaymentAdapter(SquarePaymentService service) {
        this.squareService = service;
    }
    public void processPayment(double amount) {
        squareService.charge(amount);
    }
}


public class PaymentSystem {
    public static void main(String[] args) {

        IPaymentProcessor paypal = new PayPalPaymentProcessor();
        paypal.processPayment(100.0);


        StripePaymentService stripeService = new StripePaymentService();
        IPaymentProcessor stripeAdapter = new StripePaymentAdapter(stripeService);
        stripeAdapter.processPayment(200.0);


        SquarePaymentService squareService = new SquarePaymentService();
        IPaymentProcessor squareAdapter = new SquarePaymentAdapter(squareService);
        squareAdapter.processPayment(300.0);
    }
}
