import java.util.*;

abstract class OrganizationComponent {
    protected String name;

    public OrganizationComponent(String name) {
        this.name = name;
    }

    public void add(OrganizationComponent component) {
        throw new UnsupportedOperationException("Не поддерживается для данного типа");
    }

    public void remove(OrganizationComponent component) {
        throw new UnsupportedOperationException("Не поддерживается для данного типа");
    }

    public abstract void display(String indent);
    public abstract double getBudget();
    public abstract int getHeadcount();

    public OrganizationComponent findEmployee(String name) {
        return null;
    }

    public void listAllEmployees(List<Employee> employees) {
    }
}

class Employee extends OrganizationComponent {
    private String position;
    private double salary;
    private boolean isContractor;

    public Employee(String name, String position, double salary) {
        this(name, position, salary, false);
    }

    public Employee(String name, String position, double salary, boolean isContractor) {
        super(name);
        this.position = position;
        this.salary = salary;
        this.isContractor = isContractor;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "Сотрудник: " + name + ", " + position +
                ", зарплата: " + salary + (isContractor ? " (контрактор)" : ""));
    }

    @Override
    public double getBudget() {
        return isContractor ? 0 : salary;
    }

    @Override
    public int getHeadcount() {
        return 1;
    }

    @Override
    public OrganizationComponent findEmployee(String name) {
        if (this.name.equals(name)) {
            return this;
        }
        return null;
    }

    @Override
    public void listAllEmployees(List<Employee> employees) {
        employees.add(this);
    }
}

class Department extends OrganizationComponent {
    private List<OrganizationComponent> components = new ArrayList<>();

    public Department(String name) {
        super(name);
    }

    @Override
    public void add(OrganizationComponent component) {
        components.add(component);
    }

    @Override
    public void remove(OrganizationComponent component) {
        components.remove(component);
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "Отдел: " + name);
        for (OrganizationComponent comp : components) {
            comp.display(indent + "  ");
        }
    }

    @Override
    public double getBudget() {
        double total = 0;
        for (OrganizationComponent comp : components) {
            total += comp.getBudget();
        }
        return total;
    }

    @Override
    public int getHeadcount() {
        int total = 0;
        for (OrganizationComponent comp : components) {
            total += comp.getHeadcount();
        }
        return total;
    }

    @Override
    public OrganizationComponent findEmployee(String name) {
        for (OrganizationComponent comp : components) {
            OrganizationComponent found = comp.findEmployee(name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    @Override
    public void listAllEmployees(List<Employee> employees) {
        for (OrganizationComponent comp : components) {
            comp.listAllEmployees(employees);
        }
    }
}

public class CompositeDemo {
    public static void main(String[] args) {
        Department company = new Department("Головная компания");

        Department devDept = new Department("Отдел разработки");
        devDept.add(new Employee("Алексей Иванов", "Team Lead", 150000));
        devDept.add(new Employee("Мария Петрова", "Senior Developer", 120000));
        devDept.add(new Employee("Дмитрий Сидоров", "Junior Developer", 80000));
        devDept.add(new Employee("Анна Смирнова", "QA Engineer", 90000, true)); // контрактор

        Department qaSubDept = new Department("Отдел тестирования");
        qaSubDept.add(new Employee("Ольга Кузнецова", "QA Lead", 110000));
        qaSubDept.add(new Employee("Иван Васильев", "Automation QA", 95000));
        devDept.add(qaSubDept);

        Department marketingDept = new Department("Отдел маркетинга");
        marketingDept.add(new Employee("Елена Морозова", "Marketing Manager", 130000));
        marketingDept.add(new Employee("Павел Новиков", "SMM Specialist", 70000));

        company.add(devDept);
        company.add(marketingDept);

        System.out.println("=== Структура организации ===");
        company.display("");

        System.out.println("\n=== Финансовые показатели ===");
        System.out.println("Общий бюджет компании: " + company.getBudget());
        System.out.println("Общее количество сотрудников: " + company.getHeadcount());

        System.out.println("\n=== Поиск сотрудника ===");
        OrganizationComponent found = company.findEmployee("Мария Петрова");
        if (found != null) {
            found.display("");
        } else {
            System.out.println("Сотрудник не найден.");
        }

        System.out.println("\n=== Изменение зарплаты ===");
        Employee emp = (Employee) company.findEmployee("Алексей Иванов");
        if (emp != null) {
            emp.setSalary(160000);
            System.out.println("Новая зарплата Алексея: 160000");
            System.out.println("Обновлённый бюджет компании: " + company.getBudget());
        }

        System.out.println("\n=== Список всех сотрудников ===");
        List<Employee> allEmployees = new ArrayList<>();
        company.listAllEmployees(allEmployees);
        for (Employee e : allEmployees) {
            e.display("");
        }
    }
}
