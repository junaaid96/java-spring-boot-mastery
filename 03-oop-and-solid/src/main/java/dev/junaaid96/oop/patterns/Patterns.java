package dev.junaaid96.oop.patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

// The most used GoF patterns in compact, modern Java.
public final class Patterns {

    // Holder class.
    private Patterns() {}

    // ---------- Singleton: one instance per JVM. Enum is the safest form (thread-safe, serialization-safe). ----------
    public enum AppConfig {
        INSTANCE;                                       // the single instance
        private final Map<String, String> values = Map.of("env", "dev");
        public String get(String key) { return values.get(key); }
    }

    // ---------- Factory: callers ask for a product by kind, not by concrete class. ----------
    public sealed interface Payment permits CardPayment, WalletPayment { String pay(double amount); }
    public record CardPayment() implements Payment { public String pay(double a) { return "card " + a; } }
    public record WalletPayment() implements Payment { public String pay(double a) { return "wallet " + a; } }

    public static Payment paymentFor(String kind) {
        return switch (kind) {                          // creation logic lives in one place
            case "card" -> new CardPayment();
            case "wallet" -> new WalletPayment();
            default -> throw new IllegalArgumentException("unknown payment " + kind);
        };
    }

    // ---------- Builder: readable construction of objects with many optional fields. ----------
    public record Pizza(String size, List<String> toppings, boolean extraCheese) {
        public static Builder builder() { return new Builder(); }

        public static final class Builder {
            private String size = "M";                  // sensible defaults
            private final List<String> toppings = new ArrayList<>();
            private boolean extraCheese;
            public Builder size(String size) { this.size = size; return this; }       // fluent setters
            public Builder topping(String t) { toppings.add(t); return this; }
            public Builder extraCheese() { this.extraCheese = true; return this; }
            public Pizza build() { return new Pizza(size, List.copyOf(toppings), extraCheese); } // immutable result
        }
    }

    // ---------- Strategy: swap an algorithm at runtime. A functional interface makes it a lambda. ----------
    @FunctionalInterface
    public interface DiscountStrategy { double apply(double price); }

    public static double checkout(double price, DiscountStrategy strategy) {
        return strategy.apply(price);                   // the caller chooses the algorithm
    }

    // ---------- Observer: subscribers are notified when something happens. ----------
    public static final class EventBus<T> {
        private final List<Consumer<T>> listeners = new ArrayList<>();
        public void subscribe(Consumer<T> listener) { listeners.add(listener); }
        public void publish(T event) { listeners.forEach(l -> l.accept(event)); }   // push to all
    }

    // ---------- Decorator: add behaviour by wrapping, without changing the original. ----------
    public interface TextSource { String read(); }
    public record PlainText(String text) implements TextSource { public String read() { return text; } }
    public record Decorated(TextSource inner, UnaryOperator<String> extra) implements TextSource {
        public String read() { return extra.apply(inner.read()); }  // delegate, then enhance
    }

    // ---------- Adapter: make an incompatible API fit the interface clients expect. ----------
    public static final class LegacyThermometer { public double fahrenheit() { return 212; } }  // old API
    public interface CelsiusSensor { double celsius(); }                                       // new API
    public record ThermometerAdapter(LegacyThermometer legacy) implements CelsiusSensor {
        public double celsius() { return (legacy.fahrenheit() - 32) * 5 / 9; }                 // translate
    }
}
