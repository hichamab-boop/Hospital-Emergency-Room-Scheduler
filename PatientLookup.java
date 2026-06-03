package emergencyroom;

import java.util.HashMap;

public class PatientLookup {
    private HashMap<Long, Patient> lookupTable;

    public PatientLookup() {
        this.lookupTable = new HashMap<>();
    }

    public void put(Patient p) {
        if (p != null) {
            lookupTable.put(p.getId(), p);
        }
    }

    public Patient get(long patientId) {
        return lookupTable.get(patientId);
    }

    public boolean contains(long patientId) {
        return lookupTable.containsKey(patientId);
    }

    public void remove(long patientId) {
        lookupTable.remove(patientId);
    }
}
