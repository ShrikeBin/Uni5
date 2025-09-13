/**
 * The enum stores the types for a Token object
 * 
 * @author Compiler Construction
 * @version Efterar 2025
 * 
 */
public enum TokenType 
{
	// Single-character tokens
	LEFT_PAREN, 
	RIGHT_PAREN, 
	LEFT_BRACE, 
	RIGHT_BRACE,
	COMMA,
	DOT,
	SEMICOLON,

	// One or two character tokens
	NOT, 
	NOT_EQUAL,
	EQUAL,
	ASSIGN,
	LESS,
	LESS_EQUAL,
	GREATER,
	GREATER_EQUAL,
	PLUS,
	MINUS,
	STAR,
	POWER,
	SLASH,
	MODULUS,
	AND,
	OR,
	PLUS_PLUS,
	MINUS_MINUS,

	// Literals
	IDENTIFIER,
	STRING,
	NUMBER,

	// Keywords
	IF,
	ELSE,
	ELSE_IF,
	FOR,
	WHILE,
	RETURN,
	FUNCTION,
	VAR,
	PRINT,
	TRUE,
	FALSE,
	NULL,

	// End-of-file
	EOF
}
