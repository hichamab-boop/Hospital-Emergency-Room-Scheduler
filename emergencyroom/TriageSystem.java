package emergencyroom;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Master controller for the ER Triage System.
 * Coordinates patients, severity levels, doctor queues, fast lookups, and chronological histories.
 */
public class TriageSystem {
    private final ArrayList<Patient> waitingRoom;
    private final PatientLookup patientLookup;
    private final PatientHistoryBST historyBST;
    private DoctorQueue doctorQueue;
    private final Scanner scanner;
    private int currentTime; // Simulated time counter

    public TriageSystem() {
        this.waitingRoom = new ArrayList<>();
        this.patientLookup = new PatientLookup();
        this.historyBST = new PatientHistoryBST();
        this.scanner = new Scanner(System.in);
        this.currentTime = 0; // Starts at time unit 0

        // Use Java Reflection to instantiate DoctorQueue to respect the private constructor
        try {
            Constructor<DoctorQueue> constructor = DoctorQueue.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            this.doctorQueue = constructor.newInstance();
        } catch (Exception e) {
            System.err.println("Fatal Error: Could not instantiate DoctorQueue using reflection.");
            e.printStackTrace();
        }
    }

    /**
     * Menu option to admit a new patient.
     */
    public void admitPatient() {
        System.out.println("\n--- ADMIT NEW PATIENT ---");
        System.out.print("Patient ID (Integer): ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
            if (patientLookup.contains(id)) {
                System.out.println("Error: A patient with ID " + id + " is already in the system.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Patient admission aborted.");
            return;
        }

        System.out.print("Patient Name: ");
        String name = scanner.nextLine();

        System.out.print("Date of Birth (YYYY-MM-DD): ");
        String dob = scanner.nextLine();

        System.out.println("Severity Levels:");
        System.out.println("1. MILD (Priority weight = 1)");
        System.out.println("2. MODERATE (Priority weight = 3)");
        System.out.println("3. SERIOUS (Priority weight = 6)");
        System.out.println("4. CRITICAL (Priority weight = 10)");
        System.out.print("Select Severity Level (1-4): ");
        SeverityLevel severity;
        String choice = scanner.nextLine();
        switch (choice) {
            case "1": severity = SeverityLevel.MILD; break;
            case "2": severity = SeverityLevel.MODERATE; break;
            case "3": severity = SeverityLevel.SERIOUS; break;
            case "4": severity = SeverityLevel.CRITICAL; break;
            default:
                System.out.println("Invalid choice. Defaulting to MILD.");
                severity = SeverityLevel.MILD;
        }

        // Create the patient with the current simulation time as their arrival time
        Patient patient = new Patient(id, name, dob, severity, currentTime);
        
        waitingRoom.add(patient);
        patientLookup.put(patient);

        System.out.println("-> Patient successfully admitted: " + name + " (ID: " + id + ") at time unit " + currentTime);
    }

    /**
     * Menu option to add a doctor on duty.
     */
    public void addDoctor() {
        System.out.println("\n--- ADD DOCTOR TO QUEUE ---");
        System.out.print("Doctor ID (Integer): ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Doctor addition aborted.");
            return;
        }

        System.out.print("Doctor Name: ");
        String name = scanner.nextLine();

        System.out.print("Specialization: ");
        String spec = scanner.nextLine();

        Doctor doctor = new Doctor(id, name, spec);
        doctorQueue.enqueue(doctor);

        System.out.println("-> Doctor " + name + " is now on duty and added to the rotation queue.");
    }

    /**
     * Menu option to display active patients in the waiting room sorted by priority.
     */
    public void displayWaitingRoom() {
        System.out.println("\n--- WAITING ROOM (Sorted by Dynamic Priority) ---");
        if (waitingRoom.isEmpty()) {
            System.out.println("No patients are currently waiting.");
            return;
        }

        // Sort patients by priority descending at the current time
        // Priority formula: (severity.weight * 10) + waitTime
        ArrayList<Patient> sortedPatients = new ArrayList<>(waitingRoom);
        Collections.sort(sortedPatients, (p1, p2) -> {
            int priority1 = p1.computePriority(currentTime);
            int priority2 = p2.computePriority(currentTime);
            if (priority1 != priority2) {
                return Integer.compare(priority2, priority1); // Descending order
            }
            // Tie-breaker: FIFO (Earlier arrival first)
            return Integer.compare(p1.getArrivalTime(), p2.getArrivalTime());
        });

        System.out.printf("%-5s | %-15s | %-10s | %-8s | %-12s | %-8s%n", 
                "ID", "Name", "Severity", "Arrived", "Wait Units", "Priority");
        System.out.println("------------------------------------------------------------------");
        for (Patient p : sortedPatients) {
            System.out.printf("%-5d | %-15s | %-10s | %-8d | %-12d | %-8d%n",
                    p.getId(), 
                    p.getName(), 
                    p.getSeverity(), 
                    p.getArrivalTime(), 
                    p.getWaitTime(currentTime), 
                    p.computePriority(currentTime));
        }
    }

    /**
     * Menu option to display doctors in the queue.
     */
    public void displayDoctors() {
        System.out.println("\n--- DOCTORS ON DUTY QUEUE ---");
        if (doctorQueue.isEmpty()) {
            System.out.println("No doctors are currently on duty.");
        } else {
            System.out.println(doctorQueue.toString());
        }
    }

