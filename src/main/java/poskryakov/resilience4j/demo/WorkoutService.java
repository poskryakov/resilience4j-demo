package poskryakov.resilience4j.demo;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class WorkoutService {

    public String getWorkoutSlow() {
        // Response with random delay. High response time.
        Utils.sleepRandom(5000, 10000);
        int randomIndex = ThreadLocalRandom.current().nextInt(Utils.workouts.size());
        return Utils.workouts.get(randomIndex);
    }
}
