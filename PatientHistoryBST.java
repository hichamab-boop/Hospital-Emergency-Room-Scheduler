package structures;

import emergencyroom.Patient;
import java.time.Instant;

public class PatientHistoryBST {
    
    private class Node {
        Patient patient;
        Node left;
        Node right;

        Node(Patient patient) {
            this.patient = patient;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;
    private int totalPatientsCount = 0;
    private long totalWaitTimeMs = 0;

    public PatientHistoryBST() {
        this.root = null;
    }

    public void insert(Patient p) {
        if (p == null) return;
        root = insertRecursive(root, p);
        totalPatientsCount++;
    }

    private Node insertRecursive(Node current, Patient p) {
        if (current == null) {
            return new Node(p);
        }

        if (p.getArrivalTime() < current.patient.getArrivalTime()) {
            current.left = insertRecursive(current.left, p);
        } else {
            current.right = insertRecursive(current.right, p);
        }

        return current;
    }

    public void printChronologicalHistory() {
        if (root == null) {
            System.out.println("Emergency history is empty.");
            return;
        }
        System.out.println("\n===== CHRONOLOGICAL HISTORY (BST IN-ORDER) =====");
        printInOrderRecursive(root);
        System.out.println("================================================\n");
    }

    private void printInOrderRecursive(Node node) {
        if (node != null) {
            printInOrderRecursive(node.left);
            
            System.out.println("ID: " + node.patient.getPatientID() + 
                               " | Name: " + node.patient.getName() + 
                               " | Arrival: " + Instant.ofEpochMilli(node.patient.getArrivalTime() * 60000L)); // Converts minutes to ms for display
            
            printInOrderRecursive(node.right);
        }
    }

    public void recordTreatmentEnd(Patient p, long endTimeMs) {
        long waitTime = endTimeMs - (p.getArrivalTime() * 60000L); // Convert arrival minutes to ms to compute wait difference
        totalWaitTimeMs += waitTime;
    }

    public double computeAverageWaitTimeMinutes() {
        if (totalPatientsCount == 0) return 0.0;
        
        double avgWaitMs = (double) totalWaitTimeMs / totalPatientsCount;
        return avgWaitMs / 60000.0;
    }
}
