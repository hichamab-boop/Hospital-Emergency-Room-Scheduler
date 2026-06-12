package emergencyroom;

public class DoctorQueue {
	private Node front;
	private Node rear;
	private int size;

	public DoctorQueue() {
		this.front = null;
		this.rear = null;
		this.size = 0;
	}

	public boolean isEmpty() {
		if (front == null) {
			return true;
		} else {
			return false;
		}
	}

	public int size() {
		return size;
	}

	public Doctor peek() {
		if (isEmpty()) {
			return null;
		} else
			return front.doctor;
	}

	public void enqueue(Doctor doctor) {
		Node newNode = new Node(doctor);
		if (isEmpty()) {
			front = newNode;
			rear = newNode;
		} else {
			rear.next = newNode;
			rear = newNode;
		}
		size++;
	}

	public Doctor dequeue() {
		if (isEmpty()) {
			return null;
		}
		Doctor removed = front.doctor;
		front = front.next;

		if (front == null) {
			rear = null;
		}

		size--;
		return removed;
	}

	private static class Node {
		private Doctor doctor;
		private Node next;

		Node(Doctor doctor) {
			this.doctor = doctor;
			this.next = null;
		}
	}

	@Override
	public String toString() {
		if (isEmpty()) {
			return "front -> null";
		}

		String result = "front -> ";
		Node current = front;

		while (current != null) {
			result += current.doctor.getName() + " -> ";
			current = current.next;
		}

		result += "null";
		return result;
	}
}
