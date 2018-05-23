import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Josh Morgan
 * NOTE: The sieves for the partitions in a smaller range of values will be given the full prime factors list for
 * all of the numbers up to the upper bound, which is somewhat inefficient for memory usage
 * In addition, making number of cores - 1 copies of the factor list is also time consuming and memory inefficient
 * Copying approximately (factorLimit / ln(factorLimit)) terms for each prime factor array
 * 
 */
public class ParallelLongSieve extends LongSieve{
	private static Runtime system = Runtime.getRuntime();
	private int numberOfCores;
	private long factorLimit;
	private ArrayList<Long> primeFactors;
	ArrayList<LongSieve> partialSieves;
	private long parallelExecutionTime;
	
	
	/**
	 * @param args REMOVE TO SEPARATE DRIVER CLASS LATER
	 */
	public static void main(String[] args) {
		ParallelLongSieve ps1 = new ParallelLongSieve(10000000);
	}
	
	/**
	 * @param upperBound The upperBound of the search space (exclusive)
	 * The call to super() will 
	 * 
	 */
	ParallelLongSieve(long upperBound) {
		super(upperBound);
		this.factorLimit = this.getFactorLimit();
		this.primeFactors = super.getPrimeFactors();
		getSystemInfo();
	} // End of constructor
	
	ParallelLongSieve(long lowerBound, long upperBound, String inputFile) {
		super(lowerBound, upperBound, inputFile);
		this.factorLimit = this.getFactorLimit();
		this.primeFactors = super.getPrimeFactors();
		getSystemInfo();
	} // End of constructor
	
	private void getSystemInfo() {
		this.numberOfCores = system.availableProcessors() - 1;
		// Leave one core for the OS
		if(this.numberOfCores <= 0) {
			this.numberOfCores = 1;
		}
	} // End of getSystemInfo
	
	public void parallelSieve() {
		ExecutorService EXEC = Executors.newFixedThreadPool(this.numberOfCores);
		partitionSieves();
		try {
			ArrayList<ArrayList<Long>> partials = new ArrayList<ArrayList<Long>>();
			long start = System.currentTimeMillis();
			List<Future<ArrayList<Long>>> results = EXEC.invokeAll(this.partialSieves);
			for(Future<ArrayList<Long>> result : results) {
				partials.add(result.get());
			}
			long end = System.currentTimeMillis();
			this.parallelExecutionTime = end - start;
			combinePartials(partials);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			EXEC.shutdown();
		}
		
	} // End of parallelSieve
	
	private void partitionSieves() {
		this.partialSieves = new ArrayList<LongSieve>();
		long sieveSize = (this.getUpperBound() - this.getLowerBound()) / this.numberOfCores;
		long lowerBound = this.getLowerBound();
		long upperBound = this.getLowerBound();
		// Default them to the whole space, if a double or single core system
		for(int i = 0; i < this.numberOfCores - 1; i++) {
			// All the sieves except the last one at the regular size
			lowerBound = this.getLowerBound() + sieveSize * i;
			upperBound = lowerBound + sieveSize;
			LongSieve partial = new LongSieve(lowerBound, upperBound, null);
			ArrayList<Long> copiedPrimeFactors = new ArrayList<Long>();
			// Need copies for thread safe access
			for(Long factor : this.primeFactors) {
				copiedPrimeFactors.add(factor);
			}
			partial.setPrimeFactors(copiedPrimeFactors);
			this.partialSieves.add(partial);
		}
		
		LongSieve finalSieve = new LongSieve(upperBound, this.getUpperBound(), null);
		// Making the last sieve the upperBound of the previous sieve and the real upper bound to deal with remainders
		ArrayList<Long> copiedPrimeFactors = new ArrayList<Long>();
		for(Long factor : this.primeFactors) {
			copiedPrimeFactors.add(factor);
		}
		finalSieve.setPrimeFactors(copiedPrimeFactors);
		this.partialSieves.add(finalSieve);
		
	} // End of partitionSieves
	
	private void combinePartials(ArrayList<ArrayList<Long>> partials) {
		ArrayList<Long> primes = new ArrayList<Long>();
		for(ArrayList<Long> partial : partials) {
			for(Long prime : partial) {
				primes.add(prime);
			}
		}
		this.setPrimes(primes);
	}
	
	/* (non-Javadoc)
	 * @see LongSieve#printInfo()
	 */
	public void printInfo() {
		System.out.println("This machine has " + this.numberOfCores + " core(s).");
		System.out.println("Total parallel execution time: " + this.parallelExecutionTime + " milliseconds.");
		for(LongSieve ls : this.partialSieves) {
			ls.printInfo();
		}
	} // End of printInfo
	
} // End of ParallelLongSieve
