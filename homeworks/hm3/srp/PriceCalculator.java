public class PriceCalculator {
    public double calculateTotal(Order order) {
        return order.getQuantity() * order.getPrice() * 0.9;
    }
}
