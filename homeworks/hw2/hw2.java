public void Log(string level, string message)
{
    Console.WriteLine($"{level}: {message}");
}

public static class AppConfig
{
    public static string ConnectionString =
        "Server=myServer;Database=myDb;User Id=myUser;Password=myPass;";
}

public class DatabaseService
{
    public void Connect()
    {
        string connectionString = AppConfig.ConnectionString;
        // Подключение к БД
    }
}

public class LoggingService
{
    public void Log(string message)
    {
        string connectionString = AppConfig.ConnectionString;
        // Запись лога
    }
}

public void ProcessNumbers(int[] numbers)
{
    if (numbers == null || numbers.Length == 0)
        return;

    foreach (var number in numbers)
    {
        if (number > 0)
            Console.WriteLine(number);
    }
}

public void PrintPositiveNumbers(int[] numbers)
{
    foreach (var number in numbers)
    {
        if (number > 0)
            Console.WriteLine(number);
    }
}

public int Divide(int a, int b)
{
    if (b == 0)
        return 0;

    return a / b;
}

public class User
{
    public string Name { get; set; }
    public string Email { get; set; }
}

public string ReadFile(string filePath)
{
    return "file content";
}

public class ReportGenerator
{
    public void GenerateReport()
    {
        // Генерация отчета
    }
}
