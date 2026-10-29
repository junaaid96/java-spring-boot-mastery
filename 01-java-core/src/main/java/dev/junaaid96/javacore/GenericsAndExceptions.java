package dev.junaaid96.javacore;

import java.util.Collection;
import java.util.List;

// Generics (type safety + reuse) and modern exception handling.
public final class GenericsAndExceptions {

    // Utility class.
    private GenericsAndExceptions() {}

    // Bounded type parameter: T must be Comparable to itself, so we can compare elements.
    public static <T extends Comparable<? super T>> T max(Collection<? extends T> items) {
        // Fail fast on bad input instead of returning null.
        if (items.isEmpty()) throw new IllegalArgumentException("empty collection");
        T best = null;
        for (T item : items) {
            // compareTo comes from the Comparable bound.
            if (best == null || item.compareTo(best) > 0) best = item;
        }
        return best;
    }

    // PECS: "Producer Extends, Consumer Super" -> src produces Numbers, dst consumes them.
    public static void copyNumbers(List<? extends Number> src, List<? super Number> dst) {
        dst.addAll(src);
    }

    // A custom unchecked exception with a meaningful name and message.
    public static final class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(double needed) {
            super("Insufficient funds, need " + needed + " more");
        }
    }

    // A small resource to show try-with-resources closing automatically.
    public static final class Resource implements AutoCloseable {
        // Tracks whether close() was called, for the test.
        private boolean closed;
        public boolean isClosed() { return closed; }
        @Override public void close() { closed = true; }
    }

    // Throws a domain exception when a rule is violated.
    public static double withdraw(double balance, double amount) {
        if (amount > balance) throw new InsufficientFundsException(amount - balance);
        return balance - amount;
    }

    // try-with-resources guarantees close() even if the body throws.
    public static Resource useResource() {
        Resource r = new Resource();
        try (r) {
            // Work with the resource here.
        }
        return r;
    }
}
