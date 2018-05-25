import java.util.Scanner;

public class ParallelLongSieveDriver {

	public static void main(String[] args) {
		ParallelLongSieve ps = processArgs(args);
		ps.parallelSieve();
		if(ps != null) {
			ps.call();
			if(promptForWriting(ps.getLowerBound(), ps.getUpperBound(), false)) {
				ps.writePrimes();
				System.out.println();
			}
			ps.printInfo();
		}
	}
	
	public static ParallelLongSieve processArgs(String[] args) {
		long upperBound;
		long lowerBound;
		String inputFile;
		try {
			switch(args.length) {
				case 0 : throw new NumberFormatException();
				case 1 : upperBound = Long.parseLong(args[0]);
					return new ParallelLongSieve(upperBound);
				case 2 : lowerBound = Long.parseLong(args[0]);
					upperBound = Long.parseLong(args[1]);
					return new ParallelLongSieve(lowerBound, upperBound, null);
				default : lowerBound = Long.parseLong(args[0]);
					upperBound = Long.parseLong(args[1]);
					inputFile = args[2];
					return new ParallelLongSieve(lowerBound, upperBound, inputFile);
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
	
	public static boolean promptForWriting(long lowerBound, long upperBound, boolean repeat) {
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

}
