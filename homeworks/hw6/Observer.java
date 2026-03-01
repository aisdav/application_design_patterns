package Observer;

public interface Observer {
    public void update(String wars);
}
package Observer;

public interface Subject {
    public void attach(Observer observer);
    public void detach(Observer observer);
    public void notifyObservers(String wars);
}
package Observer;

import java.util.ArrayList;
import java.util.List;

public class News implements Subject {
    private final List<Observer> subs = new ArrayList<>();

    @Override
    public void attach(Observer observer) {
        if (observer == null) return;
        subs.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        subs.remove(observer);
    }

    @Override
    public void notifyObservers(String news) {
        for (Observer o : subs) {
            o.update(news);
        }
    }

    // Удобный метод: "опубликовать новость"
    public void publish(String news) {
        notifyObservers(news);
    }
}
package Observer;

public class Main {
    public static void main(String[] args) {
        News news = new News();

        Observer a = new ConsoleObserver("Айсултан");
        Observer b = new ConsoleObserver("Друг");

        news.attach(a);
        news.attach(b);

        news.publish("USD вырос до 510");

        news.detach(b);

        news.publish("BTC упал на 3%");
    }
}
package Observer;

public class ConsoleObserver implements Observer {
    private final String name;

    public ConsoleObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(String news) {
        System.out.println(name + " услышал новость: " + news);
    }
}
