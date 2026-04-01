
interface Beverage {
    String getDescription();
    double cost();
}

class Espresso implements Beverage {
    public String getDescription() { return "Espresso"; }
    public double cost() { return 1.99; }
}

class Tea implements Beverage {
    public String getDescription() { return "Tea"; }
    public double cost() { return 1.50; }
}

class Latte implements Beverage {
    public String getDescription() { return "Latte"; }
    public double cost() { return 2.49; }
}

class Mocha implements Beverage {
    public String getDescription() { return "Mocha"; }
    public double cost() { return 2.79; }
}

abstract class BeverageDecorator implements Beverage {
    protected Beverage beverage;
    public BeverageDecorator(Beverage beverage) {
        this.beverage = beverage;
    }
}

class Milk extends BeverageDecorator {
    public Milk(Beverage beverage) { super(beverage); }
    public String getDescription() { return beverage.getDescription() + ", Milk"; }
    public double cost() { return beverage.cost() + 0.30; }
}

class Sugar extends BeverageDecorator {
    public Sugar(Beverage beverage) { super(beverage); }
    public String getDescription() { return beverage.getDescription() + ", Sugar"; }
    public double cost() { return beverage.cost() + 0.10; }
}

class WhippedCream extends BeverageDecorator {
    public WhippedCream(Beverage beverage) { super(beverage); }
    public String getDescription() { return beverage.getDescription() + ", Whipped Cream"; }
    public double cost() { return beverage.cost() + 0.50; }
}

class Syrup extends BeverageDecorator {
    public Syrup(Beverage beverage) { super(beverage); }
    public String getDescription() { return beverage.getDescription() + ", Syrup"; }
    public double cost() { return beverage.cost() + 0.40; }
}

class Chocolate extends BeverageDecorator {
    public Chocolate(Beverage beverage) { super(beverage); }
    public String getDescription() { return beverage.getDescription() + ", Chocolate"; }
    public double cost() { return beverage.cost() + 0.60; }
}

public class CoffeeShop {
    public static void main(String[] args) {
        Beverage espresso = new Espresso();
        System.out.println(espresso.getDescription() + " $" + espresso.cost());

        Beverage espressoWithMilkSugar = new Milk(new Sugar(new Espresso()));
        System.out.println(espressoWithMilkSugar.getDescription() + " $" + espressoWithMilkSugar.cost());

        Beverage latteDeluxe = new WhippedCream(new Syrup(new Latte()));
        System.out.println(latteDeluxe.getDescription() + " $" + latteDeluxe.cost());

        Beverage mochaSpecial = new Chocolate(new Milk(new Mocha()));
        System.out.println(mochaSpecial.getDescription() + " $" + mochaSpecial.cost());

        Beverage teaWithSugarMilk = new Sugar(new Milk(new Tea()));
        System.out.println(teaWithSugarMilk.getDescription() + " $" + teaWithSugarMilk.cost());
    }
}
