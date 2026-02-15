import java.util.Scanner;

interface IVehicle {
    void drive();
    void refuel();
}

class Car implements IVehicle {
    private String brand;
    private String model;
    private String fuelType;

    public Car(String brand, String model, String fuelType) {
        this.brand = brand;
        this.model = model;
        this.fuelType = fuelType;
    }

    @Override
    public void drive() {
        System.out.println("Автомобиль " + brand + " " + model + " едет. Использует " + fuelType + ".");
    }

    @Override
    public void refuel() {
        System.out.println("Заправка автомобиля " + brand + " " + model + " топливом: " + fuelType + ".");
    }
}

class Motorcycle implements IVehicle {
    private String type;
    private double engineVolume;

    public Motorcycle(String type, double engineVolume) {
        this.type = type;
        this.engineVolume = engineVolume;
    }

    @Override
    public void drive() {
        System.out.println("Мотоцикл (" + type + ") с объемом двигателя " + engineVolume + " л едет.");
    }

    @Override
    public void refuel() {
        System.out.println("Заправка мотоцикла (" + type + ").");
    }
}

class Truck implements IVehicle {
    private double loadCapacity;
    private int axlesCount;

    public Truck(double loadCapacity, int axlesCount) {
        this.loadCapacity = loadCapacity;
        this.axlesCount = axlesCount;
    }

    @Override
    public void drive() {
        System.out.println("Грузовик грузоподъемностью " + loadCapacity + " т с " + axlesCount + " осями едет.");
    }

    @Override
    public void refuel() {
        System.out.println("Заправка грузовика дизелем.");
    }
}

class Bus implements IVehicle {
    private int capacity;
    private String routeNumber;

    public Bus(int capacity, String routeNumber) {
        this.capacity = capacity;
        this.routeNumber = routeNumber;
    }

    @Override
    public void drive() {
        System.out.println("Автобус маршрута " + routeNumber + " вместимостью " + capacity + " чел. едет.");
    }

    @Override
    public void refuel() {
        System.out.println("Заправка автобуса на маршруте " + routeNumber + ".");
    }
}

abstract class VehicleFactory {
    public abstract IVehicle createVehicle();
}

class CarFactory extends VehicleFactory {
    private String brand;
    private String model;
    private String fuelType;

    public CarFactory(String brand, String model, String fuelType) {
        this.brand = brand;
        this.model = model;
        this.fuelType = fuelType;
    }

    @Override
    public IVehicle createVehicle() {
        return new Car(brand, model, fuelType);
    }
}

class MotorcycleFactory extends VehicleFactory {
    private String type;
    private double engineVolume;

    public MotorcycleFactory(String type, double engineVolume) {
        this.type = type;
        this.engineVolume = engineVolume;
    }

    @Override
    public IVehicle createVehicle() {
        return new Motorcycle(type, engineVolume);
    }
}

class TruckFactory extends VehicleFactory {
    private double loadCapacity;
    private int axlesCount;

    public TruckFactory(double loadCapacity, int axlesCount) {
        this.loadCapacity = loadCapacity;
        this.axlesCount = axlesCount;
    }

    @Override
    public IVehicle createVehicle() {
        return new Truck(loadCapacity, axlesCount);
    }
}

class BusFactory extends VehicleFactory {
    private int capacity;
    private String routeNumber;

    public BusFactory(int capacity, String routeNumber) {
        this.capacity = capacity;
        this.routeNumber = routeNumber;
    }

    @Override
    public IVehicle createVehicle() {
        return new Bus(capacity, routeNumber);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Система создания транспортных средств ===");

        while (true) {
            System.out.println("\nВыберите тип транспорта (car, motorcycle, truck, bus) или 'exit' для выхода:");
            String type = scanner.nextLine().trim().toLowerCase();

            if (type.equals("exit")) {
                break;
            }

            VehicleFactory factory = null;

            switch (type) {
                case "car":
                    System.out.print("Введите марку: ");
                    String brand = scanner.nextLine();
                    System.out.print("Введите модель: ");
                    String model = scanner.nextLine();
                    System.out.print("Введите тип топлива: ");
                    String fuel = scanner.nextLine();
                    factory = new CarFactory(brand, model, fuel);
                    break;
                case "motorcycle":
                    System.out.print("Введите тип (спортивный/туристический): ");
                    String bikeType = scanner.nextLine();
                    System.out.print("Введите объем двигателя (л): ");
                    double volume = Double.parseDouble(scanner.nextLine());
                    factory = new MotorcycleFactory(bikeType, volume);
                    break;
                case "truck":
                    System.out.print("Введите грузоподъемность (т): ");
                    double capacity = Double.parseDouble(scanner.nextLine());
                    System.out.print("Введите количество осей: ");
                    int axles = Integer.parseInt(scanner.nextLine());
                    factory = new TruckFactory(capacity, axles);
                    break;
                case "bus":
                    System.out.print("Введите вместимость (чел): ");
                    int busCapacity = Integer.parseInt(scanner.nextLine());
                    System.out.print("Введите номер маршрута: ");
                    String route = scanner.nextLine();
                    factory = new BusFactory(busCapacity, route);
                    break;
                default:
                    System.out.println("Неизвестный тип транспорта.");
                    continue;
            }

            IVehicle vehicle = factory.createVehicle();
            vehicle.drive();
            vehicle.refuel();
        }

        scanner.close();
        System.out.println("Программа завершена.");
    }
}
