package emergencyroom;

import java.util.HashMap;

public class PatientLookup {
	private HashMap<Integer, Patient> lookupTable;

	public PatientLookup() {
		this.lookupTable = new HashMap<>();
	}

	public void put(Patient p) {
		if (p != null) {
			lookupTable.put(p.getId(), p);
		}
	}

	public Patient get(int patientId) {
		return lookupTable.get(patientId);
	}

	public boolean contains(int patientId) {
		return lookupTable.containsKey(patientId);
	}

	public void remove(int patientId) {
		lookupTable.remove(patientId);
	}
}
