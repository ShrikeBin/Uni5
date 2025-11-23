package vvpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import vvpl.ast.Declaration;
import vvpl.errors.*;
import vvpl.interpret.Interpreter;
import vvpl.scan.Scanner;
import vvpl.parse.Parser;
import vvpl.scan.Token;
import vvpl.semantics.Canary;
import vvpl.ast.visitors.*;

public class Vvpl 
{
	public static void main(String[] args) throws IOException 
	{
		runFile(args[0]);
	}

	private static void runFile(String path) throws IOException 
	{
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes));
	}

	private static void run(String source) 
	{
		ErrorHandler.setPrintsOn();

		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();

		// // print the tokens
		// for (Token token : tokens) {
		// 	System.out.println(token);
		// }

		if(ErrorHandler.errors.size() != 0)
		{
			ErrorHandler.flush();
			System.err.println("[Errors during Scanning]");
			return;
		}

		Parser parser = new Parser(tokens);
		List<Declaration> program = parser.parse();

		// // print the tree
		// ASTPrinter printer = new ASTPrinter();
		// System.out.println(printer.print(program));

		if(ErrorHandler.errors.size() != 0)
		{
			ErrorHandler.flush();
			System.err.println("[Errors during Parsing]");
			return;
		}

		Canary canary = new Canary(program);
		canary.check();

		if(ErrorHandler.errors.size() != 0)
		{
			ErrorHandler.flush();
			System.err.println("[Errors during Semantic Check]");
			return;
		}

		Interpreter interpreter = new Interpreter();
		try 
		{
			interpreter.interpret(program);
		}
		catch (Exception error) 
		{
			System.err.println("[ERROR DURING INTERPRETATION]: " + error);
		}
	}
}
