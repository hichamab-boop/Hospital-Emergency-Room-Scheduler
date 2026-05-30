package structures;

import models.Patient;
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
            System.out.println("L'historique des urgences est vide.");
            return;
        }
        System.out.println("\n===== HISTORIQUE CHRONOLOGIQUE (BST IN-ORDER) =====");
        printInOrderRecursive(root);
        System.out.println("===================================================\n");
    }

    private void printInOrderRecursive(Node node) {
        if (node != null) {
            printInOrderRecursive(node.left);
            
            System.out.println("ID: " + node.patient.getPatientID() + 
                               " | Nom: " + node.patient.getName() + 
                               " | Arrivée: " + Instant.ofEpochMilli(node.patient.getArrivalTime()));
            
            printInOrderRecursive(node.right);
        }
    }

    public void recordTreatmentEnd(Patient p, long endTimeMs) {
        long waitTime = endTimeMs - p.getArrivalTime();
        totalWaitTimeMs += waitTime;
    }

    public double computeAverageWaitTimeMinutes() {
        if (totalPatientsCount == 0) return 0.0;
        
        double avgWaitMs = (double) totalWaitTimeMs / totalPatientsCount;
        return avgWaitMs / 60000.0;
    }
}
