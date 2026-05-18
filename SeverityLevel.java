package emergencyroom;

public enum SeverityLevel {
	   MILD(1),
	    MODERATE(3),
	    SERIOUS(6),
	    CRITICAL(10);
	;

	private final  int weight;

	SeverityLevel(int weight) {
		  this.weight = weight;	
		}

	int getWeight() {
		
		return weight;
	}

}
