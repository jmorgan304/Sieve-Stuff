import java.util.ArrayList;

/**
 * @author Josh Morgan
 * NOTE: The sieves for the partitions in a smaller range of values will be given the full prime factors list for
 * all of the numbers up to the upper bound, which is somewhat inefficient for memory usage
 * In addition, making number of cores - 1 copies of the factor list is also time consuming and memory inefficient
 *
 */
public class ParallelLongSieve extends LongSieve{
	private static Runtime system = Runtime.getRuntime();
	private int numberOfCores;
	private long factorLimit;
	private ArrayList<Long> primeFactors;
	
	/**
	 * @param upperBound The upperBound of the search space (exclusive)
	 * The call to super() will 
	 * 
	 */
	ParallelLongSieve(long upperBound){
		super(upperBound);
		this.factorLimit = this.getFactorLimit();
		this.primeFactors = super.getPrimeFactors();
		getSystemInfo();
	
	} // End of constructor
	
	ParallelLongSieve(long lowerBound, long upperBound, String inputFile){
		super(lowerBound, upperBound, inputFile);
		this.factorLimit = this.getFactorLimit();
		this.primeFactors = super.getPrimeFactors();
		getSystemInfo();
		
	} // End of constructor
	
	private void getSystemInfo() {
		this.numberOfCores = system.availableProcessors();

	} // End of getSystemInfo
	
	
} // End of ParallelLongSieve
