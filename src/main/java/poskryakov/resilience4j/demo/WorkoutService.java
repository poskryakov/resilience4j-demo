package poskryakov.resilience4j.demo;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class WorkoutService {

    public String getWorkoutSlow() {
        // Response with random delay. High response time.
        Util.sleepRandom(7000, 10000);
        int randomIndex = ThreadLocalRandom.current().nextInt(Util.workouts.size());
        return Util.workouts.get(randomIndex);
    }
}
