import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Callable;
/**
 * @author Josh Morgan
 * A class representing a prime number sieve.
 * The optimizations for this algorithm are:
 *     Using only the primes less than or equal to the square root of the upper bound to factor numbers.
 *     Searching only the odd numbers up to the upper bound (excluding 2)
 * Using integers, this can get all the primes below 2,147,483,647.
 */
public class Sieve implements Callable<ArrayList<Integer>>{
	private int lowerBound; 
	private int upperBound;
	private int factorLimit;
	private ArrayList<Integer> primes;
	private ArrayList<Integer> primeFactors;
	private String inputFile;
	private String outputFile;
	private long executionTime;
	
	/**
	 * This is the standard sieve that will start from 0 and go to the upper bound (exclusive).
	 * @param upperBound The upper bound of the search space (exclusive)
	 */
	Sieve(int upperBound){
		this.lowerBound = 0;
		this.upperBound = upperBound;
		this.factorLimit = (int) Math.ceil(Math.sqrt(upperBound));
	} // End of Constructor
	
	/**
	 * This version will search for primes between lower bound (inclusive) and upper bound (exclusive).
	 * It also has an optional file input for a file with primes below the factor limit. 
	 * @param lowerBound The lower bound of the search space (inclusive)
	 * @param upperBound The upper bound of the search space (exclusive)
	 * @param inputFile The path of a file containing one prime per line
	 */
	Sieve(int lowerBound, int upperBound, String inputFile){
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.factorLimit = (int) Math.ceil(Math.sqrt(upperBound));
		this.inputFile =  inputFile;
	} // End of Constructor
	
	/**
	 * This method implements a prime number sieve which uses known primes 
	 * below the square root of the specified upper bound to factor new potential ones.
	 */
	private void generatePrimes(){
		this.primes = new ArrayList<Integer>();
		if(this.upperBound <= 2 || this.lowerBound >= this.upperBound){
			return;
		}
		if(this.lowerBound <= 2){
			this.primes.add(2);
			this.primeFactors = new ArrayList<Integer>();
			this.primeFactors.add(2);
			for(int i = 3; i < this.upperBound; i+=2){
				boolean isPrime = true;
				// Search through all the primes until one greater than the factor limit has been found
				for(int prime : this.primeFactors) {
					if(i % prime == 0 && i != prime) {
						isPrime = false;
						break;
						// Stop the loop at the first complete factor
					}
				}
				if(isPrime){
					this.primes.add(i);
					if(i <= this.factorLimit) {
						this.primeFactors.add(i);
						// It will be needed to factor future numbers
					}
				}
			}
		}
		else{
			// All primes below the factor limit are needed to factor numbers between lower and upper bound
			this.primeFactors = getRequiredPrimes();
			
			int newLowerBound = this.lowerBound;
			if(newLowerBound % 2 == 0) {
				newLowerBound++;
				// This ensures if an even lower bound was given, the search will start at the next odd number
			}	
			for(int i = newLowerBound; i < this.upperBound; i+=2){
				boolean isPrime = true;
				for(int prime : this.primeFactors) {
					if(i % prime == 0 && i != prime) {
						isPrime = false;
						break;
					}
				}
				if(isPrime){
					this.primes.add(i);
				}
			}
		}
	} // End of generatePrimes
	
