package poskryakov.resilience4j.demo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static final List<String> catchphrases = List.of(
            "I’ll be back. (The Terminator, and so many more)",
            "Come with me if you want to live. (Terminator 2: Judgment Day)",
            "Hasta la vista, baby. (Terminator 2: Judgment Day)",
            "If it bleeds, we can kill it. (Predator)",
            "Get to the chopper! (Predator)",
            "All right, everyone! Chill! (Batman & Robin)",
            "Put that cookie down, now! (Jingle All the Way)",
            "To crush your enemies, see them driven before you, and to hear the lamentation of their women! (Conan the Barbarian)",
            "Don’t disturb my friend. He’s dead tired. (Commando)",
            "Dillon! You son of a... (Predator)",
            "It’s Turbo Time! (Jingle All the Way)",
            "I’m afraid that my condition has left me cold to your pleas of mercy. (Batman & Robin)",
            "You've just been erased. (Eraser)",
            "I eat Green Berets for breakfast. And right now, I'm very hungry! (Commando)",
            "I'm not into politics, I'm into survival. (The Running Man)",
            "I did nothing. The pavement was his enemy. (Twins)",
            "Consider that a divorce. (Total Recall)",
            "Cocainum! (Red Heat)",
            "Let off some steam, Bennett. (Commando)",
            "Here is Subzero! Now, plain zero! (The Running Man)",
            "Rubber baby buggy bumpers! (Last Action Hero)",
            "You should not drink and bake. (Raw Deal)",
            "You’re a choir boy compared to me! (End of Days)",
            "It’s not a tumor! (Kindergarten Cop)",
            "Always winterize your pipes. (Batman & Robin)",
            "I let him go. (Commando)",
            "Remember when I said I’d promise to kill you last? I lied. (Commando)",
            "See you at the party, Richter! (Total Recall)",
            "You should clone yourself. So you can go… (The 6th Day)",
            "You’re fired. (True Lies)",
            "Allow me to break the ice. (Batman & Robin)",
            "Stick around. (Predator)"
    );

    public static final List<String> workouts = List.of(
            "Bench press: 5 sets of 6-10 reps",
            "Cable crossovers: 6 sets of 10-12 reps",
            "Flat bench flyes: 5 sets of 6-10 reps",
            "Dumbbell pullovers: 5 sets of 10-12 reps",
            "Incline bench press: 6 sets of 6-10 reps",
            "Dips: 5 sets, each one to failure",
            "Wide-grip chin-ups: 6 sets of failure",
            "Pulley rows: 6 sets of 6-10 reps",
            "T-bar rows: 5 sets of 6-10 reps",
            "Straight-leg deadlifts: 6 sets of 15 reps",
            "One-arm dumbbell rows: 5 sets of 6-10 reps",
            "Leg presses: 6 sets of 8-12 reps",
            "Leg curls: 6 sets of 10-12 reps",
            "Barbell lunges: 5 sets of 15 reps",
            "Leg extensions: 6 sets of 12-15 reps",
            "Squats: 6 sets of 8-12 reps",
            "Standing calf raises: 10 sets of 10 reps",
            "Seated calf raises: 8 sets of 15 reps",
            "One-legged calf raises (with weights): 6 sets of 12 reps",
            "Wrist curls: 4 sets of 10 reps",
            "Reverse barbell curls: 4 sets of 8 reps",
            "Wright roller machine: 4 sets to failure",
            "Seated barbell presses: 6 sets of 6-10 reps",
            "Lateral raises: 6 sets of 6-10 reps",
            "Rear-delt lateral raises: 5 sets of 6-10 reps",
            "Cable lateral raises: 5 sets of 10-12 reps",
            "Barbell curls: 6 sets of 6-10 reps",
            "Seated dumbbell curls: 6 sets of 6-10 reps",
            "Dumbbell concentration curls: 6 sets of 6-10 reps",
            "Close-grip bench presses: 6 sets of 6-10 reps",
            "Pushdowns: 6 sets of 6-10 reps",
            "Barbell French presses: 6 sets of 6-10 reps",
            "One-arm dumbbell triceps extensions: 6 sets of 6-10 reps",
            "Seated barbell presses: 6 sets of 6-10 reps",
            "Lateral raises: 6 sets of 6-10 reps",
            "Rear-delt lateral raises: 5 sets of 6-10 reps",
            "Cable lateral raises: 5 sets of 10-12 reps",
            "Standing calf raises: 10 sets of 10 reps",
            "Seated calf raises: 8 sets of 15 reps",
            "One-legged calf raises (with weights): 6 sets of 12 reps"
    );

    public static void sleepRandom(int fromMillis, int toMillis) {
        try {
            Thread.sleep(fromMillis + ThreadLocalRandom.current().nextInt(toMillis - fromMillis));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean rollDice(int riskChances, int totalChances) {
        return ThreadLocalRandom.current().nextInt(totalChances) < riskChances;
    }
}
