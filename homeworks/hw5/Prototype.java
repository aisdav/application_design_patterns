import java.util.ArrayList;
import java.util.List;

public class OrderPrototypeDemo {

    static class Product implements Cloneable {
        private String name;
        private double price;
        private int quantity;

        public Product(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        @Override
        protected Product clone() {
            try {
                return (Product) super.clone(); 
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Cloning failed", e);
            }
        }

        @Override
        public String toString() {
            return String.format("%s (%.2f x %d)", name, price, quantity);
        }
    }

    static class Discount implements Cloneable {
        private String type; 
        private double value;

        public Discount(String type, double value) {
            this.type = type;
            this.value = value;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }

        @Override
        protected Discount clone() {
            try {
                return (Discount) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Cloning failed", e);
            }
        }

        @Override
        public String toString() {
            return type + " " + value;
        }
    }

    static class Order implements Cloneable {
        private List<Product> products;
        private double deliveryCost;
        private List<Discount> discounts;
        private String paymentMethod;

        public Order() {
            products = new ArrayList<>();
            discounts = new ArrayList<>();
        }

        public void addProduct(Product p) { products.add(p); }
        public void setDeliveryCost(double cost) { deliveryCost = cost; }
        public void addDiscount(Discount d) { discounts.add(d); }
        public void setPaymentMethod(String method) { paymentMethod = method; }

        public List<Product> getProducts() { return products; }
        public List<Discount> getDiscounts() { return discounts; }
        public double getDeliveryCost() { return deliveryCost; }
        public String getPaymentMethod() { return paymentMethod; }

        public double calculateTotal() {
            double total = products.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
            total += deliveryCost;
            for (Discount d : discounts) {
                if (d.getType().equals("percent")) {
                    total *= (1 - d.getValue() / 100);
                } else if (d.getType().equals("fixed")) {
                    total -= d.getValue();
                }
            }
            return Math.max(total, 0);
        }

        @Override
        protected Order clone() {
            try {
                Order cloned = (Order) super.clone();
                cloned.products = new ArrayList<>();
                for (Product p : this.products) {
                    cloned.products.add(p.clone());
                }
                cloned.discounts = new ArrayList<>();
                for (Discount d : this.discounts) {
                    cloned.discounts.add(d.clone());
                }
                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Cloning failed", e);
            }
        }

        @Override
        public String toString() {
            return String.format("Order{products=%s, delivery=%.2f, discounts=%s, payment='%s', total=%.2f}",
                    products, deliveryCost, discounts, paymentMethod, calculateTotal());
        }
    }

    public static void main(String[] args) {
        Order original = new Order();
        original.addProduct(new Product("Laptop", 1000, 1));
        original.addProduct(new Product("Mouse", 25, 2));
        original.setDeliveryCost(50);
        original.addDiscount(new Discount("percent", 10)); 
        original.setPaymentMethod("Credit Card");

        System.out.println("Original: " + original);

        Order clone = original.clone();
        System.out.println("Clone:    " + clone);

        clone.getProducts().get(0).setPrice(900);
        clone.getProducts().add(new Product("Keyboard", 70, 1));
        clone.setDeliveryCost(0); 
        clone.getDiscounts().clear();
        clone.setPaymentMethod("PayPal");

        System.out.println("\nAfter modifying clone:");
        System.out.println("Original: " + original);
        System.out.println("Clone:    " + clone);
    }
}
