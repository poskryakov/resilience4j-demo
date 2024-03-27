package poskryakov.resilience4j.demo;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class WorkoutService {

    public String getWorkoutFast() {
        // Response with random delay. Low response time.
        Util.sleepRandom(500, 1000);
        int randomIndex = ThreadLocalRandom.current().nextInt(Util.workouts.size());
        return Util.workouts.get(randomIndex);
    }
}
