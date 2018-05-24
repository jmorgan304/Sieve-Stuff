
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
		super(delta * iterations);
		this.delta = delta;
		this.iterations = iterations;
		this.outputFolder = outputFolder;
	} // End of constructor
	
	IterativePLS(long lowerBound, long delta, long iterations, String outputFolder){
		super(lowerBound, delta * iterations, null);
		this.delta = delta;
		this.iterations = iterations;
		this.outputFolder = outputFolder;
	} // End of constructor
	
} // End of IterativePLS