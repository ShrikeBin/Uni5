import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements a hand-written scanner with error handling and character-wise reading. 
 * 
 * @author Compiler Construction
 * @version Efterar 2025
 * 
 */
public class Scanner 
{
	// Keyword-map
	private static final Map<String, TokenType> keywords;
	static 
	{
		keywords = new HashMap<>();
		keywords.put("if",    TokenType.IF);
		keywords.put("else", TokenType.ELSE);
		keywords.put("else if", TokenType.ELSE_IF);
		keywords.put("for",   TokenType.FOR);
		keywords.put("while", TokenType.WHILE);
		keywords.put("return", TokenType.RETURN);
		keywords.put("func", TokenType.FUNCTION);
		keywords.put("var",   TokenType.VAR);
		keywords.put("print", TokenType.PRINT);
		keywords.put("true",  TokenType.TRUE);
		keywords.put("false", TokenType.FALSE);
		keywords.put("null",  TokenType.NULL);
	}

	// In and output
	private final String source;
	private final List<Token> tokens = new LinkedList<>();

	// Scan state
	private int start = 0;
	private int current = 0;
	private int line = 1;

	public Scanner(String source) 
	{
		this.source = source;
	}

	// Scan tokens
	public List<Token> scanTokens() 
	{
		while (!isAtEnd()) 
		{
			start = current;
			scanToken();
		}

		addToken(TokenType.EOF, "", null);
		return tokens;
	}

	// Scan token
	private void scanToken() 
	{
		char c = advance();
		switch (c) 
		{
			// single-char tokens
			case '(': addToken(TokenType.LEFT_PAREN); break;
			case ')': addToken(TokenType.RIGHT_PAREN); break;
			case '{': addToken(TokenType.LEFT_BRACE); break;
			case '}': addToken(TokenType.RIGHT_BRACE); break;
			case ',': addToken(TokenType.COMMA); break;
			case '.': addToken(TokenType.DOT); break;
			case ';': addToken(TokenType.SEMICOLON); break;
			case '%': addToken(TokenType.MODULUS); break;
			
			// double-char tokens
			case '&': if (match('&')) addToken(TokenType.AND); break;
			case '|': if (match('|')) addToken(TokenType.OR); break;
			case '+': if (match('+')) addToken(TokenType.PLUS_PLUS); else addToken(TokenType.PLUS); break;
			case '-': if (match('-')) addToken(TokenType.MINUS_MINUS); else addToken(TokenType.MINUS); break;
			case '*': if (match('*')) addToken(TokenType.POWER); else addToken(TokenType.STAR); break;
			case '!': if (match('=')) addToken(TokenType.NOT_EQUAL); else addToken(TokenType.NOT); break;
			case '=': if (match('=')) addToken(TokenType.EQUAL); else addToken(TokenType.ASSIGN); break;
			case '<': if (match('=')) addToken(TokenType.LESS_EQUAL); else addToken(TokenType.LESS); break;
			case '>': if (match('=')) addToken(TokenType.GREATER_EQUAL); else addToken(TokenType.GREATER); break;

			// comments
			case '/':
				if (match('/')) 
				{
					while (peek() != '\n' && !isAtEnd()) 
					{
						advance();
					}
				} 
				else if (match('*')) 
				{
					while (!(peek() == '*' && peekFurther() == '/') && !isAtEnd()) 
					{
						if (peek() == '\n') 
						{
							line++;
						}
						advance();
					}

					if (isAtEnd()) 
					{
						Spl.error(line, "Unterminated block comment.");
						return;
					}

					advance(); // consume '*'
					advance(); // consume '/'
				} 
				else 
				{
					addToken(TokenType.SLASH);
				}
				break;
			
			// whitespaces
			case ' ':
			case '\r':
			case '\t':
				break;

			case '\n':
				line++;
				break;

			case '"':
				string();
				break;

			default:
				identifier(c);
				break;
		}
	}

	private void identifier(char c) 
	{
		if(isAlpha(c))
		{
			while (isAlphaNumeric(peek())) advance();
	
			String text = source.substring(start, current);
	
			TokenType type = keywords.get(text);
			if (type == null) 
			{
				type = TokenType.IDENTIFIER;
				addToken(type, text, null);
			}
			else 
			{
				addToken(type);
			}
		}
		else if(isDigit(c))
		{
			number();
		}
		else
		{
			Spl.error(line, "Unexpected character: " + c);
		}
	}

	private void number() 
	{
		boolean isFloat = false;

		while (isDigit(peek())) advance();

		// Look for floating point part
		if (peek() == '.' && isDigit(peekFurther())) 
		{
			advance(); // consume '.'
			isFloat = true;
			while (isDigit(peek())) advance();
		}

		String value = source.substring(start, current);

		if(isFloat)
		{
			addToken(TokenType.NUMBER, value, Double.parseDouble(value));
			return;
		}
		else
		{
			addToken(TokenType.NUMBER, value, Integer.parseInt(value));
		}
	}

	private void string() 
	{
		while (peek() != '"' && !isAtEnd()) 
		{
			if (peek() == '\n') line++;		
			advance();
		}

		if (isAtEnd()) 
		{
			Spl.error(line, "Unterminated string.");
			return;
		}

		advance(); // consume the closing "

		String value = source.substring(start + 1, current - 1);

		addToken(TokenType.STRING, null, value);
	}

	private char advance() 
	{
		return source.charAt(current++);
	}

	private boolean match(char expected) 
	{
		if (isAtEnd())
		{
			return false;
		}

		if (source.charAt(current) != expected)
		{
			return false;
		}

		current++;

		return true;
	}

	private char peek() 
	{
		if (isAtEnd())
		{
			return '\0';
		}

		return source.charAt(current);
	}

	private char peekFurther()
	{
		if(isAtEnd() || current + 1 >= source.length())
		{
			return '\0';
		}

		return source.charAt(current + 1);
	}

	private boolean isAtEnd() 
	{
		return current >= source.length();
	}

	private void addToken(TokenType type, String lexeme, Object literal) 
	{
		tokens.add(new Token(type, lexeme, literal, line));
	}

	private void addToken(TokenType type) 
	{
		addToken(type, "", null);
	}

	private boolean isDigit(char c) 
	{
    	return c >= '0' && c <= '9';
	}

	private boolean isAlpha(char c) 
	{
		return (c >= 'a' && c <= 'z') ||(c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean isAlphaNumeric(char c) 
	{
		return isAlpha(c) || isDigit(c);
	}
}
