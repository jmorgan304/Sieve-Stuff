import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
	private ArrayList<Long> primeFactors;
	ArrayList<LongSieve> partialSieves;
	private long parallelExecutionTime;
	private String outputFolder;
	// Default to true for non-iterative use
	
	/**
	 * @param upperBound The upperBound of the search space (exclusive)
	 * 
	 */
	ParallelLongSieve(long upperBound) {
		super(upperBound);
		this.primeFactors = super.getPrimeFactors();
		getSystemInfo();
	} // End of constructor
	
	/**
	 * @param lowerBound The lowerBound of the search space (inclusive)
	 * @param upperBound The upperBound of the search space (exclusive)
	 * @param inputFile The optional input file to be used for the prime factors of the sieve
	 */
	ParallelLongSieve(long lowerBound, long upperBound, String inputFile) {
		super(lowerBound, upperBound, inputFile);
		this.primeFactors = super.getPrimeFactors();
		getSystemInfo();
	} // End of constructor
	
	/**
	 * @param lowerBound The lowerBound of the search space (inclusive)
	 * @param upperBound The upperBound of the search space (exclusive)
	 * @param inputFile The optional input file to be used for the prime factors of the sieve
	 * @param recombinePartials A flag to indicate not to recombine the partials and just write them to the output file directly,
	 * use if creating an iterative sieve
	 */
	ParallelLongSieve(long lowerBound, long upperBound, String inputFile, String outputFolder) {
		super(lowerBound, upperBound, inputFile);
		this.primeFactors = super.getPrimeFactors();
		getSystemInfo();
		this.outputFolder = outputFolder;
	} // End of constructor

	/**
	 * Gets the number of cores in the system and uses the number of cores - 1 for sieving
	 */
	private void getSystemInfo() {
		this.numberOfCores = system.availableProcessors() - 2;
		// Leave one core for the OS
		if(this.numberOfCores <= 0) {
			this.numberOfCores = 1;
		}
	} // End of getSystemInfo
	
	/**
	 * This method will create a FixedThreadPool with the number of cores on the machine - 1,
	 * partition the sieves, time the total runtime, and combine the partitioned sieves.
	 */
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
			if(this.outputFolder == null) {
				combinePartials(partials);
			}
			else {
				String outputFile = writePrimes(partials);
				// Will be null if there was an issue
				if(outputFile != null) {
					super.setOutputFile(outputFile);
					System.out.println("Written to: " + outputFile);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			EXEC.shutdown();
		}
		
	} // End of parallelSieve
	
	/**
	 * Partitions the numbers from lowerBound to upperBound into a number of smaller sieves equal to 
	 * the number of cores - 1 on the machine. They will be approximately equal in size but the final sieve 
	 * may be slightly larger in the case that the range of numbers isn't divisible by the number of cores - 1
	 */
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
				// Dependent on Longs being immutable, which the should be
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
	
	/**
	 * @param partials An ArrayList of the partial lists of primes to be combined
	 * This method will combine the partial lists and then set the superclass' primes to the combined set
	 */
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
		System.out.println("This machine has " + (this.numberOfCores + 2) + " core(s). Will run using " + this.numberOfCores + ".");
		System.out.println("Total parallel execution time: " + this.parallelExecutionTime + " milliseconds.");
		System.out.println();
		for(int i = 0; i < this.partialSieves.size(); i++) {
			System.out.println("Sieve " +  (i + 1) + "/" + this.partialSieves.size());
			this.partialSieves.get(i).printInfo();
			System.out.println();
		}
	} // End of printInfo
	
	public String writePrimes(ArrayList<ArrayList<Long>> primePartials) {
		if(this.outputFolder == null) {
			return super.writePrimes();
		}
		else {
			// Need to write the partials to the same file
			try {
				String fileName = "Primes [" + super.getLowerBound() + "," + super.getUpperBound() + ").txt";
				String path = this.outputFolder + "/" + fileName;
				File outputFile = new File(path);
				BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));
				
				for(ArrayList<Long> partial : primePartials) {
					for(long i : partial) {
						outputWriter.write(String.valueOf(i));
						outputWriter.newLine();
					}
				}
				outputWriter.close();
				return fileName;
			}
			catch(IOException e) {
				System.out.println("Could not write primes to the specified file");
				e.printStackTrace();
				return null;
			}
		}
	} // End of writePrimes
	
	public String getOutputFolder() {
		return this.outputFolder;
	}
	
} // End of ParallelLongSieve
