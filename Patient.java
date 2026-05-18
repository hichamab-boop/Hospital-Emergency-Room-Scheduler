package emergencyroom;

public class Patient {
private int id ; 
private String name ; 
private SeverityLevel severity;
private int arrivalTime ;
private Integer treatmentStartTime = null ;
private String doB; 
public Patient(int id, String name, String doB, SeverityLevel severity, int arrivalTime) {
	super();
	this.id = id;
	this.name = name;
	this.doB = doB;
	this.severity = severity;
	this.arrivalTime = arrivalTime;
	
}
public int getId() {
	return id;
}

public String getName() {
	return name;
}

public String getDoB() {
	return doB;
}

public SeverityLevel getSeverity() {
	return severity;
}
public Integer getTreatmentStartTime() {
	return treatmentStartTime;
}
public int getArrivalTime() {
	return arrivalTime;
} 
public int computePriority(int currentTime) {
	if (!isWaiting()) {
	    throw new IllegalStateException ("cannot compute priority for patient " + id +" -already treated ");
	}
	
	return (severity.getWeight() * 10) + getWaitTime(currentTime);	
}

public int getWaitTime(int currentTime) {
	if (isWaiting())
	 return currentTime - arrivalTime; 
	else 
		return treatmentStartTime - arrivalTime; 
}

public void updateSeverity(SeverityLevel s) {
	this.severity = s; 
	
}

public void markAsTreated(int currentTime) {
	if (treatmentStartTime != null) {
	  System.err.println("warning : patient "+ id +" has already been treated ."); 
	  return; 
	}
	this.treatmentStartTime = currentTime; 
}

public boolean isWaiting() {
	return treatmentStartTime== null; 
} 

@Override
public String toString() {
	return "Patient [id=" + id + ", name=" + name + ", severity=" + severity + ", arrivalTime="
			+ arrivalTime + ", treatmentStartTime=" + treatmentStartTime + ", doB=" + doB + "]";
}
@Override
public boolean equals(Object o) {
	
    // 1. If 'o' is the same object reference as 'this', they're equal.
	if (o==this ) {
		return true ; 
		}
    // 2. If 'o' is null or a different class, they're NOT equal.
	if (o == null || getClass()!= o.getClass()) {
		return false ; 
	}
    // 3. Otherwise, cast 'o' to Patient and compare IDs.
	Patient other = (Patient) o; 
    return this.id == other.id;
}
@Override
public int hashCode() {
    return Integer.hashCode(id);
}


public static void main(String[] args) {
    Patient alice = new Patient(1, "Alice", "2000-01-01", SeverityLevel.CRITICAL, 5);
    System.out.println(alice);                        // should show one dob, not two
    System.out.println("Waiting? " + alice.isWaiting());     // true
    System.out.println("Priority at t=10: " + alice.computePriority(10));  // should compute
    
    alice.markAsTreated(15);
    System.out.println("Waiting? " + alice.isWaiting());     // false
    System.out.println("Wait time after treatment: " + alice.getWaitTime(50)); // should be 10
    System.out.println("treatmentStartTime: " + alice.getTreatmentStartTime()); // should be 15, NOT corrupted
    
    try {
        alice.computePriority(50);  // should throw
        System.out.println("ERROR: should have thrown!");
    } catch (IllegalStateException e) {
        System.out.println("Correctly threw: " + e.getMessage());
    }
}}







