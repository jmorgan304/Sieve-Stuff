
public class ParallelLongSieve extends LongSieve{
	private static Runtime system = Runtime.getRuntime();
	private int numberOfCores;
	
	
	ParallelLongSieve(long upperBound){
		super(upperBound);
		getSystemInfo();
	
	} // End of constructor
	
	ParallelLongSieve(long lowerBound, long upperBound, String inputFile){
		super(lowerBound, upperBound, inputFile);
		getSystemInfo();
		
	} // End of constructor
	
	private void getSystemInfo() {
		this.numberOfCores = system.availableProcessors();

	} // End of getSystemInfo
	
	
} // End of ParallelLongSieve
