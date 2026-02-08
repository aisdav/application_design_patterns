public class EmailSender implements NotificationSender {
    public void send(String message) {
        System.out.println("Email sent: " + message);
    }
}
