package spl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import spl.parser.*;
import spl.scanner.*;

/**
 * The main class for the SPL interpreter. It handles reading a file,
 * scanning it, and printing the resulting tokens.
 * 
 * @author Jan Ryszkiewicz
 * @version 0.3
 * 
 */
public class Spl 
{
	// Expects a single file that comprises a SPL program as argument
	public static void main(String[] args) throws IOException 
	{
		if(args.length != 1) 
		{
			System.err.println("Usage: java -cp bin Spl [filepath]");
			System.exit(64);
		}
		runFile(args[0]);
	}

	private static void runFile(String path) throws IOException 
	{
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes));
	}

	private static void run(String source) 
	{
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();

		// print the tokens
		for (Token token : tokens) 
		{
			System.out.println(token);
		}

		Parser parser = new Parser(tokens);
		List<Declaration> statements = parser.parse();

		// print the tree
		ASTPrinter printer = new ASTPrinter();
		System.out.println(printer.print(statements));
	}

	public static void error(int line, String message) 
	{
		report(line, "", message);
	}

	private static void report(int line, String where, String message) 
	{
		System.err.println("[line " + line + "] Error" + where + ": " + message);
	}

}
