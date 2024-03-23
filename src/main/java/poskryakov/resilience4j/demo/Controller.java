package poskryakov.resilience4j.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.function.Supplier;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

@RestController
public class Controller {

    public static final Bulkhead bulkhead = createBulkhead();
    public static final RateLimiter rateLimiter = createRateLimiter();
    public static final CircuitBreaker circuitBreaker = createCircuitBreaker();
    public static final Retry retry = createRetry();

    private final WorkoutService workoutService;
    private final CatchphraseService catchphraseService;

    public Controller(CatchphraseService catchphraseService, WorkoutService workoutService) {
        this.catchphraseService = catchphraseService;
        this.workoutService = workoutService;
    }

    // 1. Bulkhead

    @GetMapping("/catchphrase-fast")
    public String getCatchphraseFast() {
        return catchphraseService.getCatchphraseFast();
    }

    @GetMapping("/workout-slow")
    public String getWorkoutSlow() {
        return workoutService.getWorkoutSlow();
    }

    @GetMapping("/workout-slow-with-bulkhead")
    public String getWorkoutSlowWithBulkhead() {
        Supplier<String> decoratedGetWorkout =
                Bulkhead.decorateSupplier(bulkhead, workoutService::getWorkoutSlow);
        return decoratedGetWorkout.get();
    }

    private static Bulkhead createBulkhead() {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(10)
                .maxWaitDuration(Duration.ofSeconds(2))
                .build();
        return Bulkhead.of("workout-slow", config);
    }

    // 2. RateLimiter

    @GetMapping("/catchphrase-rate-sensitive")
    public String getCatchphraseRateSensitive() {
        return catchphraseService.getCatchphraseRateSensitive();
    }

    @GetMapping("/catchphrase-rate-sensitive-with-rate-limiter")
    public String getCatchphraseRateSensitiveWithRateLimiter() {
        Supplier<String> decoratedGetCatchphrase =
                RateLimiter.decorateSupplier(rateLimiter, catchphraseService::getCatchphraseRateSensitive);
        return decoratedGetCatchphrase.get();
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
        Supplier<String> decoratedGetCatchphrase =
                CircuitBreaker.decorateSupplier(circuitBreaker, catchphraseService::getCatchphraseRandomDowntime);
        return decoratedGetCatchphrase.get();
    }

    private static CircuitBreaker createCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .recordExceptions(CatchphraseException.class)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .failureRateThreshold(80.0f)
                .waitDurationInOpenState(Duration.ofSeconds(1))
                .permittedNumberOfCallsInHalfOpenState(3)
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
        Supplier<String> decoratedGetCatchphrase =
                Retry.decorateSupplier(retry, catchphraseService::getCatchphraseRandomFail);
        return decoratedGetCatchphrase.get();
    }

    private static Retry createRetry() {
        RetryConfig config = RetryConfig.custom()
                .retryExceptions(CatchphraseException.class)
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(1000, 2))
                .build();

        return Retry.of("catchphrase-random-fail", config);
    }
}
