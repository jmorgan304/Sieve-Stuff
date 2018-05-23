import java.util.Scanner;
/**
 * @author Josh Morgan
 * This class is a driver to highlight the features of a Sieve object.
 * The command line arguments will be interpreted with the corresponding Sieve constructors.
 * The user will also be asked if they would like to create a file containing the primes.
 */
public class SieveDriver {

	/**
	 * @param args
	 * If one integer is given, then that will be used as the upper bound for a Sieve.
	 * If two integers are given, they will be used as the lower and upper bounds respectively.
	 * A third argument will be assumed to be the input file, and any additional arguments will not be used.
	 */
	public static void main(String[] args) {
		Sieve primeSieve = processArgs(args);
		if(primeSieve != null) {
			primeSieve.call();
			if(promptForWriting(primeSieve.getLowerBound(), primeSieve.getUpperBound(), false)) {
				primeSieve.writePrimes();
			}
			primeSieve.printInfo();
		}
	} // End of main
	
	/**
	 * @param args The command line arguments or prompted user arguments
	 * @return A Sieve with the corresponding parameters
	 */
	public static Sieve processArgs(String[] args) {
		int upperBound;
		int lowerBound;
		String inputFile;
		try {
			switch(args.length) {
				case 0 : throw new NumberFormatException();
				case 1 : upperBound = Integer.parseInt(args[0]);
					return new Sieve(upperBound);
				case 2 : lowerBound = Integer.parseInt(args[0]);
					upperBound = Integer.parseInt(args[1]);
					return new Sieve(lowerBound, upperBound, null);
				default : lowerBound = Integer.parseInt(args[0]);
					upperBound = Integer.parseInt(args[1]);
					inputFile = args[2];
					return new Sieve(lowerBound, upperBound, inputFile);
			}
		}
		catch(NumberFormatException e) {
			System.out.println("Please type the arguments as either: ");
			System.out.println("upperBound");
			System.out.println("Or: ");
			System.out.println("lowerBound upperBound optionalInputFile");
			String[] args2 = new Scanner(System.in).nextLine().split(" ");
			return processArgs(args2);
		}
	} // End of processArgs
	
	/**
	 * @param lowerBound The lower bound of the Sieve to be written
	 * @param upperBound The upper bound of the Sieve to be written
	 * @param repeat A flag for handling incorrect input
	 * @return True if writing to file, False if otherwise
	 */
	public static boolean promptForWriting(int lowerBound, int upperBound, boolean repeat) {
		if(! repeat) {
			System.out.println("Would you like to write the primes to a file named: ");
			System.out.println("Primes [" + lowerBound + "," + upperBound + ").txt ?");
		}
		System.out.println("Please type Y or N");
		String response = new Scanner(System.in).nextLine();
		if(response.equalsIgnoreCase("Y")) {
			return true;
		}
		else if(response.equalsIgnoreCase("N")) {
			return false;
		}
		else {
			return promptForWriting(0, 0, true);
		}
	} // End of promptForWriting

} // End of SieveDriver
