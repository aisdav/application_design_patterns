public class Employee {
    private String name;
    private double baseSalary;
    private SalaryCalculator calculator;

    public Employee(String name, double baseSalary, SalaryCalculator calculator) {
        this.name = name;
        this.baseSalary = baseSalary;
        this.calculator = calculator;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public double calculateSalary() {
        return calculator.calculate(baseSalary);
    }
}
