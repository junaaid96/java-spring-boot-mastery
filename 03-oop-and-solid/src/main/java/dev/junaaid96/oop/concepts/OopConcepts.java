package dev.junaaid96.oop.concepts;

// The four OOP pillars in one file: encapsulation, abstraction, inheritance, polymorphism.
public final class OopConcepts {

    // Utility holder.
    private OopConcepts() {}

    // ENCAPSULATION: state is private; changes go through methods that enforce rules.
    public static final class BankAccount {
        private double balance;                       // hidden state

        public void deposit(double amount) {
            if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
            balance += amount;                        // only valid changes allowed
        }

        public double balance() { return balance; }   // read-only access
    }

    // ABSTRACTION: callers depend on "what" (speak), not "how".
    public abstract static class Animal {
        private final String name;                    // shared state lives in the base class

        protected Animal(String name) { this.name = name; }

        public String name() { return name; }

        public abstract String sound();              // subclasses must provide this

        // Template method: common algorithm, varying step.
        public String speak() { return name + " says " + sound(); }
    }

    // INHERITANCE: Dog "is-a" Animal and reuses its code.
    public static final class Dog extends Animal {
        public Dog(String name) { super(name); }      // call parent constructor
        @Override public String sound() { return "Woof"; }
    }

    // Another subclass for runtime polymorphism.
    public static final class Cat extends Animal {
        public Cat(String name) { super(name); }
        @Override public String sound() { return "Meow"; }
    }

    // COMPILE-TIME POLYMORPHISM (overloading): same name, different parameter lists.
    public static int add(int a, int b) { return a + b; }
    public static double add(double a, double b) { return a + b; }
    public static String add(String a, String b) { return a + b; }

    // RUNTIME POLYMORPHISM (overriding): the actual object type decides which sound() runs.
    public static String chorus(Animal... animals) {
        StringBuilder sb = new StringBuilder();
        for (Animal a : animals) sb.append(a.sound()).append(' ');
        return sb.toString().trim();
    }
}
