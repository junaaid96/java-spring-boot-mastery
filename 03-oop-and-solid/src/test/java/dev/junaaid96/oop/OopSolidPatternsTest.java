package dev.junaaid96.oop;

import dev.junaaid96.oop.concepts.OopConcepts;
import dev.junaaid96.oop.patterns.Patterns;
import dev.junaaid96.oop.solid.Solid;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

// Tests that show each concept, principle and pattern in action.
class OopSolidPatternsTest {

    @Test
    void oopPillars() {
        var account = new OopConcepts.BankAccount();
        account.deposit(50);
        assertThat(account.balance()).isEqualTo(50);
        assertThatThrownBy(() -> account.deposit(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(new OopConcepts.Dog("Rex").speak()).isEqualTo("Rex says Woof");
        assertThat(OopConcepts.chorus(new OopConcepts.Dog("a"), new OopConcepts.Cat("b"))).isEqualTo("Woof Meow");
        assertThat(OopConcepts.add("a", "b")).isEqualTo("ab");
    }

    @Test
    void solidPrinciples() {
        assertThat(new Solid.Invoice(100, 0.1).total()).isCloseTo(110, within(0.001));
        assertThat(new Solid.NotificationService(new Solid.SmsNotifier()).notifyUser("hi")).isEqualTo("sms:hi");
        assertThat(Solid.maxAltitude(List.of(new Solid.Sparrow("s")))).isEqualTo(100);
        assertThat(new Solid.Penguin("p")).isNotInstanceOf(Solid.Flyer.class);
        assertThat(new Solid.Robot()).isNotInstanceOf(Solid.Eatable.class);
        var store = new Solid.InMemoryOrderStore();
        new Solid.OrderService(store).place("book");
        assertThat(store.all()).containsExactly("book");
    }

    @Test
    void designPatterns() {
        assertThat(Patterns.AppConfig.INSTANCE.get("env")).isEqualTo("dev");
        assertThat(Patterns.paymentFor("card").pay(5)).isEqualTo("card 5.0");
        var pizza = Patterns.Pizza.builder().size("L").topping("olive").extraCheese().build();
        assertThat(pizza.toppings()).containsExactly("olive");
        assertThat(Patterns.checkout(100, p -> p * 0.9)).isEqualTo(90);
        var bus = new Patterns.EventBus<String>();
        List<String> got = new ArrayList<>();
        bus.subscribe(got::add);
        bus.publish("created");
        assertThat(got).containsExactly("created");
        var shout = new Patterns.Decorated(new Patterns.PlainText("hi"), String::toUpperCase);
        assertThat(shout.read()).isEqualTo("HI");
        assertThat(new Patterns.ThermometerAdapter(new Patterns.LegacyThermometer()).celsius()).isEqualTo(100);
    }
}
