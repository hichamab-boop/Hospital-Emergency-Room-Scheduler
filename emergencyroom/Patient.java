package emergencyroom;

public class Patient {
	private int id;
	private String name;
	private SeverityLevel severity;
	private int arrivalTime;
	private Integer treatmentStartTime = null;
	private String doB;

	public Patient(int id, String name, String doB, SeverityLevel severity, int arrivalTime) {
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
			throw new IllegalStateException("cannot compute priority for patient " + id + " - already treated ");
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
			System.err.println("warning : patient " + id + " has already been treated .");
			return;
		}
		this.treatmentStartTime = currentTime;
	}

	public boolean isWaiting() {
		return treatmentStartTime == null;
	}

	@Override
	public String toString() {
		return "Patient [id=" + id + ", name=" + name + ", severity=" + severity + ", arrivalTime="
				+ arrivalTime + ", treatmentStartTime=" + treatmentStartTime + ", doB=" + doB + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Patient other = (Patient) o;
		return this.id == other.id;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(id);
	}
}
