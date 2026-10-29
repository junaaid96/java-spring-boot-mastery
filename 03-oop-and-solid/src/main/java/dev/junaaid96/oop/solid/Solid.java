package dev.junaaid96.oop.solid;

import java.util.ArrayList;
import java.util.List;

// All five SOLID principles, each with a "bad" version (in comments) and a "good" version (code).
public final class Solid {

    // Holder class.
    private Solid() {}

    // ---------------- S: Single Responsibility ----------------
    // BAD: class Invoice { total(); saveToDb(); printPdf(); }  -> three reasons to change.
    // GOOD: each class has one reason to change.
    public record Invoice(double amount, double taxRate) {
        public double total() { return amount * (1 + taxRate); }   // only business math here
    }

    // Persistence is a separate responsibility.
    public static final class InvoiceRepository {
        private final List<Invoice> store = new ArrayList<>();
        public void save(Invoice invoice) { store.add(invoice); }
        public int count() { return store.size(); }
    }

    // ---------------- O: Open/Closed ----------------
    // BAD: if (type == "EMAIL") ... else if (type == "SMS") ...  -> must edit for every new channel.
    // GOOD: open for extension (new implementation), closed for modification.
    public interface Notifier { String send(String message); }
    public static final class EmailNotifier implements Notifier { public String send(String m) { return "email:" + m; } }
    public static final class SmsNotifier implements Notifier { public String send(String m) { return "sms:" + m; } }

    // Never changes when a new Notifier is added.
    public static final class NotificationService {
        private final Notifier notifier;
        public NotificationService(Notifier notifier) { this.notifier = notifier; }
        public String notifyUser(String message) { return notifier.send(message); }
    }

    // ---------------- L: Liskov Substitution ----------------
    // BAD: class Penguin extends Bird { void fly() { throw new UnsupportedOperationException(); } }
    // GOOD: only birds that can fly implement Flyer, so every substitute honours the contract.
    public interface Bird { String name(); }
    public interface Flyer extends Bird { int altitude(); }
    public record Sparrow(String name) implements Flyer { public int altitude() { return 100; } }
    public record Penguin(String name) implements Bird {}

    // Works for every Flyer without surprises.
    public static int maxAltitude(List<? extends Flyer> flyers) {
        return flyers.stream().mapToInt(Flyer::altitude).max().orElse(0);
    }

    // ---------------- I: Interface Segregation ----------------
    // BAD: interface Worker { work(); eat(); }  -> a Robot is forced to implement eat().
    // GOOD: small, focused interfaces.
    public interface Workable { String work(); }
    public interface Eatable { String eat(); }
    public static final class Human implements Workable, Eatable {
        public String work() { return "human works"; }
        public String eat() { return "human eats"; }
    }
    public static final class Robot implements Workable {
        public String work() { return "robot works"; }
    }

    // ---------------- D: Dependency Inversion ----------------
    // BAD: class OrderService { private MySqlDatabase db = new MySqlDatabase(); }  -> tied to a concrete DB.
    // GOOD: high-level code depends on an abstraction; the concrete class is injected.
    public interface OrderStore { void save(String order); List<String> all(); }
    public static final class InMemoryOrderStore implements OrderStore {
        private final List<String> orders = new ArrayList<>();
        public void save(String order) { orders.add(order); }
        public List<String> all() { return List.copyOf(orders); }
    }
    public static final class OrderService {
        private final OrderStore store;                         // abstraction, not implementation
        public OrderService(OrderStore store) { this.store = store; }   // constructor injection
        public void place(String order) { store.save(order); }
    }
}