	/**
	 * This is a helper method for generatePrimes which gets the primes below the factor limit.
	 * It will try to load the specified file containing them if given, or it will manually generate them.
	 * @return An ArrayList of the primes below the factor limit of this sieve
	 */
	private ArrayList<Integer> getRequiredPrimes(){	
		if(this.inputFile != null){
			// Try to load the previous primes from the provided file
			try{
				File primeFile = new File(this.inputFile);
				Scanner fileScanner = new Scanner(primeFile);
				ArrayList<Integer> previousPrimes = new ArrayList<Integer>();
				
				while(fileScanner.hasNextLine()){
					Integer previousPrime = Integer.parseInt(fileScanner.nextLine());
					previousPrimes.add(previousPrime);
					if(previousPrime > this.factorLimit) {
						// All the primes needed to factor numbers below the upperbound have been added
						break;
					}
				}
				
				int lastPrimeInFile = previousPrimes.get(previousPrimes.size() - 1);
				if(lastPrimeInFile < this.factorLimit) {
					Sieve intermediateSieve = new Sieve(lastPrimeInFile + 2, this.factorLimit + 1, null);
					intermediateSieve.generatePrimes();
					// Generate the primes between the last prime in the file and the factor limit
					for(int i : intermediateSieve.getPrimes()) {
						previousPrimes.add(i);
					}
				}
				return previousPrimes;
			}
			catch (IOException e){
				System.out.println("Could not load file, generating primes below: " + this.lowerBound);
				Sieve lowerSieve = new Sieve(this.factorLimit);
				lowerSieve.generatePrimes();
				return lowerSieve.getPrimes();
			}
		}
		else if(this.primeFactors != null) {
			// The prime factors have been provided from setPrimeFactors
			return this.primeFactors;
		}
		else {
			// No file provided, manually calculate the previous primes
			Sieve lowerSieve = new Sieve(this.factorLimit);
			lowerSieve.generatePrimes();
			return lowerSieve.getPrimes();
		}
	} // End of getPreviousPrimes
	
	/**
	 * This method will write the primes in the sieve.
	 * It will name the file of the form "Primes [lowerBound,upperBound).txt"
	 * It will also set the value of Sieve.outputFile to the file name if there weren't any errors.
	 * @return The name of the file written to if correctly written to, null if there was an error
	 */
	public String writePrimes() {
		try {
			String fileName = "Primes [" + this.lowerBound + "," + this.upperBound + ").txt";
			File outputFile = new File(fileName);
			BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));
			for(int i : this.primes) {
				outputWriter.write(String.valueOf(i));
				outputWriter.newLine();
			}
			outputWriter.close();
			this.outputFile = fileName;
			// Assigns the value only if there were no errors writing
			return fileName;
		}
		catch(IOException e) {
			System.out.println("Could not write primes to the specified file");
			e.printStackTrace();
			return null;
		}
	} // End of writePrimes
	
	/**
	 * This method prints out relevant information regarding the sieve.
	 */
	public void printInfo() {
		System.out.println("This sieve looks for primes from " + this.lowerBound + " (inclusive) to " 
				+ this.upperBound + " (exclusive).");
		System.out.println("It has done this by using the primes below " + this.factorLimit 
				+ " to factor numbers in that range.");
		System.out.println("There are " + this.primes.size() + " primes between " 
				+ this.lowerBound + " (inclusive) and " + this.upperBound + " (exclusive)");
		System.out.println("It did this in: " + this.executionTime + " milliseconds.");
		if(this.outputFile != null) {
			System.out.println("The primes were written to: " + this.outputFile);
		}
	} // End of printInfo
	
	public int getLowerBound() {
		return this.lowerBound;
	}
	
	public int getUpperBound() {
		return this.upperBound;
	}
	
	public int getFactorLimit() {
		return this.factorLimit;
	}
	
	public ArrayList<Integer> getPrimes(){
		return this.primes;
	}
	
	public void setPrimes(ArrayList<Integer> primes) {
		this.primes = primes;
	}
	
	public long getExecutionTime() {
		return this.executionTime;
	}
	
	/**
	 * @return The prime numbers below the factorLimit used to find primes below the upperLimit
	 * Checks for null in the case where the sieve is being used in an iterated or parallel setup
	 */
	public ArrayList<Integer> getPrimeFactors(){
		if(this.primeFactors == null) {
			this.primeFactors = getRequiredPrimes();
		}
		return this.primeFactors;
	}
	
	/**
	 * @param primeFactors The prime factors used to factor the numbers below the upperLimit
	 */
	public void setPrimeFactors(ArrayList<Integer> primeFactors) {
		// Only added for functionality within the IteratedSieve class, only called in getRequiredPrimes
		this.primeFactors = primeFactors;
	}
	
	/**
	 * @return The primes from this sieve
	 * This implements the callable interface for parallel execution and also allows the driver to call generatePrimes
	 */
	public ArrayList<Integer> call(){
		long start = System.currentTimeMillis();
		generatePrimes();
		long end = System.currentTimeMillis();
		this.executionTime = end - start;
		return this.primes;
	} // End of call
	
} // End of Sieve
