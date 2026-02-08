public class SmsSender implements NotificationSender {
    public void send(String message) {
        System.out.println("SMS sent: " + message);
    }
}
