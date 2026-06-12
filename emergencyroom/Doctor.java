package emergencyroom;

public class Doctor {
	private final int id;
	private final String name;
	private final String specialization; // always "General" - doctors are general doctors
	private boolean available;

	public Doctor(int id, String name) {
		this.id = id;
		this.name = name;
		this.specialization = "General"; // fixed: every doctor is a general doctor
		this.available = true;            // available by default
	}

	// getters
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSpec() {
		return specialization;
	}

	public boolean isAvailable() {
		return available;
	}

	// setter, needed for the only changeable value (availability)
	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public String toString() {
		return "Doctor [id=" + id + ", name=" + name + ", specialization=" + specialization
				+ ", available=" + available + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Doctor other = (Doctor) obj;
		return id == other.id;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(id);
	}
}
