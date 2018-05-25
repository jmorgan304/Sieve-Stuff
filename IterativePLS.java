import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Josh Morgan
 * An Iterative Parallel Long Sieve (IterativePLS) will create a Parallel Long Sieve for each
 * of the deltas and execute them in order of lowest to highest ranges.
 */
public class IterativePLS extends ParallelLongSieve{
	private String outputFolder;
	private long delta;
	private long iterations;
	
	IterativePLS(long delta, long iterations, String outputFolder){
		super(0, delta * iterations, null, false);
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
		super(lowerBound, delta * iterations, null, false);
		this.delta = delta;
		this.iterations = iterations;
		if(verifyOutputFolder(outputFolder)) {
			// Start the sieving
		}
		else {
			// Error
		}
	} // End of constructor
	
	private boolean verifyOutputFolder(String outputFolder) {
		try {
			Path path = Paths.get(outputFolder);
			this.outputFolder = outputFolder;
			return true;
		}
		catch(InvalidPathException e) {
			// Path is invalid, create the outputFolder
			String path = "../Primes";
			File folder = new File(path);
			if(folder.mkdir()) {
				// Create a folder on the same level as the java file
				this.outputFolder = path;
				return true;
			}
			return false;
		}
		
	} // End of verifyOutputFolder
	
	private void iterate() {
		long lowerBound = super.getLowerBound();
		long upperBound = super.getLowerBound();
		for(int i = 0; i < this.iterations; i++) {
			lowerBound += i * this.delta;
			upperBound += (i + 1) * this.delta;
			ParallelLongSieve pls = new ParallelLongSieve(lowerBound, upperBound, null, false);
			pls.parallelSieve();
			pls.printInfo();
		}
	} // End of iterate
	
	
} // End of IterativePLS