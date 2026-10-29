package dev.junaaid96.javacore;

import dev.junaaid96.javacore.RecordsAndSealed.*;
import dev.junaaid96.javacore.StreamsAndOptional.Employee;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

// Tests double as executable documentation: read them to see expected behaviour.
class JavaCoreTest {

    // Sample data reused by the stream tests.
    private final List<Employee> staff = List.of(
            new Employee("Ana", "ENG", 120), new Employee("Bo", "ENG", 80), new Employee("Cy", "HR", 70));

    @Test
    void sealedShapesAndPatterns() {
        // Exhaustive switch over a sealed hierarchy.
        assertThat(RecordsAndSealed.area(new Rectangle(2, 3))).isEqualTo(6);
        assertThat(RecordsAndSealed.area(new Square(4))).isEqualTo(16);
        // Compact constructor rejects invalid state.
        assertThatThrownBy(() -> new Circle(-1)).isInstanceOf(IllegalArgumentException.class);
        // Guarded patterns.
        assertThat(RecordsAndSealed.describe(500)).isEqualTo("big int 500");
        assertThat(RecordsAndSealed.describe("hi")).isEqualTo("string of length 2");
        assertThat(RecordsAndSealed.describe(null)).isEqualTo("null");
        // Text block output.
        assertThat(RecordsAndSealed.jsonFor("Ana", 30)).isEqualTo("{\"name\": \"Ana\", \"age\": 30}");
    }

    @Test
    void streamsAndOptional() {
        assertThat(StreamsAndOptional.highEarners(staff, 75)).containsExactly("Ana", "Bo");
        assertThat(StreamsAndOptional.averageSalaryByDepartment(staff)).containsEntry("ENG", 100.0);
        assertThat(StreamsAndOptional.departmentOf(staff, "Zed")).isEqualTo("UNKNOWN");
    }

    @Test
    void concurrency() throws Exception {
        // 10_000 virtual threads finish quickly.
        assertThat(ConcurrencyBasics.runOnVirtualThreads(10_000)).isEqualTo(10_000);
        assertThat(ConcurrencyBasics.sumInParallel(List.of(1, 2, 3))).isEqualTo(14);
        assertThat(ConcurrencyBasics.handleRequest("req-1")).isEqualTo("processed req-1");
    }

    @Test
    void genericsAndExceptions() {
        assertThat(GenericsAndExceptions.max(List.of(3, 9, 4))).isEqualTo(9);
        List<Object> sink = new ArrayList<>();
        GenericsAndExceptions.copyNumbers(List.of(1, 2.5), sink);
        assertThat(sink).hasSize(2);
        assertThatThrownBy(() -> GenericsAndExceptions.withdraw(10, 25))
                .isInstanceOf(GenericsAndExceptions.InsufficientFundsException.class);
        assertThat(GenericsAndExceptions.useResource().isClosed()).isTrue();
    }
}
