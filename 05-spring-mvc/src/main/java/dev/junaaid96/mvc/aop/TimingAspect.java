package dev.junaaid96.mvc.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

// AOP: cross-cutting logic (timing, logging) kept out of business code. Modernized from the old SpringAOP module.
@Aspect
@Component
public class TimingAspect {

    // Counts advised calls so tests can prove the aspect ran.
    private final AtomicLong calls = new AtomicLong();

    // Pointcut expression: every public method in the service package.
    @Around("execution(public * dev.junaaid96.mvc.service..*(..))")
    public Object time(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();          // before advice
        try {
            return pjp.proceed();                // run the real method
        } finally {
            calls.incrementAndGet();             // after advice (runs even on exceptions)
            long micros = (System.nanoTime() - start) / 1000;
            System.out.println(pjp.getSignature().toShortString() + " took " + micros + "us");
        }
    }

    public long calls() { return calls.get(); }
}
