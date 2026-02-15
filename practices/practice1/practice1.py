class Vehicle:
    def __init__(self, brand: str, model: str, year: int):
        self.brand = brand
        self.model = model
        self.year = year

    def start_engine(self):
        print(f"{self.brand} {self.model}: двигатель запущен")

    def stop_engine(self):
        print(f"{self.brand} {self.model}: двигатель остановлен")

    def __str__(self):
        return f"{self.brand} {self.model} ({self.year})"

class Car(Vehicle):
    def __init__(self, brand, model, year, doors: int, transmission: str):
        super().__init__(brand, model, year)
        self.doors = doors
        self.transmission = transmission

    def __str__(self):
        return (f"Автомобиль: {self.brand} {self.model} ({self.year}), "
                f"{self.doors} двери, КПП: {self.transmission}")


class Motorcycle(Vehicle):
    def __init__(self, brand, model, year, body_type: str, has_box: bool):
        super().__init__(brand, model, year)
        self.body_type = body_type
        self.has_box = has_box

    def __str__(self):
        box = "есть бокс" if self.has_box else "без бокса"
        return (f"Мотоцикл: {self.brand} {self.model} ({self.year}), "
                f"тип: {self.body_type}, {box}")    
class Garage:
    def __init__(self, name: str):
        self.name = name
        self.vehicles = []

    def add_vehicle(self, vehicle: Vehicle):
        self.vehicles.append(vehicle)
        print(f"➡ {vehicle} добавлен в гараж '{self.name}'")

    def remove_vehicle(self, vehicle: Vehicle):
        if vehicle in self.vehicles:
            self.vehicles.remove(vehicle)
            print(f"❌ {vehicle} удалён из гаража '{self.name}'")
        else:
            print("⚠ Транспортное средство не найдено")

    def list_vehicles(self):
        print(f"\nГараж '{self.name}':")
        for v in self.vehicles:
            print(" -", v)
class Fleet:
    def __init__(self):
        self.garages = []

    def add_garage(self, garage: Garage):
        self.garages.append(garage)
        print(f"🏠 Гараж '{garage.name}' добавлен в автопарк")

    def remove_garage(self, garage: Garage):
        if garage in self.garages:
            self.garages.remove(garage)
            print(f"❌ Гараж '{garage.name}' удалён из автопарка")

    def find_vehicle(self, brand: str, model: str):
        for garage in self.garages:
            for vehicle in garage.vehicles:
                if vehicle.brand == brand and vehicle.model == model:
                    return garage.name, vehicle
        return None

    def list_all(self):
        print("\n=== Автопарк ===")
        for garage in self.garages:
            garage.list_vehicles()

if __name__ == "__main__":

    car1 = Car("Toyota", "Camry", 2020, 4, "Автомат")
    car2 = Car("BMW", "X5", 2022, 5, "Автомат")
    bike1 = Motorcycle("Yamaha", "MT-07", 2021, "Нейкед", False)


    vehicles = [car1, car2, bike1]
    for v in vehicles:
        v.start_engine()
        v.stop_engine()


    garage1 = Garage("Гараж №1")
    garage2 = Garage("Гараж №2")

    garage1.add_vehicle(car1)
    garage1.add_vehicle(bike1)
    garage2.add_vehicle(car2)

    fleet = Fleet()
    fleet.add_garage(garage1)
    fleet.add_garage(garage2)

    fleet.list_all()


    result = fleet.find_vehicle("BMW", "X5")
    if result:
        garage_name, vehicle = result
        print(f"\n🔍 Найдено: {vehicle} в гараже '{garage_name}'")


    garage1.remove_vehicle(bike1)
    fleet.remove_garage(garage2)

    fleet.list_all()
