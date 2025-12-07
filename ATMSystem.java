//package ATMSystem;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

/**
 * ATMSystem.java
 *
 * Single-file console ATM simulation:
 * - BankAccount class models a user's account
 * - ATM class provides menu-driven UI and operations
 * - Main class starts the ATM and demonstrates usage
 *
 * Compile: javac ATMSystem.java
 * Run:     java ATMSystem
 */

class BankAccount {
    private String accountHolder;
    private final String accountNumber;
    private double balance;

    public BankAccount(String accountHolder, String accountNumber, double initialBalance) {
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
        this.balance = Math.max(0.0, initialBalance);
    }

    // synchronized for thread-safety if used concurrently
    public synchronized boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public synchronized boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        return true;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}

class ATM {
    private final BankAccount account;
    private final Scanner scanner;

    public ATM(BankAccount account, Scanner scanner) {
        this.account = account;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("===================================");
        System.out.println("   Welcome to Simple Java ATM");
        System.out.println("===================================");
        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = promptInt("Choose an option: ");
            switch (choice) {
                case 1:
                    handleCheckBalance();
                    break;
                case 2:
                    handleDeposit();
                    break;
                case 3:
                    handleWithdraw();
                    break;
                case 4:
                    handleMiniStatement(); // simple message; extendable
                    break;
                case 5:
                    System.out.println("Thank you for using Simple Java ATM. Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please choose between 1 and 5.");
            }
            System.out.println(); // blank line for readability
        }
    }

    private void printMenu() {
        System.out.println("Menu:");
        System.out.println("  1. Check Balance");
        System.out.println("  2. Deposit");
        System.out.println("  3. Withdraw");
        System.out.println("  4. Mini-statement (summary)");
        System.out.println("  5. Exit");
    }

    private void handleCheckBalance() {
        double bal = account.getBalance();
        System.out.printf("Account: %s (%s)%n", account.getAccountHolder(), account.getAccountNumber());
        System.out.printf("Current Balance: %.2f%n", bal);
    }

    private void handleDeposit() {
        double amount = promptDouble("Enter amount to deposit: ");
        if (amount <= 0) {
            System.out.println("Deposit failed: amount must be positive.");
            return;
        }
        boolean ok = account.deposit(amount);
        if (ok) {
            System.out.printf("Deposit successful: %.2f added.%n", amount);
            System.out.printf("New Balance: %.2f%n", account.getBalance());
        } else {
            System.out.println("Deposit failed.");
        }
    }

    private void handleWithdraw() {
        double amount = promptDouble("Enter amount to withdraw: ");
        if (amount <= 0) {
            System.out.println("Withdrawal failed: amount must be positive.");
            return;
        }
        // You can add withdrawal limits here (e.g., daily limit) if desired
        if (amount > account.getBalance()) {
            System.out.println("Withdrawal failed: insufficient balance.");
            System.out.printf("Available Balance: %.2f%n", account.getBalance());
            return;
        }
        boolean ok = account.withdraw(amount);
        if (ok) {
            System.out.printf("Please collect your cash: %.2f%n", amount);
            System.out.printf("Remaining Balance: %.2f%n", account.getBalance());
        } else {
            System.out.println("Withdrawal failed.");
        }
    }

    private void handleMiniStatement() {
        // For simplicity, we show a short summary. Extend to store transactions for a true statement.
        System.out.println("Mini-statement (summary):");
        System.out.printf("Account Holder : %s%n", account.getAccountHolder());
        System.out.printf("Account Number : %s%n", account.getAccountNumber());
        System.out.printf("Available Bal. : %.2f%n", account.getBalance());
        System.out.println("(Full transaction history feature can be added.)");
    }

    // Helper: prompt integer with validation
    private int promptInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int val = scanner.nextInt();
                scanner.nextLine(); // consume newline
                return val;
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine(); // clear invalid token
            }
        }
    }

    // Helper: prompt double with validation
    private double promptDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double val = scanner.nextDouble();
                scanner.nextLine(); // consume newline
                // Round to 2 decimals to avoid weird fractional cents if wanted:
                double rounded = Math.round(val * 100.0) / 100.0;
                return rounded;
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please enter a numeric amount (e.g., 1000 or 250.50).");
                scanner.nextLine(); // clear invalid token
            }
        }
    }
}

public class ATMSystem {
    public static void main(String[] args) {
        // Use US/standard locale for decimal point handling; change if necessary
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        System.out.println("Create a demo account (for this session).");
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = "Demo User";
        }
        System.out.print("Enter account number: ");
        String accNo = scanner.nextLine().trim();
        if (accNo.isEmpty()) {
            accNo = "00000000";
        }

        double initialBalance = 0.0;
        while (true) {
            try {
                System.out.print("Enter initial balance (or 0): ");
                initialBalance = scanner.nextDouble();
                scanner.nextLine(); // consume newline
                if (initialBalance < 0) {
                    System.out.println("Initial balance cannot be negative.");
                    continue;
                }
                break;
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please enter a numeric amount.");
                scanner.nextLine(); // clear invalid token
            }
        }

        BankAccount account = new BankAccount(name, accNo, initialBalance);
        ATM atm = new ATM(account, scanner);

        // start ATM UI loop
        atm.start();

        scanner.close();
    }
}