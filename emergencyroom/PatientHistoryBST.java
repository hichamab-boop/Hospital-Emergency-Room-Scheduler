package emergencyroom;

public class PatientHistoryBST {

	private static class Node {
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
	private long totalWaitTime = 0;

	public PatientHistoryBST() {
		this.root = null;
	}

	public void insert(Patient p) {
		if (p == null)
			return;
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
		System.out.println("================================================");
	}

	private void printInOrderRecursive(Node node) {
		if (node != null) {
			printInOrderRecursive(node.left);

			System.out.println("ID: " + node.patient.getId() +
					" | Name: " + node.patient.getName() +
					" | Arrival Unit: " + node.patient.getArrivalTime());

			printInOrderRecursive(node.right);
		}
	}

	public void recordTreatmentEnd(Patient p, int endTime) {
		int waitTime = endTime - p.getArrivalTime();
		totalWaitTime += waitTime;
	}

	public double computeAverageWaitTimeMinutes() {
		if (totalPatientsCount == 0)
			return 0.0;
		return (double) totalWaitTime / totalPatientsCount;
	}
}
