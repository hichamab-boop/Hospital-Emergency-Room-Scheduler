package emergencyroom;

public class testDoctor {

	public static void main(String[] args) {
		Doctor d1 = new Doctor(101, "Dr. Smith", "General");
		System.out.println(d1);
		d1.setAvailable(false);
		System.out.println(d1);
	}

}