    /**
     * Menu option to treat the next highest priority patient.
     */
    public void treatNextPatient() {
        System.out.println("\n--- TREAT NEXT PATIENT ---");
        if (waitingRoom.isEmpty()) {
            System.out.println("No patients in the waiting room.");
            return;
        }

        if (doctorQueue.isEmpty()) {
            System.out.println("Warning: Cannot treat patient. No doctors on duty! Add doctors first.");
            return;
        }

        // Find the patient with the highest priority score at the current time
        Patient highestPriorityPatient = waitingRoom.get(0);
        int maxPriority = highestPriorityPatient.computePriority(currentTime);

        for (Patient p : waitingRoom) {
            int priority = p.computePriority(currentTime);
            if (priority > maxPriority) {
                maxPriority = priority;
                highestPriorityPatient = p;
            } else if (priority == maxPriority) {
                // Tie-breaker: FIFO (Earlier arrival first)
                if (p.getArrivalTime() < highestPriorityPatient.getArrivalTime()) {
                    highestPriorityPatient = p;
                }
            }
        }

        // Remove from waiting list and lookup table
        waitingRoom.remove(highestPriorityPatient);
        patientLookup.remove(highestPriorityPatient.getId());

        // Assign the next available doctor (FIFO rotation)
        Doctor doctor = doctorQueue.dequeue();
        doctor.setAvailable(false);

        // Mark patient as treated and insert into BST history
        highestPriorityPatient.markAsTreated(currentTime);
        historyBST.insert(highestPriorityPatient);
        historyBST.recordTreatmentEnd(highestPriorityPatient, currentTime);

        System.out.println("Success! Treatment started at time unit: " + currentTime);
        System.out.println("  Patient: " + highestPriorityPatient.getName() + " (ID: " + highestPriorityPatient.getId() + ")");
        System.out.println("  Assigned Doctor: " + doctor.getName() + " (Specialization: " + doctor.getSpec() + ")");

        // Rotate the doctor back to the rear of the queue to keep them on duty
        doctor.setAvailable(true);
        doctorQueue.enqueue(doctor);
        System.out.println("  Doctor " + doctor.getName() + " has completed the intake and rotated to the rear of the queue.");
    }

    /**
     * Menu option to search for a patient by ID.
     */
    public void searchPatient() {
        System.out.println("\n--- SEARCH PATIENT BY ID ---");
        System.out.print("Enter Patient ID to search: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Patient p = patientLookup.get(id);
            if (p != null) {
                System.out.println("\nPatient Found:");
                System.out.println("  ID: " + p.getId());
                System.out.println("  Name: " + p.getName());
                System.out.println("  DOB: " + p.getDoB());
                System.out.println("  Severity: " + p.getSeverity());
                System.out.println("  Arrival Time Unit: " + p.getArrivalTime());
                if (p.isWaiting()) {
                    System.out.println("  Status: Waiting");
                    System.out.println("  Current Wait Units: " + p.getWaitTime(currentTime));
                    System.out.println("  Current Priority Score: " + p.computePriority(currentTime));
                } else {
                    System.out.println("  Status: Treated");
                    System.out.println("  Treatment Start Time Unit: " + p.getTreatmentStartTime());
                    System.out.println("  Total Wait Units: " + p.getWaitTime(currentTime));
                }
            } else {
                System.out.println("Patient not found in the active lookup system.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    /**
     * Menu option to display treated patients history.
     */
    public void displayHistory() {
        historyBST.printChronologicalHistory();
        System.out.printf("Average Patient Wait Time: %.2f units%n", historyBST.computeAverageWaitTimeMinutes());
    }

    /**
     * Menu option to advance simulated time.
     */
    public void advanceTime() {
        System.out.println("\n--- ADVANCE SIMULATED TIME ---");
        System.out.printf("Current simulated time is: %d%n", currentTime);
        System.out.print("Enter number of time units to advance: ");
        try {
            int units = Integer.parseInt(scanner.nextLine());
            if (units < 0) {
                System.out.println("Time cannot move backward!");
                return;
            }
            currentTime += units;
            System.out.printf("Simulated time advanced by %d units. New current time: %d%n", units, currentTime);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format.");
        }
    }

    /**
     * Main simulation loop.
     */
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n==============================================");
            System.out.printf("  INTEGRATED ER TRIAGE MANAGER (Time Unit: %d)%n", currentTime);
            System.out.println("==============================================");
            System.out.println("1. Admit a new patient");
            System.out.println("2. Add a doctor on duty");
            System.out.println("3. Display waiting room (sorted by priority)");
            System.out.println("4. Display doctor queue (duty rotation)");
            System.out.println("5. Treat the next highest-priority patient");
            System.out.println("6. Display treatment history & average wait time");
            System.out.println("7. Search for a patient (O(1) Lookup)");
            System.out.println("8. Advance simulated time");
            System.out.println("0. Exit");
            System.out.print("-> Your choice: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": admitPatient(); break;
                case "2": addDoctor(); break;
                case "3": displayWaitingRoom(); break;
                case "4": displayDoctors(); break;
                case "5": treatNextPatient(); break;
                case "6": displayHistory(); break;
                case "7": searchPatient(); break;
                case "8": advanceTime(); break;
                case "0":
                    running = false;
                    System.out.println("Shutting down the triage system. Stay safe!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        TriageSystem system = new TriageSystem();
        system.start();
    }
}
