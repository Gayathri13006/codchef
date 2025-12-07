import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

/**
 * StudentGradeCalculator
 *
 * Console program that:
 *  - accepts number of subjects
 *  - accepts marks (out of 100) for each subject
 *  - calculates total, average percentage
 *  - assigns a grade based on average percentage
 *  - shows results
 */
public class StudentGradeCalculator {

    public static void main(String[] args) {
        // Use a Scanner for console input
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        System.out.println("=== Student Grade Calculator ===");

        int numSubjects = 0;
        while (true) {
            try {
                System.out.print("Enter number of subjects: ");
                numSubjects = scanner.nextInt();
                if (numSubjects <= 0) {
                    System.out.println("Please enter a positive integer for number of subjects.");
                    continue;
                }
                break;
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine(); // clear invalid token
            }
        }

        double total = 0.0;
        for (int i = 1; i <= numSubjects; i++) {
            double mark = -1;
            while (true) {
                try {
                    System.out.printf("Enter marks for subject %d (0 - 100): ", i);
                    mark = scanner.nextDouble();
                    if (mark < 0 || mark > 100) {
                        System.out.println("Marks must be between 0 and 100. Try again.");
                        continue;
                    }
                    break;
                } catch (InputMismatchException ime) {
                    System.out.println("Invalid input. Please enter a numeric value (0 - 100).");
                    scanner.nextLine(); // clear invalid token
                }
            }
            total += mark;
        }

        // calculations
        double average = total / numSubjects; // average percentage
        String grade = calculateGrade(average);

        // display results (formatting to 2 decimal places)
        System.out.println("\n=== Result ===");
        System.out.printf("Total Marks      : %.2f out of %.2f%n", total, numSubjects * 100.0);
        System.out.printf("Average Percent  : %.2f%%%n", average);
        System.out.printf("Grade            : %s%n", grade);

        scanner.close();
    }

    /**
     * Returns grade string based on average percentage.
     * Grade boundaries (example):
     *  90 - 100 : A+
     *  80 - 89.99: A
     *  70 - 79.99: B
     *  60 - 69.99: C
     *  50 - 59.99: D
     *  <50      : F
     */
    private static String calculateGrade(double avg) {
        if (avg >= 90.0) {
            return "A+";
        } else if (avg >= 80.0) {
            return "A";
        } else if (avg >= 70.0) {
            return "B";
        } else if (avg >= 60.0) {
            return "C";
        } else if (avg >= 50.0) {
            return "D";
        } else {
            return "F";
        }
    }
}