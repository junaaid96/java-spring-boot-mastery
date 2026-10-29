package dev.junaaid96.collections;

import dev.junaaid96.collections.QueueAndSortingExamples.Student;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

// Executable documentation for the collections module.
class CollectionsTest {

    @Test
    void lists() {
        assertThat(ListExamples.arrayListBasics()).containsExactly("a", "C");
        assertThat(ListExamples.linkedListBasics()).containsExactly(1, 2, 3);
        assertThat(ListExamples.failFastDemo()).isTrue();
        assertThat(ListExamples.safeRemoval()).containsExactly(3, 5);
        // Unmodifiable copies throw on mutation.
        assertThatThrownBy(() -> ListExamples.immutableCopy(List.of("x")).add("y"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(ListExamples.lastOf(List.of("a", "z"))).isEqualTo("z");
    }

    @Test
    void setsAndMaps() {
        assertThat(SetAndMapExamples.hashSet("a", "b")).containsExactlyInAnyOrder("a", "b");
        assertThat(SetAndMapExamples.insertionOrdered("b", "a", "b")).containsExactly("b", "a");
        assertThat(SetAndMapExamples.ceiling(5, 1, 4, 7, 9)).isEqualTo(7);
        assertThat(SetAndMapExamples.wordCount("a b A")).containsEntry("a", 2).containsEntry("b", 1);
        assertThat(SetAndMapExamples.groupByFirstLetter("apple", "avocado", "kiwi").get('a')).hasSize(2);
        Map<Integer, String> lru = SetAndMapExamples.lruCache(2);
        lru.put(1, "one"); lru.put(2, "two"); lru.get(1); lru.put(3, "three");   // 2 is least recently used
        assertThat(lru).containsOnlyKeys(1, 3);
    }

    @Test
    void queuesAndSorting() {
        assertThat(QueueAndSortingExamples.balancedBrackets("{[()]}")).isTrue();
        assertThat(QueueAndSortingExamples.balancedBrackets("(]")).isFalse();
        assertThat(QueueAndSortingExamples.topK(List.of(5, 1, 9, 3, 7), 2)).containsExactly(9, 7);
        List<Student> s = List.of(new Student("Zoe", 10, 3.9), new Student("Al", 10, 3.1), new Student("Bo", 9, 3.6));
        assertThat(QueueAndSortingExamples.sortStudents(s)).extracting(Student::name).containsExactly("Al", "Zoe", "Bo");
        assertThat(QueueAndSortingExamples.honorRoll(s).get(true)).containsExactly("Zoe", "Bo");
        assertThat(QueueAndSortingExamples.names(s)).isEqualTo("[Zoe, Al, Bo]");
    }
}
