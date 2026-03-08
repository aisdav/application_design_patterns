using System;
using System.Collections.Generic;

public interface IMediator
{
    void SendMessage(string message, User sender);
    void SendPrivateMessage(string message, User sender, User recipient);
    void RegisterUser(User user);
    void UnregisterUser(User user);
}

public class ChatRoom : IMediator
{
    private List<User> _users = new List<User>();

    public void RegisterUser(User user)
    {
        _users.Add(user);
        NotifyAll($"Пользователь {user.Name} присоединился к чату.", user);
    }

    public void UnregisterUser(User user)
    {
        _users.Remove(user);
        NotifyAll($"Пользователь {user.Name} покинул чат.", user);
    }

    public void SendMessage(string message, User sender)
    {
        foreach (var user in _users)
        {
            if (user != sender)
                user.Receive(message, sender);
        }
    }

    public void SendPrivateMessage(string message, User sender, User recipient)
    {
        if (_users.Contains(recipient))
            recipient.Receive($"(личное) {message}", sender);
        else
            Console.WriteLine($"Пользователь {recipient.Name} не в чате.");
    }

    private void NotifyAll(string notification, User except)
    {
        foreach (var user in _users)
        {
            if (user != except)
                user.ReceiveNotification(notification);
        }
    }
}

public abstract class User
{
    protected IMediator _mediator;
    public string Name { get; }

    public User(string name, IMediator mediator)
    {
        Name = name;
        _mediator = mediator;
    }

    public void Send(string message)
    {
        Console.WriteLine($"{Name} отправляет: {message}");
        _mediator.SendMessage(message, this);
    }

    public void SendPrivate(string message, User recipient)
    {
        Console.WriteLine($"{Name} отправляет личное сообщение для {recipient.Name}: {message}");
        _mediator.SendPrivateMessage(message, this, recipient);
    }

    public abstract void Receive(string message, User sender);
    public virtual void ReceiveNotification(string notification)
    {
        Console.WriteLine($"{Name} получил уведомление: {notification}");
    }
}

// Конкретный пользователь
public class ChatUser : User
{
    public ChatUser(string name, IMediator mediator) : base(name, mediator) { }

    public override void Receive(string message, User sender)
    {
        Console.WriteLine($"{Name} получил от {sender.Name}: {message}");
    }
}

class Program
{
    static void Main()
    {
        ChatRoom chat = new ChatRoom();

        ChatUser alice = new ChatUser("Алиса", chat);
        ChatUser bob = new ChatUser("Боб", chat);
        ChatUser charlie = new ChatUser("Чарли", chat);

        chat.RegisterUser(alice);
        chat.RegisterUser(bob);
        chat.RegisterUser(charlie);

        alice.Send("Всем привет!");
        bob.SendPrivate("Привет, Алиса!", alice);

        chat.UnregisterUser(charlie);

        alice.Send("Чарли, ты здесь?");
        charlie.Send("Я здесь!"); // не отправится, так как отписан
    }
}
