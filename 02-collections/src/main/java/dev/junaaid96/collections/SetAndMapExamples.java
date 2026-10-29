package dev.junaaid96.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

// Sets (unique elements) and Maps (key -> value). Modernized from HshSt/TreeSt/LnkdHshSt/HshMp/TreeMp/LnkdHshMp.
public final class SetAndMapExamples {

    // Utility class.
    private SetAndMapExamples() {}

    // HashSet: no order guarantee, O(1) average add/contains (relies on hashCode/equals).
    public static Set<String> hashSet(String... words) {
        return new HashSet<>(Set.of(words));
    }

    // LinkedHashSet: keeps insertion order, duplicates ignored.
    public static Set<String> insertionOrdered(String... words) {
        Set<String> set = new LinkedHashSet<>();
        for (String w : words) set.add(w);   // add returns false for duplicates
        return set;
    }

    // TreeSet: sorted (red-black tree), O(log n); offers navigation methods.
    public static Integer ceiling(int target, Integer... values) {
        TreeSet<Integer> tree = new TreeSet<>(Set.of(values));
        return tree.ceiling(target);         // smallest element >= target, or null
    }

    // HashMap word count using merge(): the idiomatic "increment or insert".
    public static Map<String, Integer> wordCount(String text) {
        Map<String, Integer> counts = new HashMap<>();
        for (String w : text.toLowerCase().split("\\s+")) counts.merge(w, 1, Integer::sum);
        return counts;
    }

    // computeIfAbsent: build a multimap (key -> list) without null checks.
    public static Map<Character, List<String>> groupByFirstLetter(String... words) {
        Map<Character, List<String>> groups = new TreeMap<>();   // TreeMap = sorted keys
        for (String w : words) groups.computeIfAbsent(w.charAt(0), k -> new ArrayList<>()).add(w);
        return groups;
    }

    // LinkedHashMap with accessOrder=true + removeEldestEntry = a tiny LRU cache.
    public static <K, V> Map<K, V> lruCache(int capacity) {
        return new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;    // evict least-recently-used when full
            }
        };
    }
}
