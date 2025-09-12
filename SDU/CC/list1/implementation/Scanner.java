package spl.scan;

import static spl.scan.TokenType.*;

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
public class Scanner {
	// Keyword-map
	private static final Map<String, TokenType> keywords;
	static {
		keywords = new HashMap<>();
		// add the keywords
	}

	// In and output
	private final String source;
	private final List<Token> tokens = new LinkedList<>();

	// Scan state
	private int start = 0;
	private int current = 0;
	private int line = 1;

	public Scanner(String source) {
		this.source = source;
	}

	// Scan tokens
	public List<Token> scanTokens() {
		while (!isAtEnd()) {
			// We are at the beginning of the next lexeme.
			start = current;
			scanToken();
		}

		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}

	// Scan token
	private void scanToken() {
		char c = advance();
		switch (c) {
		// single-char-tokens
		// tokens of multiple tokens
		// ...
		}
	}

	private void identifier() {
	}

	private void number() {

	}

	private void string() {

	}

	private char advance() {
		return source.charAt(current++);
	}

	private boolean match(char expected) {
		// implement logic
		return false;
	}

	private char peek() {
		if (isAtEnd())
			return '\0';
		return source.charAt(current);
	}


	private boolean isAtEnd() {
		return current >= source.length();
	}
}
