interface IInternalDeliveryService {
    void deliverOrder(String orderId);
    String getDeliveryStatus(String orderId);
    double getDeliveryCost(String orderId);
}

class InternalDeliveryService implements IInternalDeliveryService {
    @Override
    public void deliverOrder(String orderId) {
        System.out.println("[Internal] Доставка заказа " + orderId + " выполнена.");
    }

    @Override
    public String getDeliveryStatus(String orderId) {
        return "[Internal] Статус заказа " + orderId + ": доставлен";
    }

    @Override
    public double getDeliveryCost(String orderId) {
        return 10.0;
    }
}

class ExternalLogisticsServiceA {
    public void shipItem(int itemId) {
        System.out.println("[ExternalA] Отправка товара " + itemId);
    }

    public String trackShipment(int shipmentId) {
        return "[ExternalA] Статус отправки " + shipmentId + ": в пути";
    }

    public double calculateShipping(int itemId) {
        return 15.5;
    }
}

class ExternalLogisticsServiceB {
    public void sendPackage(String packageInfo) {
        System.out.println("[ExternalB] Отправка посылки: " + packageInfo);
    }

    public String checkPackageStatus(String trackingCode) {
        return "[ExternalB] Статус посылки " + trackingCode + ": ожидает получения";
    }

    public double getPackagePrice(String packageInfo) {
        return 12.0;
    }
}

class LogisticsAdapterA implements IInternalDeliveryService {
    private ExternalLogisticsServiceA service;
    private int itemId;

    public LogisticsAdapterA(ExternalLogisticsServiceA service, int itemId) {
        this.service = service;
        this.itemId = itemId;
    }

    @Override
    public void deliverOrder(String orderId) {
        service.shipItem(itemId);
    }

    @Override
    public String getDeliveryStatus(String orderId) {
        return service.trackShipment(itemId);
    }

    @Override
    public double getDeliveryCost(String orderId) {
        return service.calculateShipping(itemId);
    }
}

class LogisticsAdapterB implements IInternalDeliveryService {
    private ExternalLogisticsServiceB service;
    private String packageInfo;

    public LogisticsAdapterB(ExternalLogisticsServiceB service, String packageInfo) {
        this.service = service;
        this.packageInfo = packageInfo;
    }

    @Override
    public void deliverOrder(String orderId) {
        service.sendPackage(packageInfo);
    }

    @Override
    public String getDeliveryStatus(String orderId) {
        return service.checkPackageStatus(packageInfo);
    }

    @Override
    public double getDeliveryCost(String orderId) {
        return service.getPackagePrice(packageInfo);
    }
}

class DeliveryServiceFactory {
    public static IInternalDeliveryService createService(String type, Object... args) {
        switch (type) {
            case "internal":
                return new InternalDeliveryService();
            case "externalA":
                return new LogisticsAdapterA(new ExternalLogisticsServiceA(), (int) args[0]);
            case "externalB":
                return new LogisticsAdapterB(new ExternalLogisticsServiceB(), (String) args[0]);
            default:
                throw new IllegalArgumentException("Неизвестный тип службы");
        }
    }
}

public class AdapterDemo {
    public static void main(String[] args) {
        IInternalDeliveryService internal = DeliveryServiceFactory.createService("internal");
        internal.deliverOrder("ORD001");
        System.out.println(internal.getDeliveryStatus("ORD001"));
        System.out.println("Стоимость: " + internal.getDeliveryCost("ORD001"));
        System.out.println();

        IInternalDeliveryService externalA = DeliveryServiceFactory.createService("externalA", 123);
        externalA.deliverOrder("EXT001");
        System.out.println(externalA.getDeliveryStatus("EXT001"));
        System.out.println("Стоимость: " + externalA.getDeliveryCost("EXT001"));
        System.out.println();

        IInternalDeliveryService externalB = DeliveryServiceFactory.createService("externalB", "pack_xyz");
        externalB.deliverOrder("EXT002");
        System.out.println(externalB.getDeliveryStatus("EXT002"));
        System.out.println("Стоимость: " + externalB.getDeliveryCost("EXT002"));
    }
}
