package poskryakov.resilience4j.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.MaxRetriesExceededException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

@RestController
public class Controller {

    private static final String RESILIENCE4J_MESSAGE = "Resilience4j";
    private static final Bulkhead bulkhead = createBulkhead();
    private static final RateLimiter rateLimiter = createRateLimiter();
    private static final CircuitBreaker circuitBreaker = createCircuitBreaker();
    private static final Retry retry = createRetry();

    private final WorkoutService workoutService;
    private final CatchphraseService catchphraseService;

    public Controller(CatchphraseService catchphraseService, WorkoutService workoutService) {
        this.catchphraseService = catchphraseService;
        this.workoutService = workoutService;
    }

    // 1. Bulkhead

    @GetMapping("/catchphrase-slow")
    public String getCatchphraseFast() {
        return catchphraseService.getCatchphraseSlow();
    }

    @GetMapping("/workout-fast")
    public String getWorkoutFast() {
        return workoutService.getWorkoutFast();
    }

    @GetMapping("/catchphrase-slow-with-bulkhead")
    public String getCatchphraseSlowWithBulkhead() {
        return Decorators.ofSupplier(catchphraseService::getCatchphraseSlow)
                .withBulkhead(bulkhead)
                .withFallback(
                        List.of(BulkheadFullException.class),
                        exception -> RESILIENCE4J_MESSAGE
                )
                .get();
    }

    private static Bulkhead createBulkhead() {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(10)
                .maxWaitDuration(Duration.ofSeconds(2))
                .build();
        return Bulkhead.of("catchphrase-slow", config);
    }

    // 2. RateLimiter

    @GetMapping("/catchphrase-rate-sensitive")
    public String getCatchphraseRateSensitive() {
        return catchphraseService.getCatchphraseRateSensitive();
    }

    @GetMapping("/catchphrase-rate-sensitive-with-rate-limiter")
    public String getCatchphraseRateSensitiveWithRateLimiter() {
        return Decorators.ofSupplier(catchphraseService::getCatchphraseRateSensitive)
                .withRateLimiter(rateLimiter)
                .withFallback(
                        List.of(RequestNotPermitted.class),
                        exception -> RESILIENCE4J_MESSAGE
                )
                .get();
    }

    private static RateLimiter createRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(8)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofSeconds(1))
                .build();
        return RateLimiter.of("catchphrase-rate-sensitive", config);
    }

    // 3. CircuitBreaker

    @GetMapping("/catchphrase-random-downtime")
    public String getCatchphraseRandomDowntime() {
        return catchphraseService.getCatchphraseRandomDowntime();
    }

    @GetMapping("/catchphrase-random-downtime-with-circuit-breaker")
    public String getCatchphraseRandomDowntimeWithCircuitBreaker() {
        return Decorators.ofSupplier(catchphraseService::getCatchphraseRandomDowntime)
                .withCircuitBreaker(circuitBreaker)
                .withFallback(
                        List.of(CallNotPermittedException.class),
                        exception -> RESILIENCE4J_MESSAGE
                )
                .get();
    }

    private static CircuitBreaker createCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .recordExceptions(CatchphraseException.class)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .failureRateThreshold(80.0f)
                .waitDurationInOpenState(Duration.ofSeconds(1))
                .permittedNumberOfCallsInHalfOpenState(1)
                .build();
        return CircuitBreaker.of("catchphrase-random-downtime", config);
    }

    // 4. Retry

    @GetMapping("/catchphrase-random-fail")
    public String getCatchphraseRandomFails() {
        return catchphraseService.getCatchphraseRandomFail();
    }

    @GetMapping("/catchphrase-random-fail-with-retry")
    public String getCatchphraseRandomFail() {
        return Decorators.ofSupplier(catchphraseService::getCatchphraseRandomFail)
                .withRetry(retry)
                .withFallback(
                        List.of(MaxRetriesExceededException.class),
                        exception -> RESILIENCE4J_MESSAGE
                )
                .get();
    }

    private static Retry createRetry() {
        RetryConfig config = RetryConfig.custom()
                .retryExceptions(CatchphraseException.class)
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(1000, 2))
                .build();
        return Retry.of("catchphrase-random-fail", config);
    }
}
