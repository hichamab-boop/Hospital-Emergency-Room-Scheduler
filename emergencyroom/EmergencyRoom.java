package emergencyroom;

/**
 * EmergencyRoom.java — the orchestrator ("the kitchen").
 *
 * This class owns ONE instance of each custom data structure and coordinates
 * them. It is the only place that knows about all four structures at once.
 * Main (the UI) talks ONLY to this class, never to the structures directly.
 *
 * Structures used (all built from scratch by the team):
 *   - PatientPriorityQueue (max-heap)  -> who to treat next, by priority
 *   - DoctorQueue          (FIFO queue)-> which doctor is next, fairly
 *   - PatientHistoryBST    (BST)       -> treated-patient history + avg wait
 *   - PatientLookup        (hashmap)   -> O(1) lookup of waiting patients by ID
 *
 * TIME MODEL: time is an abstract integer "tick" counter that this class owns.
 * A patient's arrivalTime is the tick at which they were admitted. The user can
 * advance time from the menu to watch waiting patients' priorities grow.
 *
 * NOTE: this requires ONE new method on PatientPriorityQueue: reheapify(int).
 * See the explanation that accompanies this file.
 */
public class EmergencyRoom {

    private final PatientPriorityQueue waitingPatients; // the max-heap
    private final DoctorQueue availableDoctors;         // the FIFO queue
    private final PatientHistoryBST history;            // the BST
    private final PatientLookup lookup;                 // the hashmap wrapper
    private int currentTime;                            // the simulation clock

    public EmergencyRoom() {
        this.waitingPatients = new PatientPriorityQueue();
        this.availableDoctors = new DoctorQueue();
        this.history = new PatientHistoryBST();
        this.lookup = new PatientLookup();
        this.currentTime = 0;
    }

    // ---------- time ----------

    public int getCurrentTime() {
        return currentTime;
    }

    public void advanceTime(int units) {
        if (units > 0) {
            currentTime += units;
        }
    }

    // ---------- patients ----------

    /** True if a WAITING patient with this id is already in the system. */
    public boolean patientExists(int id) {
        return lookup.contains(id);
    }

    /**
     * Admits a patient: records their arrival time, then puts them into the
     * heap (for prioritising) and the lookup table (for fast search).
     */
    public void admitPatient(int id, String name, String doB, SeverityLevel severity) {
        Patient p = new Patient(id, name, doB, severity, currentTime);
        waitingPatients.insert(p, currentTime);
        lookup.put(p);
    }

    /**
     * Treats the highest-priority waiting patient using the next available
     * doctor. Returns a message describing what happened (or why it couldn't).
     */
    public String treatNextPatient() {
        if (waitingPatients.isEmpty()) {
            return "No patients are waiting.";
        }
        if (availableDoctors.isEmpty()) {
            return "No doctors are available. Add a doctor first.";
        }

        // Priorities depend on the current time, and the heap can go "stale"
        // as time passes, so we rebuild it for the current time before pulling
        // the true highest-priority patient.
        waitingPatients.reheapify(currentTime);

        Patient p = waitingPatients.extractMax(currentTime); // out of the heap
        Doctor d = availableDoctors.dequeue();               // next free doctor

        lookup.remove(p.getId());        // no longer a waiting patient
        p.markAsTreated(currentTime);    // stamp treatment time
        history.insert(p);               // record in the history BST
        history.recordTreatmentEnd(p, currentTime); // accumulate wait-time stats

        // Doctor finishes intake and rotates back to the rear of the queue,
        // ready to be assigned again (fair FIFO rotation).
        d.setAvailable(true);
        availableDoctors.enqueue(d);

        return "Treating " + p.getName() + " (ID " + p.getId() + ", " + p.getSeverity()
                + ") with Dr. " + d.getName() + " (" + d.getSpec() + ") at time " + currentTime + ".";
    }

    /**
     * Changes a waiting patient's severity and re-positions them in the heap.
     * Returns false if no waiting patient has that id.
     */
    public boolean updatePatientSeverity(int patientId, SeverityLevel newSeverity) {
        Patient p = lookup.get(patientId);
        if (p == null || !p.isWaiting()) {
            return false;
        }
        p.updateSeverity(newSeverity);
        waitingPatients.updatePriority(patientId, currentTime);
        return true;
    }

    /** Fast O(1) lookup of a waiting patient by id (null if not found). */
    public Patient findPatient(int id) {
        return lookup.get(id);
    }

    // ---------- doctors ----------

    public void addDoctor(int id, String name) {
        Doctor d = new Doctor(id, name); // always a general doctor
        availableDoctors.enqueue(d);
    }

    // ---------- displays ----------
    // These print directly because the history BST already prints directly.
    // Keeping all the display logic here means Main just routes to it.

    /**
     * Shows the waiting patients from highest to lowest priority.
     * We empty the heap into a temporary array (which gives sorted order once
     * the heap is correct for the current time), print it, then put everyone
     * back. This uses ONLY the heap's public operations.
     */
    public void printWaitingPatients() {
        System.out.println("\n--- WAITING PATIENTS (highest priority first) ---");
        if (waitingPatients.isEmpty()) {
            System.out.println("No patients are currently waiting.");
            return;
        }

        waitingPatients.reheapify(currentTime); // correct order for "now"

        int n = waitingPatients.size();
        Patient[] order = new Patient[n];
        for (int i = 0; i < n; i++) {
            order[i] = waitingPatients.extractMax(currentTime);
        }

        for (int i = 0; i < n; i++) {
            Patient p = order[i];
            System.out.println((i + 1) + ". " + p.getName() + " (ID " + p.getId() + ") | "
                    + p.getSeverity() + " | waited " + p.getWaitTime(currentTime)
                    + " | priority " + p.computePriority(currentTime));
        }

        // put everyone back so the heap is unchanged after display
        for (int i = 0; i < n; i++) {
            waitingPatients.insert(order[i], currentTime);
        }
    }

    public void printDoctors() {
        System.out.println("\n--- DOCTORS ON DUTY (front = next to be assigned) ---");
        if (availableDoctors.isEmpty()) {
            System.out.println("No doctors are currently on duty.");
        } else {
            System.out.println(availableDoctors.toString());
        }
    }

    public void printHistory() {
        history.printChronologicalHistory();
        System.out.printf("Average wait time: %.2f time units%n",
                history.computeAverageWaitTimeMinutes());
    }
}
