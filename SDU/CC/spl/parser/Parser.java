package spl.parser;

import java.util.ArrayList;
import java.util.List;

import spl.Spl;
import spl.scanner.*;
import spl.parser.statement.*;
import spl.parser.expression.*;

public class Parser 
{
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) 
    { 
        this.tokens = tokens; 
    }

    public List<Declaration> parse() 
    {
        List<Declaration> program = new ArrayList<>();
        while (!isAtEnd()) 
        {
            Declaration decl = declaration();
            if (decl != null) program.add(decl);
        }
        return program;
    }

    private Declaration declaration() 
    {
        try 
        {
            if (match(TokenType.FUNCTION_DEC))
            {
                return funDeclaration();
            }

            return statement();
        } 
        catch (ParseError error) 
        {
            synchronize();
            return null;
        }
    }

    private Declaration funDeclaration()
    {
        Token name = consume(TokenType.IDENTIFIER, "Expected function name.");
        consume(TokenType.LEFT_PAREN, "Expected ( after function declaration.");
        List<Token> params = new ArrayList<>();
        while(!match(TokenType.RIGHT_PAREN))
        {
            Token var = consume(TokenType.IDENTIFIER, "Expected variable in function parameters.");
            params += var;
            if(peek(TokenType.RIGHT_PAREN))
            {
                break;
            }
            consume(TokenType.COMMA, "Expected , between parameters");
        }
        
        return new FunDecl(name, params, null, null);
    }

    private Declaration varDeclaration() throws ParseError 
    {
        Token name = consume(TokenType.IDENTIFIER, "Expected variable name.");
        Expression initializer = null;

        if (match(TokenType.ASSIGN)) 
        {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration.");

        return new VarDecl(name, initializer);
    }

    private Statement statement() throws ParseError 
    {
        if (match(TokenType.PRINT)) 
        {
            return printStmt();
        }

        if (match(TokenType.LEFT_BRACE)) 
        {
            return new Block(block());
        }

        if (match(TokenType.IF))
        {
            return ifStmt();
        }

        if (match(TokenType.WHILE)) 
        {
            return whileStmt();
        }

        return expressionStmt();
    }

    private Statement ifStmt() throws ParseError 
    {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'.");

        Expression condition = expression();

        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition.");

        Statement thenBranch = statement();
        Statement elseBranch = null;

        if (match(TokenType.ELSE)) 
        {
            elseBranch = statement();
        }

        return new If(condition, thenBranch, elseBranch);
    }

    private Statement whileStmt() throws ParseError 
    {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'.");

        Expression condition = expression();

        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition.");

        Statement body = statement();

        return new While(condition, body);
    }

    private Statement printStmt() throws ParseError 
    {
        Expression value = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after value.");
        return new Print(value);
    }

    private List<Declaration> block() throws ParseError 
    {
        List<Declaration> declarations = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) 
        {
            declarations.add(declaration());
        }

        consume(TokenType.RIGHT_BRACE, "Expected '}' after block.");
        return declarations;
    }

    private Statement expressionStmt() throws ParseError 
    {
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after expression.");
        return expr;
    }

    // Expressions: assignment -> or
    private Expression expression() throws ParseError 
    {
        return assignment();
    }

    private Expression assignment() throws ParseError 
    {
        if (checkAhead(TokenType.ASSIGN)) 
        {
            Token name = consume(TokenType.IDENTIFIER, "Expected identifier as left operand of assignment.");
            advance();
            Expression value = assignment();
            return new Assign(name, value);
        }
        return or();
    }

    private Expression or() throws ParseError
    {
        Expression expr = and();

        while (check(TokenType.OR)) 
        {
            Token operator = advance();
            Expression right = and();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expression and() throws ParseError 
    {
        Expression expr = equality();

        while (check(TokenType.AND)) 
        {
            Token operator = advance();
            Expression right = equality();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expression equality() throws ParseError 
    {
        Expression expr = comparison();
        while (check(TokenType.NOT_EQUAL, TokenType.EQUAL)) 
        {
            Token op = advance();
            Expression right = comparison();
            expr = new Logical(expr, op, right);
        }
        return expr;
    }

    private Expression comparison() throws ParseError 
    {
        Expression expr = term();

        while (check(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) 
        {
            Token op = advance();
            Expression right = term();
            expr = new Logical(expr, op, right);
        }

        return expr;
    }

    private Expression term() throws ParseError 
    {
        Expression expr = factor();
        while (check(TokenType.MINUS, TokenType.PLUS)) 
        {
            Token op = advance();
            Expression right = factor();
            expr = new Binary(expr, op, right);
        }
        return expr;
    }

    private Expression factor() throws ParseError 
    {
        Expression expr = unary();
        while (check(TokenType.SLASH, TokenType.STAR)) 
        {
            Token op = advance();
            Expression right = unary();
            expr = new Binary(expr, op, right);
        }
        return expr;
    }

    private Expression unary() throws ParseError 
    {
        if (check(TokenType.NOT, TokenType.MINUS)) 
        {
            Token op = advance();
            Expression right = unary();
            return new Unary(op, right);
        }
        return primary();
    }

    private Expression primary() throws ParseError 
    {
        if (check(TokenType.IDENTIFIER)) 
        {
            return new Variable(advance());
        }

        if (check(TokenType.NUMBER, TokenType.STRING, TokenType.TRUE, TokenType.FALSE))
        {
            return new Literal(advance());
        }

        consume(TokenType.LEFT_PAREN, "Expected expression.");
        Expression expr = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.");
        return expr;
    }

    private boolean match(TokenType type) 
    {
        if (check(type)) 
        {
            advance();
            return true;
        }
        return false;
    }

    private boolean check(TokenType... types) 
    {
        for (TokenType type : types) 
        {
            if (peek().type == type) return true;
        }
        return false;
    }

    private boolean checkAhead(TokenType type) 
    {
        return peekAhead().type == type;
    }

    private Token advance() 
    {
        return tokens.get(current++);
    }

    private boolean isAtEnd() 
    {
        return peek().type == TokenType.EOF;
    }

    private Token peek() 
    {
        return tokens.get(current);
    }

    private Token peekAhead() 
    {
        if (isAtEnd()) 
        {
            return peek();
        }
        return tokens.get(current + 1);
    }

    private Token consume(TokenType type, String message) throws ParseError 
    {
        if (check(type)) 
        {
            return advance();
        }
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) 
    {
        Spl.error(token.line, "Parse error at '" + token.lexeme + "': " + message);
        return new ParseError();
    }

    private void synchronize() 
    {
        while (!isAtEnd()) 
        {
            if (match(TokenType.SEMICOLON)) 
            {
                return;
            }
            advance();
        }
    }
}

