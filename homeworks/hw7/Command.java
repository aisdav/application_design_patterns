using System;
using System.Collections.Generic;

public interface ICommand
{
    void Execute();
    void Undo();
}

public class Light
{
    public void On() => Console.WriteLine("Свет включен");
    public void Off() => Console.WriteLine("Свет выключен");
}

public class Door
{
    public void Open() => Console.WriteLine("Дверь открыта");
    public void Close() => Console.WriteLine("Дверь закрыта");
}

public class Thermostat
{
    private int temperature = 20;
    public void Increase() => Console.WriteLine($"Температура увеличена до {++temperature}");
    public void Decrease() => Console.WriteLine($"Температура уменьшена до {--temperature}");
}
public class LightOnCommand : ICommand
{
    private Light _light;
    public LightOnCommand(Light light) => _light = light;
    public void Execute() => _light.On();
    public void Undo() => _light.Off();
}

public class LightOffCommand : ICommand
{
    private Light _light;
    public LightOffCommand(Light light) => _light = light;
    public void Execute() => _light.Off();
    public void Undo() => _light.On();
}

public class DoorOpenCommand : ICommand
{
    private Door _door;
    public DoorOpenCommand(Door door) => _door = door;
    public void Execute() => _door.Open();
    public void Undo() => _door.Close();
}

public class DoorCloseCommand : ICommand
{
    private Door _door;
    public DoorCloseCommand(Door door) => _door = door;
    public void Execute() => _door.Close();
    public void Undo() => _door.Open();
}

public class ThermostatIncreaseCommand : ICommand
{
    private Thermostat _thermostat;
    public ThermostatIncreaseCommand(Thermostat t) => _thermostat = t;
    public void Execute() => _thermostat.Increase();
    public void Undo() => _thermostat.Decrease();
}

public class ThermostatDecreaseCommand : ICommand
{
    private Thermostat _thermostat;
    public ThermostatDecreaseCommand(Thermostat t) => _thermostat = t;
    public void Execute() => _thermostat.Decrease();
    public void Undo() => _thermostat.Increase();
}

public class RemoteControl
{
    private Stack<ICommand> _history = new Stack<ICommand>();

    public void ExecuteCommand(ICommand command)
    {
        command.Execute();
        _history.Push(command);
    }

    public void UndoLastCommand()
    {
        if (_history.Count > 0)
        {
            var command = _history.Pop();
            command.Undo();
        }
        else
        {
            Console.WriteLine("Нет команд для отмены");
        }
    }
}

class Program
{
    static void Main()
    {
        Light light = new Light();
        Door door = new Door();
        Thermostat thermostat = new Thermostat();

        RemoteControl remote = new RemoteControl();

        remote.ExecuteCommand(new LightOnCommand(light));
        remote.ExecuteCommand(new DoorOpenCommand(door));
        remote.ExecuteCommand(new ThermostatIncreaseCommand(thermostat));

        remote.UndoLastCommand();
        remote.UndoLastCommand(); 
        remote.UndoLastCommand(); 
        remote.UndoLastCommand(); 
    }
}
