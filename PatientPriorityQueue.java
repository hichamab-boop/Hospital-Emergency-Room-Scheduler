package emergencyroom;
import java.util.ArrayList;
public class PatientPriorityQueue {
	private ArrayList<Patient> heap;
	 public PatientPriorityQueue() {
	        heap = new ArrayList<>();
	    }
	// --- Public methods ---
	    public void insert(Patient p, int currentTime){
	    	heap.add(p); 
	    	siftUp(heap.size() -1 ,currentTime); 
	    }
	    public Patient extractMax(int currentTime){ 
	    	if (isEmpty()) 
	    		return null ; 
	    	 Patient  max = heap.get(0) ; 
	    	swap (0,heap.size() -1); 
	    	heap.remove(heap.size()-1); 
	    	
	    	if (!isEmpty())
	    		siftDown(0 , currentTime); 
	    	return max; 
	    }
	    
	    public Patient peek(){
	    	if (isEmpty())
	    		return null ; 
	    	return heap.get(0) ; }
	    
	    public boolean isEmpty(){
	    	return heap.isEmpty(); }
	    
	    public int size(){
	    	return heap.size(); }
	    
	    public void updatePriority(int patientId, int currentTime) {
	    	
			for (int i = 0;  i<heap.size() ; i++) {
				if(heap.get(i).getId()==patientId) {
	              siftUp(i, currentTime); 
	              siftDown(i, currentTime); 
	              break ; 
				}
			}
	    }

	    // --- Private helpers (the actual algorithms) ---
	    private void swap(int i, int j){
	    	 Patient temp ; 
	    	 temp = heap.get(i); 
	    	 heap.set(i,heap.get(j)); 
	    	 heap.set(j, temp); 
	    	 }
	    private void siftUp(int index, int currentTime)   { 
	    	while (index > 0) {
	    		int parentIndex = (index-1)/2;
	    	    int childPriority = heap.get(index).computePriority(currentTime); 
	    	    int parentPriority= heap.get(parentIndex).computePriority(currentTime);
	    	    
	    	   if (childPriority > parentPriority) {
	    		   swap (index,parentIndex);
	    		   index=parentIndex; }
	    	   else 
	    		   break ; 
	    	   
	    	}
	    }
	    private void siftDown(int index, int currentTime){
	    	while (true) {
	    		int leftIndex = 2 * index + 1 ; 
	    		int rightIndex = 2 * index +2 ; 
	    		int largest = index ; 
	    		
	    		if (leftIndex < heap.size () && heap.get(leftIndex).computePriority(currentTime)>heap.get(largest).computePriority(currentTime)) {
	    			 largest = leftIndex ; 	 
	    		}
	    		if (rightIndex  < heap.size () && heap.get(rightIndex).computePriority(currentTime)>heap.get(largest).computePriority(currentTime)) {
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
	    public static void main(String[] args) {
	        PatientPriorityQueue pq = new PatientPriorityQueue();
	        
	        Patient a = new Patient(1, "Alice", "2000-01-01", SeverityLevel.CRITICAL, 0);
	        Patient b = new Patient(2, "Bob", "1995-05-15", SeverityLevel.MILD, 2);
	        Patient c = new Patient(3, "Carol", "1988-11-30", SeverityLevel.SERIOUS, 1);
	        
	        pq.insert(a, 5);
	        pq.insert(b, 5);
	        pq.insert(c, 5);
	        
	        System.out.println("Size: " + pq.size());            // 3
	        System.out.println("Peek: " + pq.peek());            // Alice (CRITICAL)
	        System.out.println("Extract: " + pq.extractMax(5));   // Alice
	        System.out.println("Extract: " + pq.extractMax(5));   // Carol (SERIOUS)
	        System.out.println("Extract: " + pq.extractMax(5));   // Bob (MILD)
	        System.out.println("Empty: " + pq.isEmpty());         // true
	     // Test updatePriority
	        PatientPriorityQueue pq2 = new PatientPriorityQueue();

	        Patient d = new Patient(4, "Dan", "1990-01-01", SeverityLevel.MILD, 0);
	        Patient e = new Patient(5, "Eve", "1992-06-15", SeverityLevel.SERIOUS, 1);

	        pq2.insert(d, 5);
	        pq2.insert(e, 5);

	        System.out.println("Before update: " + pq2.peek());  // Eve (SERIOUS)

	        d.updateSeverity(SeverityLevel.CRITICAL);
	        pq2.updatePriority(4, 5);

	        System.out.println("After update: " + pq2.peek());   // Dan (now CRITICAL)
	    }
	    

}
