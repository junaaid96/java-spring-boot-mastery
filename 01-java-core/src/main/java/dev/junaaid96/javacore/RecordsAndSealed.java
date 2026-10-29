package dev.junaaid96.javacore;

// Demonstrates records, sealed types, pattern matching and switch expressions (Java 16-21+, all final in 25).
public final class RecordsAndSealed {

    // A sealed interface lists every allowed implementation, so the compiler knows the full set.
    public sealed interface Shape permits Circle, Rectangle, Square {}

    // A record is an immutable data carrier: fields, constructor, equals/hashCode/toString are generated.
    public record Circle(double radius) implements Shape {
        // Compact constructor: runs validation before fields are assigned.
        public Circle {
            if (radius <= 0) throw new IllegalArgumentException("radius must be positive");
        }
    }

    // Records can implement interfaces just like classes.
    public record Rectangle(double width, double height) implements Shape {}

    // Another permitted shape.
    public record Square(double side) implements Shape {}

    // Utility class: private constructor prevents instantiation.
    private RecordsAndSealed() {}

    // Switch expression + record patterns: no default needed because Shape is sealed (exhaustive).
    public static double area(Shape shape) {
        return switch (shape) {
            // Record pattern deconstructs the Circle and binds its radius.
            case Circle(double r) -> Math.PI * r * r;
            // Deconstruct both components of a Rectangle.
            case Rectangle(double w, double h) -> w * h;
            // Square has one component.
            case Square(double s) -> s * s;
        };
    }

    // Pattern matching for instanceof with a guard-like condition and a `when` guard in switch.
    public static String describe(Object value) {
        return switch (value) {
            // `null` can be handled explicitly in a modern switch.
            case null -> "null";
            // Guarded pattern: only matches integers greater than 100.
            case Integer i when i > 100 -> "big int " + i;
            // Any other Integer.
            case Integer i -> "int " + i;
            // Type pattern binds a String variable.
            case String s -> "string of length " + s.length();
            // Fallback for everything else.
            default -> "other " + value.getClass().getSimpleName();
        };
    }

    // Text blocks keep multi-line strings readable; formatted() fills placeholders.
    public static String jsonFor(String name, int age) {
        return """
                {"name": "%s", "age": %d}""".formatted(name, age);
    }
}
