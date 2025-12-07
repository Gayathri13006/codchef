package NumberGame;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.Scanner;

/**
 * NumberGame.java
 *
 * Single-file console implementation:
 * - Difficulty levels (Easy / Medium / Hard)
 * - Limited attempts
 * - Multiple rounds
 * - Warmer/colder hint
 * - Scoring & persistent high score saved to highscore.txt
 * * Compile: javac NumberGame.java
 * Run:     java NumberGame
 */
public class NumberGame {
    private static final Path HIGHSCORE_PATH = Paths.get("highscore.txt");
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    // Difficulty container
    static class Difficulty {
        final String name;
        final int min, max, attempts, multiplier;
        Difficulty(String name, int min, int max, int attempts, int multiplier) {
            this.name = name; this.min = min; this.max = max; this.attempts = attempts; this.multiplier = multiplier;
        }
    }

    // Predefined difficulties
    private static final Difficulty EASY = new Difficulty("Easy", 1, 20, 8, 1);
    private static final Difficulty MEDIUM = new Difficulty("Medium", 1, 100, 7, 2);
    private static final Difficulty HARD = new Difficulty("Hard", 1, 1000, 10, 3);

    public static void main(String[] args) {
        System.out.println("=== NUMBER GAME (Java) ===");
        HighScore hs = loadHighScore();
        if (hs != null) {
            System.out.printf("Current high score: %d by %s%n", hs.score, hs.name);
        } else {
            System.out.println("No high score yet. Set the first one!");
        }

        int roundsPlayed = 0;
        int totalScore = 0;

        while (true) {
            roundsPlayed++;
            Difficulty chosen = chooseDifficulty();
            int roundScore = playRound(chosen);
            totalScore += roundScore;

            if (roundScore > 0) {
                HighScore current = loadHighScore();
                if (current == null || roundScore > current.score) {
                    System.out.print("New high score! Enter your name: ");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) name = "Player";
                    saveHighScore(new HighScore(name, roundScore));
                    System.out.printf("Saved high score: %s %d%n", name, roundScore);
                }
            }

            System.out.printf("Total score after %d rounds: %d%n", roundsPlayed, totalScore);
            System.out.print("Play again? (y/n): ");
            String again = scanner.nextLine().trim().toLowerCase();
            if (!again.equals("y") && !again.equals("yes")) break;
        }

        System.out.printf("Thanks for playing! Rounds: %d, Total score: %d%n", roundsPlayed, totalScore);
    }

    private static Difficulty chooseDifficulty() {
        System.out.println("\nChoose difficulty:");
        System.out.println("1) Easy   (1-20, 8 attempts, ×1)");
        System.out.println("2) Medium (1-100, 7 attempts, ×2)");
        System.out.println("3) Hard   (1-1000, 10 attempts, ×3)");
        while (true) {
            System.out.print("Enter 1/2/3: ");
            String line = scanner.nextLine().trim();
            switch (line) {
                case "1": return EASY;
                case "2": return MEDIUM;
                case "3": return HARD;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static int playRound(Difficulty diff) {
        int secret = random.nextInt(diff.max - diff.min + 1) + diff.min;
        int attemptsLeft = diff.attempts;
        Integer prevDiff = null;
        Instant start = Instant.now();

        System.out.printf("%nI've picked a number between %d and %d. You have %d attempts.%n",
                diff.min, diff.max, diff.attempts);

        int attemptsUsed = 0;
        while (attemptsLeft > 0) {
            attemptsUsed++;
            int guess = promptGuess(diff.min, diff.max);
            if (guess == secret) {
                Instant end = Instant.now();
                long seconds = Duration.between(start, end).getSeconds();
                int baseScore = Math.max(0, (diff.attempts - attemptsUsed + 1)) * diff.multiplier * 10;
                int timeBonus = (int)Math.max(0, 30 - seconds); // small time bonus
                int total = baseScore + timeBonus;
                System.out.printf("🎉 Correct! The number was %d. Attempts used: %d. Time: %ds%n", secret, attemptsUsed, seconds);
                System.out.printf("Round score: %d (base %d + time bonus %d)%n%n", total, baseScore, timeBonus);
                return total;
            } else {
                if (guess > secret) {
                    System.out.println("Too high.");
                } else {
                    System.out.println("Too low.");
                }

                int curDiff = Math.abs(secret - guess);
                if (prevDiff != null) {
                    if (curDiff < prevDiff) System.out.println("You're getting warmer (closer) than last guess.");
                    else if (curDiff > prevDiff) System.out.println("You're getting colder (farther) than last guess.");
                    else System.out.println("Same distance as last guess.");
                } else {
                    // initial hint
                    if (curDiff <= Math.max(1, (diff.max - diff.min) / 10)) {
                        System.out.println("Very close!");
                    }
                }
                prevDiff = curDiff;
            }
            attemptsLeft--;
            System.out.printf("Attempts left: %d%n%n", attemptsLeft);
        }

        System.out.printf("😞 Out of attempts. The number was %d.%n%n", secret);
        return 0;
    }

    private static int promptGuess(int min, int max) {
        while (true) {
            System.out.printf("Enter your guess (%d-%d): ", min, max);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val < min || val > max) {
                    System.out.printf("Please enter a number between %d and %d.%n", min, max);
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // --- High score helpers ---
    private static class HighScore {
        final String name;
        final int score;
        HighScore(String name, int score) { this.name = name; this.score = score; }
    }

    private static HighScore loadHighScore() {
        if (!Files.exists(HIGHSCORE_PATH)) return null;
        try {
            String content = new String(Files.readAllBytes(HIGHSCORE_PATH)).trim();
            if (content.isEmpty()) return null;
            String[] parts = content.split("\\s+", 2);
            if (parts.length < 2) return null;
            String name = parts[0];
            int score = Integer.parseInt(parts[1]);
            return new HighScore(name, score);
        } catch (IOException | NumberFormatException e) {
            // if file corrupt or unreadable, ignore and treat as no highscore
            return null;
        }
    }

    private static void saveHighScore(HighScore hs) {
        String line = hs.name + " " + hs.score;
        try {
            Files.write(HIGHSCORE_PATH, line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }
}
