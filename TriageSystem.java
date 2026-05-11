import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Class representing a Patient in the emergency room.
 * Implements Comparable to define the priority logic for triage.
 */
class Patient implements Comparable<Patient> {
    private Long id;
    private String name;
    private String symptoms;
    private int severity; // Scale from 1 to 10
    private long arrivalTime; // Timestamp in milliseconds

    public Patient(Long id, String name, String symptoms, int severity) {
        this.id = id;
        this.name = name;
        this.symptoms = symptoms;
        this.severity = severity;
        this.arrivalTime = System.currentTimeMillis();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    
    /**
     * Calculates waiting time in minutes.
     * Note for presentation: To see the score evolve quickly during a demo,
     * you can divide by 1000 (seconds) instead of 60000 (minutes).
     */
    public int getWaitTimeMinutes() {
        long diffMillis = System.currentTimeMillis() - arrivalTime;
        return (int) (diffMillis / 60000); // 60000 ms = 1 minute
    }

    /**
     * Calculates the priority score.
     * Formula: (Severity * 100) + Wait time in minutes.
     */
    public int getScore() {
        return (severity * 100) + getWaitTimeMinutes();
    }

    /**
     * Defines the priority order for the PriorityQueue (Max-Heap).
     */
    @Override
    public int compareTo(Patient other) {
        int myScore = this.getScore();
        int otherScore = other.getScore();

        if (myScore != otherScore) {
            // Descending order: highest score goes first
            return Integer.compare(otherScore, myScore);
        } else {
            // Tie-breaker: FIFO - the first arrived (smaller timestamp) goes first
            return Long.compare(this.arrivalTime, other.arrivalTime);
        }
    }

    @Override
    public String toString() {
        return String.format("Patient #%d: %s | Severity: %d/10 | Wait Time: %d min | Priority Score: %d | Symptoms: %s",
                id, name, severity, getWaitTimeMinutes(), getScore(), symptoms);
    }
}

/**
 * Main class managing the triage system.
 */
public class TriageSystem {
    // Structure 1: PriorityQueue (Max-Heap) to manage the waiting room by dynamic priority
    private PriorityQueue<Patient> waitingQueue;
    
    // Structure 2: HashMap for quick patient search by ID (O(1) complexity)
    private HashMap<Long, Patient> patientMap;
    
    // Structure 3: ArrayList to keep the history of treated patients
    private ArrayList<Patient> treatmentHistory;
    
    private Long nextId = 1L;
    private Scanner scanner;

    public TriageSystem() {
        waitingQueue = new PriorityQueue<>();
        patientMap = new HashMap<>();
        treatmentHistory = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    /**
     * Method to admit a new patient into the system.
     */
    public void admitPatient() {
        System.out.println("\n--- ADMITTING A NEW PATIENT ---");
        System.out.print("Patient Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Symptoms: ");
        String symptoms = scanner.nextLine();
        
        System.out.print("Severity (1-10): ");
        int severity = 1;
        try {
            severity = Integer.parseInt(scanner.nextLine());
            if (severity < 1) severity = 1;
            if (severity > 10) severity = 10;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Default severity = 1.");
        }

        Patient newPatient = new Patient(nextId++, name, symptoms, severity);
        
        // Add to the priority queue and hash map
        waitingQueue.add(newPatient);
        patientMap.put(newPatient.getId(), newPatient);
        
        System.out.println("-> Patient successfully admitted! " + newPatient.getName() + " (ID: " + newPatient.getId() + ")");
    }

    /**
     * Refreshes priorities. 
     * In Java, a PriorityQueue does not automatically reorder if internal values change.
     * We must recreate the queue to force a new sort based on updated scores (passing time).
     */
    public void refreshPriorities() {
        PriorityQueue<Patient> updatedQueue = new PriorityQueue<>(waitingQueue);
        waitingQueue = updatedQueue;
    }

    /**
     * Displays the current waiting queue.
     */
    public void displayWaitingQueue() {
        refreshPriorities(); // Recalculate scores with current time
        
        System.out.println("\n--- CURRENT WAITING QUEUE ---");
        if (waitingQueue.isEmpty()) {
            System.out.println("The waiting queue is empty.");
            return;
        }
        
        // Use a copy to poll and display in order without emptying the real queue.
        PriorityQueue<Patient> displayCopy = new PriorityQueue<>(waitingQueue);
        int position = 1;
        while (!displayCopy.isEmpty()) {
            System.out.println(position + ". " + displayCopy.poll());
            position++;
        }
    }

    /**
     * Treats the next patient (the one with the highest priority).
     */
    public void treatNextPatient() {
        refreshPriorities(); // Ensure sorting is up-to-date before taking the patient
        
        Patient patientToTreat = waitingQueue.poll(); // Extracts the highest priority patient
        
        if (patientToTreat == null) {
            System.out.println("\n-> No patients in the waiting queue.");
            return;
        }
        
        // Remove from the map and add to the history
        patientMap.remove(patientToTreat.getId());
        treatmentHistory.add(patientToTreat);
        
        System.out.println("\n--- TREATMENT IN PROGRESS ---");
        System.out.println("The doctor is now seeing: " + patientToTreat.getName());
        System.out.println("Symptoms: " + patientToTreat.toString());
    }

    /**
     * Displays the history of patients who have received treatment.
     */
    public void displayHistory() {
        System.out.println("\n--- TREATMENT HISTORY ---");
        if (treatmentHistory.isEmpty()) {
            System.out.println("No patients have been treated yet.");
            return;
        }
        
        for (Patient p : treatmentHistory) {
            System.out.println("Treated: " + p.getName() + " (ID: " + p.getId() + ")");
        }
    }
    
    /**
     * Allows quick patient lookups using the HashMap (O(1)).
     */
    public void searchPatient() {
        System.out.print("\nEnter the ID of the patient to search for in the waiting room: ");
        try {
            Long searchId = Long.parseLong(scanner.nextLine());
            Patient p = patientMap.get(searchId);
            
            if (p != null) {
                // Displays patient info (time is dynamically calculated when calling toString)
                System.out.println("Patient found: " + p);
            } else {
                System.out.println("Patient not found. They might have already been treated or the ID does not exist.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    /**
     * Main loop for the console application.
     */
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=================================");
            System.out.println("    ER TRIAGE SYSTEM MANAGER    ");
            System.out.println("=================================");
            System.out.println("1. Admit a new patient");
            System.out.println("2. Display waiting queue");
            System.out.println("3. Treat the next patient");
            System.out.println("4. Display treatment history");
            System.out.println("5. Search for a patient (Quick search by ID)");
            System.out.println("0. Exit");
            System.out.print("-> Your choice: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1": admitPatient(); break;
                case "2": displayWaitingQueue(); break;
                case "3": treatNextPatient(); break;
                case "4": displayHistory(); break;
                case "5": searchPatient(); break;
                case "0":
                    running = false;
                    System.out.println("Shutting down the triage system...");
                    break;
                default:
                    System.out.println("Unrecognized choice, please try again.");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        TriageSystem system = new TriageSystem();
        system.start();
    }
}
