import java.util.*;

interface CostCalculationStrategy {
    double calculateCost(TripDetails trip);
}

class TripDetails {
    private double distance;
    private String transportType;
    private String serviceClass;
    private int passengers;
    private boolean childDiscount;
    private boolean pensionerDiscount;
    private double extraLuggageWeight;

    public TripDetails(double distance, String transportType, String serviceClass,
                       int passengers, boolean childDiscount, boolean pensionerDiscount,
                       double extraLuggageWeight) {
        this.distance = distance;
        this.transportType = transportType;
        this.serviceClass = serviceClass;
        this.passengers = passengers;
        this.childDiscount = childDiscount;
        this.pensionerDiscount = pensionerDiscount;
        this.extraLuggageWeight = extraLuggageWeight;
    }

    public double getDistance() { return distance; }
    public String getServiceClass() { return serviceClass; }
    public int getPassengers() { return passengers; }
    public boolean hasChildDiscount() { return childDiscount; }
    public boolean hasPensionerDiscount() { return pensionerDiscount; }
    public double getExtraLuggageWeight() { return extraLuggageWeight; }
}

class PlaneCostStrategy implements CostCalculationStrategy {
    public double calculateCost(TripDetails trip) {
        double baseRate = trip.getServiceClass().equals("Business") ? 0.35 : 0.15;
        double cost = trip.getDistance() * baseRate * trip.getPassengers();
        cost += trip.getExtraLuggageWeight() * 2.0;
        if (trip.hasChildDiscount()) cost *= 0.9;
        if (trip.hasPensionerDiscount()) cost *= 0.85;
        return cost;
    }
}

class TrainCostStrategy implements CostCalculationStrategy {
    public double calculateCost(TripDetails trip) {
        double baseRate = trip.getServiceClass().equals("Business") ? 0.18 : 0.08;
        double cost = trip.getDistance() * baseRate * trip.getPassengers();
        cost += trip.getExtraLuggageWeight() * 1.0;
        if (trip.hasChildDiscount()) cost *= 0.8;
        if (trip.hasPensionerDiscount()) cost *= 0.7;
        return cost;
    }
}

class BusCostStrategy implements CostCalculationStrategy {
    public double calculateCost(TripDetails trip) {
        double baseRate = trip.getServiceClass().equals("Business") ? 0.12 : 0.05;
        double cost = trip.getDistance() * baseRate * trip.getPassengers();
        cost += trip.getExtraLuggageWeight() * 0.5;
        if (trip.hasChildDiscount()) cost *= 0.85;
        if (trip.hasPensionerDiscount()) cost *= 0.8;
        return cost;
    }
}

class TravelBookingContext {
    private CostCalculationStrategy strategy;

    public void setStrategy(CostCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateTripCost(TripDetails trip) {
        if (strategy == null) throw new IllegalStateException("Strategy not set");
        if (trip == null) throw new IllegalArgumentException("Trip cannot be null");
        return strategy.calculateCost(trip);
    }
}

public class StrategyDemo {
    public static void main(String[] args) {
        TravelBookingContext context = new TravelBookingContext();
        TripDetails trip = new TripDetails(1200, "Plane", "Business", 2, true, false, 15);

        context.setStrategy(new PlaneCostStrategy());
        System.out.printf("Plane: %.2f%n", context.calculateTripCost(trip));

        context.setStrategy(new TrainCostStrategy());
        System.out.printf("Train: %.2f%n", context.calculateTripCost(trip));

        context.setStrategy(new BusCostStrategy());
        System.out.printf("Bus: %.2f%n", context.calculateTripCost(trip));
    }
}
