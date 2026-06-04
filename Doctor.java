package emergencyroom;

public class Doctor {
	private final int id;
	private final String name;
	private final String specialization;
	private boolean available;

	public Doctor(int id, String name, String specialization) {
		this.id = id;
		this.name = name;
		this.specialization = specialization;
		this.available = true; // true by default
	}

//getters
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

//Setters, needed for changeable values (isAvailable)
	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public String toString() {
		return "Doctor [id=" + id + ", name=" + name + ", specialization=" + specialization + ", " + "available="
				+ available + "]";
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
