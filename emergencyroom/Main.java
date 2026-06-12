package emergencyroom;

import java.util.Scanner;

/**
 * Main.java — the console user interface ("the waiter").
 *
 * Stays THIN: it only reads input (with validation), calls an EmergencyRoom
 * method, and shows the result. No data-structure logic lives here.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final EmergencyRoom er = new EmergencyRoom();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": admitPatient();        break;
                case "2": addDoctor();           break;
                case "3": treatNextPatient();    break;
                case "4": updateSeverity();      break;
                case "5": lookupPatient();       break;
                case "6": er.printWaitingPatients(); break;
                case "7": er.printDoctors();     break;
                case "8": er.printHistory();     break;
                case "9": advanceTime();         break;
                case "0":
                    running = false;
                    System.out.println("Shutting down the triage system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from the menu.");
            }
        }

        scanner.close();
    }

    // ============================================================
    // MENU
    // ============================================================

    private static void printMenu() {
        System.out.println("\n===== ER TRIAGE SYSTEM (time: " + er.getCurrentTime() + ") =====");
        System.out.println("1. Admit a new patient");
        System.out.println("2. Add a doctor");
        System.out.println("3. Treat next patient");
        System.out.println("4. Update a patient's severity");
        System.out.println("5. Look up a patient by ID");
        System.out.println("6. View waiting patients");
        System.out.println("7. View doctors on duty");
        System.out.println("8. View patient history & stats");
        System.out.println("9. Advance time");
        System.out.println("0. Exit");
        System.out.print("-> Your choice: ");
    }

    // ============================================================
    // MENU ACTIONS — each is short: read, call, display.
    // ============================================================

    private static void admitPatient() {
        System.out.println("\n--- ADMIT NEW PATIENT ---");
        int id = readInt("Patient ID (integer): ");

        if (er.patientExists(id)) {
            System.out.println("A patient with ID " + id + " is already waiting. Aborting.");
            return;
        }

        String name = readName("Name: ");                          // letters only, no digits
        String doB = readDateOfBirth("Date of birth (dd-mm-yyyy): "); // numeric date only
        SeverityLevel severity = readSeverity();

        er.admitPatient(id, name, doB, severity);
        System.out.println("-> Admitted " + name + " (ID " + id + ", " + severity
                + ") at time " + er.getCurrentTime() + ".");
    }

    private static void addDoctor() {
        System.out.println("\n--- ADD DOCTOR ---");
        int id = readInt("Doctor ID (integer): ");
        String name = readName("Name: ");

        er.addDoctor(id, name); // always a general doctor
        System.out.println("-> Doctor " + name + " (ID " + id + ", General) added and on duty.");
    }

    private static void treatNextPatient() {
        System.out.println("\n--- TREAT NEXT PATIENT ---");
        System.out.println(er.treatNextPatient());
    }

    private static void updateSeverity() {
        System.out.println("\n--- UPDATE PATIENT SEVERITY ---");
        int id = readInt("Patient ID: ");
        SeverityLevel newSeverity = readSeverity();

        if (er.updatePatientSeverity(id, newSeverity)) {
            System.out.println("-> Patient " + id + " severity updated to " + newSeverity + ".");
        } else {
            System.out.println("No waiting patient found with ID " + id + ".");
        }
    }

    private static void lookupPatient() {
        System.out.println("\n--- LOOK UP PATIENT ---");
        int id = readInt("Patient ID: ");
        Patient p = er.findPatient(id);
        if (p != null) {
            System.out.println(p);
        } else {
            System.out.println("No waiting patient found with ID " + id + ".");
        }
    }

    private static void advanceTime() {
        System.out.println("\n--- ADVANCE TIME ---");
        int units = readInt("Advance by how many time units? ");
        er.advanceTime(units);
        System.out.println("-> Time is now " + er.getCurrentTime() + ".");
    }

    // ============================================================
    // INPUT HELPERS — all validation in one place. Each loops until
    // the user provides something valid, so the program never crashes.
    // ============================================================

    /** Reads an integer, re-asking until a valid one is entered. */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("That's not a valid integer. Please try again.");
            }
        }
    }

    /** Reads a non-empty line of text. */
    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /**
     * Reads a NAME: must not be empty and must not contain any digit.
     * (Letters, spaces and basic punctuation are fine.)
     */
    private static String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("Name cannot be empty. Please try again.");
            } else if (line.matches(".*\\d.*")) { // contains at least one digit
                System.out.println("Name cannot contain numbers. Please try again.");
            } else {
                return line;
            }
        }
    }

    /**
     * Reads a DATE OF BIRTH in strict dd-mm-yyyy format (digits and dashes only).
     * Rejects letters and wrong formats. Checks day 1-31 and month 1-12.
     */
    private static String readDateOfBirth(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.matches("\\d{2}-\\d{2}-\\d{4}")) {
                String[] parts = line.split("-");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                if (day >= 1 && day <= 31 && month >= 1 && month <= 12) {
                    return line;
                }
            }
            System.out.println("Invalid date. Use the format dd-mm-yyyy (e.g., 05-11-2000).");
        }
    }

    /** Shows the four severity options and returns the chosen SeverityLevel. */
    private static SeverityLevel readSeverity() {
        while (true) {
            System.out.println("Severity levels:");
            System.out.println("  1. MILD");
            System.out.println("  2. MODERATE");
            System.out.println("  3. SERIOUS");
            System.out.println("  4. CRITICAL");
            int choice = readInt("Choose severity (1-4): ");
            switch (choice) {
                case 1: return SeverityLevel.MILD;
                case 2: return SeverityLevel.MODERATE;
                case 3: return SeverityLevel.SERIOUS;
                case 4: return SeverityLevel.CRITICAL;
                default:
                    System.out.println("Please choose a number between 1 and 4.");
            }
        }
    }
}
