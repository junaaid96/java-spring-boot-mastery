package dev.junaaid96.javacore;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

// Virtual threads (final since Java 21) and ScopedValue (final in Java 25).
public final class ConcurrencyBasics {

    // A ScopedValue is an immutable, thread-safe alternative to ThreadLocal for passing context.
    public static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    // Utility class.
    private ConcurrencyBasics() {}

    // Run many blocking tasks on virtual threads: cheap, so one thread per task is fine.
    public static int runOnVirtualThreads(int tasks) throws InterruptedException {
        // Thread-safe counter shared by all tasks.
        AtomicInteger done = new AtomicInteger();
        // try-with-resources closes the executor, which waits for all tasks to finish.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Submit `tasks` small blocking jobs.
            IntStream.range(0, tasks).forEach(i -> executor.submit(() -> {
                // Simulate blocking I/O; a virtual thread unmounts instead of blocking an OS thread.
                Thread.sleep(10);
                // Record completion atomically.
                return done.incrementAndGet();
            }));
        }
        // All tasks are complete once the executor is closed.
        return done.get();
    }

    // Fan-out / fan-in with Futures: compute several values in parallel and combine them.
    public static int sumInParallel(List<Integer> numbers) throws InterruptedException, ExecutionException {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Start one task per number (squares it).
            List<Future<Integer>> futures = numbers.stream().map(n -> executor.submit(() -> n * n)).toList();
            // Join: get() blocks until each result is ready.
            int sum = 0;
            for (Future<Integer> f : futures) sum += f.get();
            return sum;
        }
    }

    // Bind a request id for the duration of a call; code deeper in the stack can read it.
    public static String handleRequest(String id) {
        return ScopedValue.where(REQUEST_ID, id).call(ConcurrencyBasics::deepServiceCall);
    }

    // Reads the scoped value without it being passed as a parameter.
    private static String deepServiceCall() {
        return "processed " + REQUEST_ID.get();
    }
}
