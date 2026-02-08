from abc import ABC, abstractmethod

class OrderItem:
    def __init__(self, name, price, quantity):
        self.name = name
        self.price = price
        self.quantity = quantity

    def total_price(self):
        return self.price * self.quantity


class Order:
    def __init__(self, payment, delivery, discount_calculator, notifications):
        self.items = []
        self.payment = payment
        self.delivery = delivery
        self.discount_calculator = discount_calculator
        self.notifications = notifications
        self.status = "CREATED"

    def add_item(self, item):
        self.items.append(item)

    def calculate_total(self):
        subtotal = sum(item.total_price() for item in self.items)
        discount = self.discount_calculator.calculate(subtotal)
        return subtotal - discount

    def process_order(self):
        amount = self.calculate_total()
        self.payment.process_payment(amount)
        self.delivery.deliver_order(self)
        self.update_status("COMPLETED")

    def update_status(self, status):
        self.status = status
        for notifier in self.notifications:
            notifier.send_notification(f"Order status changed to {self.status}")


class IPayment(ABC):
    @abstractmethod
    def process_payment(self, amount):
        pass


class CreditCardPayment(IPayment):
    def process_payment(self, amount):
        pass


class PayPalPayment(IPayment):
    def process_payment(self, amount):
        pass


class BankTransferPayment(IPayment):
    def process_payment(self, amount):
        pass


class IDelivery(ABC):
    @abstractmethod
    def deliver_order(self, order):
        pass


class CourierDelivery(IDelivery):
    def deliver_order(self, order):
        pass


class PostDelivery(IDelivery):
    def deliver_order(self, order):
        pass


class PickUpPointDelivery(IDelivery):
    def deliver_order(self, order):
        pass


class INotification(ABC):
    @abstractmethod
    def send_notification(self, message):
        pass


class EmailNotification(INotification):
    def send_notification(self, message):
        pass


class SmsNotification(INotification):
    def send_notification(self, message):
        pass


class IDiscountRule(ABC):
    @abstractmethod
    def calculate(self, amount):
        pass


class PercentageDiscount(IDiscountRule):
    def __init__(self, percent):
        self.percent = percent

    def calculate(self, amount):
        return amount * self.percent / 100


class FixedDiscount(IDiscountRule):
    def __init__(self, value):
        self.value = value

    def calculate(self, amount):
        return self.value if amount >= self.value else 0


class DiscountCalculator:
    def __init__(self, rules):
        self.rules = rules

    def calculate(self, amount):
        return sum(rule.calculate(amount) for rule in self.rules)


payment = CreditCardPayment()
delivery = CourierDelivery()
discounts = DiscountCalculator([
    PercentageDiscount(10),
    FixedDiscount(500)
])
notifications = [
    EmailNotification(),
    SmsNotification()
]

order = Order(payment, delivery, discounts, notifications)
order.add_item(OrderItem("Laptop", 300000, 1))
order.add_item(OrderItem("Mouse", 5000, 2))
order.process_order()
