import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Josh Morgan
 * An Iterative Parallel Long Sieve (IterativePLS) will create a Parallel Long Sieve for each
 * of the deltas and execute them in order of lowest to highest ranges.
 */
public class IterativePLS extends ParallelLongSieve{
	private long delta;
	private long iterations;
	
	IterativePLS(long delta, long iterations, String outputFolder){
		super(0, delta * iterations, null, outputFolder);
		this.delta = delta;
		this.iterations = iterations;
		if(verifyOutputFolder(outputFolder)) {
			// Start the sieving
			iterate();
		}
		else {
			// Error
			System.out.println("Could not create or verify the output folder");
		}
	} // End of constructor
	
	IterativePLS(long lowerBound, long delta, long iterations, String outputFolder){
		super(lowerBound, delta * iterations, null, outputFolder);
		this.delta = delta;
		this.iterations = iterations;
		if(verifyOutputFolder(outputFolder)) {
			// Start the sieving
			iterate();
		}
		else {
			// Error
		}
	} // End of constructor
	
	private boolean verifyOutputFolder(String outputFolder) {
		try {
			Path path = Paths.get(outputFolder);
			if(Files.exists(path)) {
				return true;
			}
			else {
				// Path is invalid, create the outputFolder
				String folder = "../Primes";
				File fileFolder = new File(folder);
				if(fileFolder.mkdir()) {
					// Create a folder on the same level as the java file
					return true;
				}
				return false;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	} // End of verifyOutputFolder
	
	private void iterate() {
		long lowerBound = super.getLowerBound();
		long upperBound = super.getLowerBound() + this.delta;
		for(int i = 0; i < this.iterations; i++) {
			ParallelLongSieve pls = new ParallelLongSieve(lowerBound, upperBound, null, super.getOutputFolder());
			pls.parallelSieve();
			pls.printInfo();
			lowerBound += this.delta;
			upperBound += this.delta;
		}
	} // End of iterate
	
	
} // End of IterativePLS