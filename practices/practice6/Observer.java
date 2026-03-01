import java.util.*;
import java.util.concurrent.*;

interface Observer {
    void update(Stock stock);
}

interface Subject {
    void attach(String symbol, Observer observer);
    void detach(String symbol, Observer observer);
    void notifyObservers(Stock stock);
}

class Stock {
    private String symbol;
    private double price;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
}

class StockExchange implements Subject {
    private Map<String, List<Observer>> observers = new HashMap<>();
    private Map<String, Double> prices = new HashMap<>();

    public void attach(String symbol, Observer observer) {
        observers.computeIfAbsent(symbol, k -> new ArrayList<>()).add(observer);
        System.out.println("Observer " + observer.getClass().getSimpleName() + " subscribed to " + symbol);
    }

    public void detach(String symbol, Observer observer) {
        List<Observer> list = observers.get(symbol);
        if (list != null) {
            list.remove(observer);
            System.out.println("Observer " + observer.getClass().getSimpleName() + " unsubscribed from " + symbol);
        }
    }

    public void notifyObservers(Stock stock) {
        List<Observer> list = observers.get(stock.getSymbol());
        if (list != null) {
            for (Observer obs : list) {
                CompletableFuture.runAsync(() -> obs.update(stock));
            }
        }
    }

    public void setPrice(String symbol, double newPrice) {
        prices.put(symbol, newPrice);
        notifyObservers(new Stock(symbol, newPrice));
    }
}

class Trader implements Observer {
    private String name;

    public Trader(String name) {
        this.name = name;
    }

    public void update(Stock stock) {
        System.out.printf("[%s] Received: %s = %.2f%n", name, stock.getSymbol(), stock.getPrice());
    }
}

class TradingRobot implements Observer {
    private String name;
    private double buyThreshold;
    private double sellThreshold;

    public TradingRobot(String name, double buyBelow, double sellAbove) {
        this.name = name;
        this.buyThreshold = buyBelow;
        this.sellThreshold = sellAbove;
    }

    public void update(Stock stock) {
        if (stock.getPrice() < buyThreshold) {
            System.out.printf("[Robot %s] BUY %s at %.2f (below %.2f)%n",
                name, stock.getSymbol(), stock.getPrice(), buyThreshold);
        } else if (stock.getPrice() > sellThreshold) {
            System.out.printf("[Robot %s] SELL %s at %.2f (above %.2f)%n",
                name, stock.getSymbol(), stock.getPrice(), sellThreshold);
        } else {
            System.out.printf("[Robot %s] %s = %.2f – no action%n",
                name, stock.getSymbol(), stock.getPrice());
        }
    }
}

public class ObserverDemo {
    public static void main(String[] args) throws Exception {
        StockExchange exchange = new StockExchange();

        Trader trader1 = new Trader("Ivan");
        Trader trader2 = new Trader("Peter");
        TradingRobot robot = new TradingRobot("Alpha", 100, 150);

        exchange.attach("AAPL", trader1);
        exchange.attach("AAPL", robot);
        exchange.attach("GOOG", trader2);
        exchange.attach("GOOG", robot);

        exchange.setPrice("AAPL", 95);
        exchange.setPrice("AAPL", 120);
        exchange.setPrice("GOOG", 200);
        exchange.setPrice("GOOG", 90);

        exchange.detach("AAPL", robot);

        exchange.setPrice("AAPL", 80);

        Thread.sleep(1000);
    }
}
