public interface PaymentStrategy {
    void pay(double amount);
}
public class PaypalPayment implements PaymentStrategy{
    @Override
    public void pay(double amount){
        System.out.println("Оплата пейпал "+ amount);
    }
}
public class CryptoPayment implements PaymentStrategy {
    @Override
    public void pay(double amount){
        System.out.println("Оплата криптой "+ amount);
    }
}
public class CardPayment implements PaymentStrategy{
    @Override
    public void pay(double amount){
        System.out.println("Оплата картой" + amount);
    }
}

public class PaymentContext {
    private PaymentStrategy strategy;
    public PaymentContext() {
    }
    public PaymentContext (PaymentStrategy strategy){
        this.strategy= strategy;
    }
    public void setStrategy(PaymentStrategy strategy){
        this.strategy = strategy;
    }
    public void pay(double amount){
        if (strategy == null) {
            throw new IllegalStateException("Стратегия оплаты не установлена");
        }
        strategy.pay(amount);
    }
}
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PaymentContext context = new PaymentContext();

        System.out.println("Каким способом хотите оплатить?");
        System.out.println("1 - Оплата картой");
        System.out.println("2 - Оплата криптовалютой");
        System.out.println("3 - Оплата PayPal");

        int method = scanner.nextInt();

        if (method == 1) {
            context.setStrategy(new CardPayment());
        }
        else if (method == 2) {
            context.setStrategy(new CryptoPayment());
        }
        else if (method == 3) {
            context.setStrategy(new PaypalPayment());
        }
        else {
            System.out.println("Неверный выбор");
            return;
        }

        System.out.print("Введите сумму: ");
        double amount = scanner.nextDouble();

        context.pay(amount);

        scanner.close();
    }
}
