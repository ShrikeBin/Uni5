package vvpl.scan;

/**
 * The class Token stores the essential data for a token object and
 * provides a custom toString() method.
 * 
 * @author Compiler Construction
 * @version Efterar 2025
 * 
 */
public class Token 
{
	public final TokenType type;		// Type of token
	public final String lexeme;         // Literal string representation
	public final Object literal;	    // Parsed representation
	public final int line;				// Line in source code

	public Token(TokenType type, String lexeme, Object literal, int line) 
	{
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.line = line;
	}

	public String toString() 
	{
		return "<" + type + "," + lexeme + "> " + "Literal: " + literal + ", Line: " + line;
	}
}

