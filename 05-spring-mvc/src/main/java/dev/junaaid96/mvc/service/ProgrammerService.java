package dev.junaaid96.mvc.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// A Spring bean (singleton by default). Thread-safe map because one instance serves all requests.
@Service
public class ProgrammerService {

    private final Map<Integer, Programmer> store = new ConcurrentHashMap<>();

    // Save or replace a programmer.
    public Programmer save(Programmer p) {
        store.put(p.id(), p);
        return p;
    }

    // Optional signals "may be absent" to callers.
    public Optional<Programmer> find(int id) { return Optional.ofNullable(store.get(id)); }

    // Return a copy so callers cannot mutate internal state.
    public List<Programmer> all() { return List.copyOf(store.values()); }
}
