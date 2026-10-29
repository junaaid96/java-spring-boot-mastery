package dev.junaaid96.collections;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

// Queues/Deques (from PQ/ArrDeq/Stck), Comparator-based sorting and stream collectors.
public final class QueueAndSortingExamples {

    // Domain record used for sorting/collecting.
    public record Student(String name, int grade, double gpa) {}

    // Utility class.
    private QueueAndSortingExamples() {}

    // ArrayDeque as a stack (LIFO) - preferred over the legacy synchronized Stack class.
    public static boolean balancedBrackets(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '(', '[', '{' -> stack.push(c);                     // open: push
                case ')' -> { if (stack.isEmpty() || stack.pop() != '(') return false; }
                case ']' -> { if (stack.isEmpty() || stack.pop() != '[') return false; }
                case '}' -> { if (stack.isEmpty() || stack.pop() != '{') return false; }
                default -> { }                                           // ignore other chars
            }
        }
        return stack.isEmpty();                                          // all opened were closed
    }

    // PriorityQueue is a binary min-heap: poll() always returns the smallest per the comparator.
    public static List<Integer> topK(List<Integer> nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int n : nums) {
            minHeap.offer(n);                       // O(log k)
            if (minHeap.size() > k) minHeap.poll(); // drop smallest so only k largest remain
        }
        return minHeap.stream().sorted(Comparator.reverseOrder()).toList();
    }

    // Comparator chaining: grade descending, then name ascending.
    public static List<Student> sortStudents(List<Student> students) {
        return students.stream()
                .sorted(Comparator.comparingInt(Student::grade).reversed().thenComparing(Student::name))
                .toList();
    }

    // Collectors: partitioningBy splits into true/false buckets.
    public static Map<Boolean, List<String>> honorRoll(List<Student> students) {
        return students.stream().collect(Collectors.partitioningBy(s -> s.gpa() >= 3.5,
                Collectors.mapping(Student::name, Collectors.toList())));
    }

    // Collectors.joining builds a string from elements.
    public static String names(List<Student> students) {
        return students.stream().map(Student::name).collect(Collectors.joining(", ", "[", "]"));
    }
}
