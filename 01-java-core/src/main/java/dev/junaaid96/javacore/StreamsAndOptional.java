package dev.junaaid96.javacore;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Streams express "what" to compute over collections; Optional models "maybe a value" without null.
public final class StreamsAndOptional {

    // Simple immutable domain type used by the examples.
    public record Employee(String name, String department, double salary) {}

    // Prevent instantiation of this utility class.
    private StreamsAndOptional() {}

    // filter -> map -> toList: names of employees earning more than a threshold, sorted.
    public static List<String> highEarners(List<Employee> employees, double threshold) {
        return employees.stream()
                // Keep only matching elements (intermediate, lazy).
                .filter(e -> e.salary() > threshold)
                // Transform each Employee into its name.
                .map(Employee::name)
                // Natural (alphabetical) order.
                .sorted()
                // Terminal operation: Java 16+ unmodifiable list.
                .toList();
    }

    // groupingBy + averagingDouble: average salary per department.
    public static Map<String, Double> averageSalaryByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.averagingDouble(Employee::salary)));
    }

    // Return Optional instead of null so callers must decide what to do when nothing is found.
    public static Optional<Employee> findByName(List<Employee> employees, String name) {
        return employees.stream().filter(e -> e.name().equals(name)).findFirst();
    }

    // Optional chaining: map the value if present, otherwise use a default.
    public static String departmentOf(List<Employee> employees, String name) {
        return findByName(employees, name).map(Employee::department).orElse("UNKNOWN");
    }
}
