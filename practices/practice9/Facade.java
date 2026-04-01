import java.util.*;

class RoomBookingSystem {
    private Map<Integer, String> bookings;
    private Set<Integer> allRooms;

    public RoomBookingSystem(Set<Integer> roomNumbers) {
        this.allRooms = new HashSet<>(roomNumbers);
        this.bookings = new HashMap<>();
    }

    public boolean checkAvailability(int roomNumber) {
        return allRooms.contains(roomNumber) && !bookings.containsKey(roomNumber);
    }

    public void bookRoom(int roomNumber, String clientName) {
        if (!checkAvailability(roomNumber)) {
            System.out.println("Номер " + roomNumber + " недоступен.");
            return;
        }
        bookings.put(roomNumber, clientName);
        System.out.println("Номер " + roomNumber + " успешно забронирован на " + clientName);
    }

    public void cancelBooking(int roomNumber, String clientName) {
        if (!bookings.containsKey(roomNumber) || !bookings.get(roomNumber).equals(clientName)) {
            System.out.println("Бронь номера " + roomNumber + " на имя " + clientName + " не найдена.");
            return;
        }
        bookings.remove(roomNumber);
        System.out.println("Бронь номера " + roomNumber + " отменена.");
    }
}

class RestaurantSystem {
    private Map<Integer, String> tableBookings;
    private Set<Integer> allTables;

    public RestaurantSystem(Set<Integer> tableNumbers) {
        this.allTables = new HashSet<>(tableNumbers);
        this.tableBookings = new HashMap<>();
    }

    public boolean checkTableAvailability(int tableNumber) {
        return allTables.contains(tableNumber) && !tableBookings.containsKey(tableNumber);
    }

    public void bookTable(int tableNumber, String clientName) {
        if (!checkTableAvailability(tableNumber)) {
            System.out.println("Столик " + tableNumber + " недоступен.");
            return;
        }
        tableBookings.put(tableNumber, clientName);
        System.out.println("Столик " + tableNumber + " забронирован для " + clientName);
    }

    public void orderFood(int tableNumber, List<String> dishes) {
        System.out.println("Заказ блюд " + dishes + " для столика " + tableNumber + " принят.");
    }
}

class EventManagementSystem {
    private List<String> availableHalls = Arrays.asList("Конференц-зал A", "Конференц-зал B");
    private Map<String, String> hallBookings;

    public EventManagementSystem() {
        hallBookings = new HashMap<>();
    }

    public boolean bookHall(String hallName, String eventName) {
        if (!availableHalls.contains(hallName) || hallBookings.containsKey(hallName)) {
            System.out.println("Зал " + hallName + " недоступен.");
            return false;
        }
        hallBookings.put(hallName, eventName);
        System.out.println("Зал " + hallName + " забронирован для мероприятия \"" + eventName + "\"");
        return true;
    }

    public void bookEquipment(List<String> equipment) {
        System.out.println("Заказано оборудование: " + equipment);
    }
}

class CleaningService {
    private Map<Integer, String> cleaningSchedule;

    public CleaningService() {
        cleaningSchedule = new HashMap<>();
    }

    public void scheduleCleaning(int roomNumber, String time) {
        cleaningSchedule.put(roomNumber, time);
        System.out.println("Уборка номера " + roomNumber + " запланирована на " + time);
    }

    public void performCleaning(int roomNumber) {
        if (cleaningSchedule.containsKey(roomNumber)) {
            System.out.println("Выполняется уборка номера " + roomNumber);
            cleaningSchedule.remove(roomNumber);
        } else {
            System.out.println("Нет запланированной уборки для номера " + roomNumber);
        }
    }

    public void requestCleaning(int roomNumber) {
        System.out.println("Запрос на уборку номера " + roomNumber + " принят. Уборщик выезжает.");
    }
}

class HotelFacade {
    private RoomBookingSystem roomBooking;
    private RestaurantSystem restaurant;
    private EventManagementSystem eventManagement;
    private CleaningService cleaning;

    public HotelFacade() {
        Set<Integer> rooms = new HashSet<>(Arrays.asList(101, 102, 103, 201, 202));
        roomBooking = new RoomBookingSystem(rooms);

        Set<Integer> tables = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        restaurant = new RestaurantSystem(tables);

        eventManagement = new EventManagementSystem();
        cleaning = new CleaningService();
    }

    public void bookRoomWithServices(int roomNumber, String clientName, List<String> dishes, String cleaningTime) {
        System.out.println("=== Бронирование номера с услугами ===");
        roomBooking.bookRoom(roomNumber, clientName);
        restaurant.bookTable(roomNumber, clientName);
        restaurant.orderFood(roomNumber, dishes);
        cleaning.scheduleCleaning(roomNumber, cleaningTime);
        System.out.println("Бронирование завершено.\n");
    }

    public void organizeEvent(String eventName, String hallName, List<String> equipment,
                              List<Integer> participantRooms, String clientName) {
        System.out.println("=== Организация мероприятия ===");
        if (eventManagement.bookHall(hallName, eventName)) {
            eventManagement.bookEquipment(equipment);
            for (int room : participantRooms) {
                roomBooking.bookRoom(room, clientName + " (участник)");
            }
        }
        System.out.println("Мероприятие организовано.\n");
    }

    public void bookTableWithTaxi(int tableNumber, String clientName, List<String> dishes) {
        System.out.println("=== Бронирование стола с такси ===");
        restaurant.bookTable(tableNumber, clientName);
        restaurant.orderFood(tableNumber, dishes);
        System.out.println("Вызвано такси для клиента " + clientName + " к отелю.");
        System.out.println("Бронирование стола завершено.\n");
    }

    public void cancelBooking(int roomNumber, String clientName) {
        System.out.println("=== Отмена бронирования номера ===");
        roomBooking.cancelBooking(roomNumber, clientName);
    }

    public void requestCleaning(int roomNumber) {
        System.out.println("=== Запрос уборки ===");
        cleaning.requestCleaning(roomNumber);
    }
}

public class Main {
    public static void main(String[] args) {
        HotelFacade hotel = new HotelFacade();

        hotel.bookRoomWithServices(101, "Иван Петров",
                Arrays.asList("Салат", "Стейк", "Чай"), "10:00");

        hotel.organizeEvent("Java-конференция", "Конференц-зал A",
                Arrays.asList("Проектор", "Микрофон"),
                Arrays.asList(201, 202), "Организатор");

        hotel.bookTableWithTaxi(3, "Мария Смирнова",
                Arrays.asList("Паста", "Вино"));

        hotel.cancelBooking(101, "Иван Петров");
        hotel.requestCleaning(202);
    }
}
