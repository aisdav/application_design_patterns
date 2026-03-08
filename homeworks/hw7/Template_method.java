using System;

public abstract class Beverage
{

    public void PrepareRecipe()
    {
        BoilWater();
        Brew();
        PourInCup();
        if (CustomerWantsCondiments())
        {
            AddCondiments();
        }
    }

    protected void BoilWater() => Console.WriteLine("Кипячение воды");
    protected void PourInCup() => Console.WriteLine("Наливание в чашку");

    protected abstract void Brew();
    protected abstract void AddCondiments();

    protected virtual bool CustomerWantsCondiments() => true;
}

public class Tea : Beverage
{
    protected override void Brew() => Console.WriteLine("Заваривание чая");
    protected override void AddCondiments() => Console.WriteLine("Добавление лимона");
}

public class Coffee : Beverage
{
    protected override void Brew() => Console.WriteLine("Заваривание кофе");
    protected override void AddCondiments() => Console.WriteLine("Добавление сахара и молока");

    protected override bool CustomerWantsCondiments()
    {
        Console.Write("Хотите добавки (сахар/молоко)? (y/n): ");
        string answer = Console.ReadLine();
        return answer?.ToLower() == "y";
    }
}

public class HotChocolate : Beverage
{
    protected override void Brew() => Console.WriteLine("Размешивание горячего шоколада");
    protected override void AddCondiments() => Console.WriteLine("Добавление маршмеллоу");
}

class Program
{
    static void Main()
    {
        Console.WriteLine("Приготовление чая:");
        Tea tea = new Tea();
        tea.PrepareRecipe();

        Console.WriteLine("\nПриготовление кофе:");
        Coffee coffee = new Coffee();
        coffee.PrepareRecipe();

        Console.WriteLine("\nПриготовление горячего шоколада:");
        HotChocolate hotChoc = new HotChocolate();
        hotChoc.PrepareRecipe();
    }
}
