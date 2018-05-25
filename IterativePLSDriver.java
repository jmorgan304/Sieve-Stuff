import java.util.Scanner;

public class IterativePLSDriver {

	public static void main(String[] args) {
		IterativePLS ps = processArgs(args);
	}
	
	public static IterativePLS processArgs(String[] args) {
		long delta;
		long iterations;
		String outputFolder;
		long lowerBound;
		String inputFile;
		try {
			switch(args.length) {
				case 3 : delta = Long.parseLong(args[0]);
					iterations = Long.parseLong(args[1]);
					outputFolder = args[2];
					return new IterativePLS(delta, iterations, outputFolder);
				case 4 : lowerBound = Long.parseLong(args[0]);
					delta = Long.parseLong(args[1]);
					iterations = Long.parseLong(args[2]);
					outputFolder = args[3];
					return new IterativePLS(lowerBound, delta, iterations, outputFolder);
				default : throw new NumberFormatException();
			}
		}
		catch(NumberFormatException e) {
			System.out.println("Please type the arguments as either: ");
			System.out.println("delta iterations outputFolder");
			System.out.println("Or: ");
			System.out.println("lowerBound delta iterations outputFolder");
			String[] args2 = new Scanner(System.in).nextLine().split(" ");
			return processArgs(args2);
		}
	} // End of processArgs
	
}
