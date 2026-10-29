package dev.junaaid96.collections;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// List = ordered, index-based, allows duplicates. Modernized from ArrLst/LnkLst/Vect/Stck.
public final class ListExamples {

    // Utility class.
    private ListExamples() {}

    // ArrayList: backed by a resizable array -> O(1) get(i), amortized O(1) add at end, O(n) insert in middle.
    public static List<String> arrayListBasics() {
        // Program to the interface (List), not the implementation.
        List<String> list = new ArrayList<>();
        list.add("b");          // append
        list.add(0, "a");       // insert at index (shifts elements)
        list.add("c");
        list.set(2, "C");       // replace by index
        list.remove("b");       // remove by value (first match)
        return list;            // [a, C]
    }

    // LinkedList: doubly linked nodes -> O(1) add/remove at ends, O(n) get(i). Also a Deque.
    public static LinkedList<Integer> linkedListBasics() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addFirst(2);       // head insert O(1)
        list.addLast(3);        // tail insert O(1)
        list.addFirst(1);
        return list;            // [1, 2, 3]
    }

    // Fail-fast iterators: modifying a list during a for-each throws ConcurrentModificationException.
    public static boolean failFastDemo() {
        List<Integer> nums = new ArrayList<>(List.of(1, 2, 3));
        try {
            // for-each uses an Iterator under the hood; list.remove changes modCount.
            for (Integer n : nums) if (n == 1) nums.remove(n);
            return false;
        } catch (ConcurrentModificationException e) {
            return true;        // expected
        }
    }

    // Correct ways to remove while iterating: Iterator.remove() or removeIf().
    public static List<Integer> safeRemoval() {
        List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        // Iterator.remove keeps the iterator in sync.
        for (Iterator<Integer> it = nums.iterator(); it.hasNext(); ) if (it.next() == 1) it.remove();
        // removeIf is the concise modern form.
        nums.removeIf(n -> n % 2 == 0);
        return nums;            // [3, 5]
    }

    // Immutability: List.of is unmodifiable; List.copyOf makes a defensive unmodifiable copy.
    public static List<String> immutableCopy(List<String> source) {
        return List.copyOf(source);
    }

    // Java 21 SequencedCollection: getFirst/getLast/reversed work on every ordered collection.
    public static String lastOf(List<String> list) {
        return list.getLast();
    }
}
