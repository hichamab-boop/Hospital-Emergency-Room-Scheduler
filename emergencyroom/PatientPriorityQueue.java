package emergencyroom;

import java.util.ArrayList;

public class PatientPriorityQueue {
	private ArrayList<Patient> heap;

	public PatientPriorityQueue() {
		heap = new ArrayList<>();
	}

	// --- Public methods ---
	public void insert(Patient p, int currentTime) {
		heap.add(p);
		siftUp(heap.size() - 1, currentTime);
	}

	public Patient extractMax(int currentTime) {
		if (isEmpty())
			return null;
		Patient max = heap.get(0);
		swap(0, heap.size() - 1);
		heap.remove(heap.size() - 1);

		if (!isEmpty())
			siftDown(0, currentTime);
		return max;
	}

	public Patient peek() {
		if (isEmpty())
			return null;
		return heap.get(0);
	}

	public boolean isEmpty() {
		return heap.isEmpty();
	}

	public int size() {
		return heap.size();
	}

	public void updatePriority(int patientId, int currentTime) {
		for (int i = 0; i < heap.size(); i++) {
			if (heap.get(i).getId() == patientId) {
				siftUp(i, currentTime);
				siftDown(i, currentTime);
				break;
			}
		}
	}

	/**
	 * Rebuilds the whole heap so its order reflects priorities at the given time.
	 * Needed because priority grows with wait time: as time advances, the stored
	 * order can become stale. This is the standard O(n) build-heap.
	 */
	public void reheapify(int currentTime) {
		for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
			siftDown(i, currentTime);
		}
	}

	// --- Private helpers (the actual algorithms) ---
	private void swap(int i, int j) {
		Patient temp = heap.get(i);
		heap.set(i, heap.get(j));
		heap.set(j, temp);
	}

	private void siftUp(int index, int currentTime) {
		while (index > 0) {
			int parentIndex = (index - 1) / 2;
			int childPriority = heap.get(index).computePriority(currentTime);
			int parentPriority = heap.get(parentIndex).computePriority(currentTime);

			if (childPriority > parentPriority) {
				swap(index, parentIndex);
				index = parentIndex;
			} else
				break;
		}
	}

	private void siftDown(int index, int currentTime) {
		while (true) {
			int leftIndex = 2 * index + 1;
			int rightIndex = 2 * index + 2;
			int largest = index;

			if (leftIndex < heap.size()
					&& heap.get(leftIndex).computePriority(currentTime) > heap.get(largest).computePriority(currentTime)) {
				largest = leftIndex;
			}
			if (rightIndex < heap.size()
					&& heap.get(rightIndex).computePriority(currentTime) > heap.get(largest).computePriority(currentTime)) {
				largest = rightIndex;
			}
			if (largest != index) {
				swap(index, largest);
				index = largest;
			} else {
				break;
			}
		}
	}
}
