package vvpl.scan;

/**
 * The enum stores the types for a Token object
 * 
 * @author Compiler Construction
 * @version Efterar 2025
 * 
 */
public enum TokenType 
{
    // Keywords
    VAR,
    TYPE_DEF,
    ASSIGN,
    FUNCTION,
    RETURN,
    IF,
    ELSE,
    WHILE,
    PRINT,
    TRUE,
    FALSE,
    OR,
    AND,
    NOT,

    // Arithmetic operators
    ADD,
    SUB,
    MULT,
    DIV,

    // Types
    NUMBER_TYPE,
    STRING_TYPE,
    BOOL_TYPE,
    CAST,

    // Literals
    NUMBER,
    STRING,
    ID,

    // Symbols
    LEFT_PAREN,       // (
    RIGHT_PAREN,      // )
    LEFT_BRACE,       // {
    RIGHT_BRACE,      // }
    COMMA,            // ,
    SEMICOLON,        // ;
    MINUS,            // -
    // PLUS,             // +
    // STAR,             // *
    // SLASH,            // /

    // Comparison
    EQUALS,           // ==
    NOT_EQUALS,       // !=
    GREATER,          // >
    GREATER_EQUAL,    // >=
    LESS,             // <
    LESS_EQUAL,       // <=

    // End-of-file
    EOF,
}


