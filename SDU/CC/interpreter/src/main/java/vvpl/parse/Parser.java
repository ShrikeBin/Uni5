package vvpl.parse;

import java.util.LinkedList;
import java.util.List;

import vvpl.ast.*;
import vvpl.ast.expression.*;
import vvpl.ast.statement.*;
import vvpl.ast.function.*;
import vvpl.ast.variable.*;
import vvpl.errors.*;
import vvpl.scan.Token;
import vvpl.scan.TokenType;

/**
 * @author Nel Skowronek
 * @version CompilerConstruction FT 2025
 */

public class Parser 
{
    private List<Token> tokens;
    private int current = 0;
   
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Declaration> parse() {
        List<Declaration> program = new LinkedList<>();
       
        while (!isAtEnd()) {
            Declaration declaration = declaration();
            if (declaration != null) {
                program.add(declaration);
            }
        }

        return program;
    }

    private Declaration declaration() {
        try {
            if (match(TokenType.VAR)) return varDeclaration();
            if (match(TokenType.FUNCTION)) return funDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Declaration varDeclaration() throws ParseError {
        Token name = consume(TokenType.ID, "Expected variable name.");
        consume(TokenType.TYPE_DEF, "Expected 'has_type'.");
        Token type = consume(new TokenType[]{TokenType.NUMBER_TYPE, TokenType.STRING_TYPE, TokenType.BOOL_TYPE}, "Expected variable type.");

        Expression initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = expression();
        }
        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration.");
        return new VarDecl(name, type, initializer);
    }

    private Declaration funDeclaration() throws ParseError {
        Token name = consume(TokenType.ID, "Expected function name.");
        consume(TokenType.LEFT_PAREN, "Expected '(' after function name.");
        List<Param> params = params();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after function parameters.");

        Token type = null;
        if (match(TokenType.TYPE_DEF)) {
            type = consume(new TokenType[]{TokenType.NUMBER_TYPE, TokenType.STRING_TYPE, TokenType.BOOL_TYPE}, "Expected return type.");
        }
        consume(TokenType.LEFT_BRACE, "Expected { after function declaration.");
        Statement body = block();
        return new FuncDecl(name, params, type, body);
    }

    private List<Param> params() throws ParseError {
        List<Param> params = new LinkedList<>();
        if (check(TokenType.RIGHT_PAREN)) return params;
        params.add(param());
        while (!check(TokenType.RIGHT_PAREN) && !isAtEnd()) {
            consume(TokenType.COMMA, "Expected ',' after parameter.");
            params.add(param());
        }
        return params;
    }

    private Param param() throws ParseError {
        Token id = consume(TokenType.ID, "Expected parameter name.");
        consume(TokenType.TYPE_DEF, "Expected 'has_type'.");
        Token type = consume(new TokenType[]{TokenType.NUMBER_TYPE, TokenType.STRING_TYPE, TokenType.BOOL_TYPE}, "Expected parameter type.");
        return new Param(id, type);
    }

    private Statement statement() throws ParseError {
        if (match(TokenType.LEFT_BRACE)) return block();
        if (check(TokenType.PRINT)) return printStmt(advance());
        if (check(TokenType.IF)) return ifStmt(advance());
        if (check(TokenType.WHILE)) return whileStmt(advance());
        if (check(TokenType.RETURN)) return returnStmt(advance());
        return expressionStmt();
    }

    private Statement ifStmt(Token keyword) throws ParseError {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'.");
        Expression condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition.");
        Statement thenBranch = statement();

        Statement elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }
        return new If(keyword, condition, thenBranch, elseBranch);
    }

    private Statement whileStmt(Token keyword) throws ParseError {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'.");
        Expression condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition.");
        Statement body = statement();
        return new While(keyword, condition, body);
    }

    private Statement printStmt(Token keyword) throws ParseError {
        Expression value = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after print statement.");
        return new Print(keyword, value);
    }

    private Statement block() throws ParseError {
        List<Declaration> declarations = new LinkedList<>();
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            Declaration declaration = declaration();
            if (declaration != null) {
                declarations.add(declaration);
            }
        }
        consume(TokenType.RIGHT_BRACE, "Expected '}' after block.");
        return new Block(declarations);
    }

    private Statement returnStmt(Token keyword) throws ParseError {
        Expression value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }
        consume(TokenType.SEMICOLON, "Expected ';' after return.");
        return new Return(keyword, value);
    }

    private Statement expressionStmt() throws ParseError {
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after expression.");
        return new Expr(expr);
    }

    // Expressions: assignment -> or
    private Expression expression() throws ParseError {
        return assignment();
    }

    private Expression assignment() throws ParseError {
        if (checkAhead(TokenType.ASSIGN)) {
            Token name = consume(TokenType.ID, "Expected identifier as left operand of assignment.");
            advance();
            Expression value = assignment();
            return new Assignment(name, value);
        }
        return or();
    }

    private Expression or() throws ParseError {
        Expression expr = and();
        while (check(TokenType.OR)) {
            Token operator = advance();
            Expression right = and();
            expr = new Logical(expr, operator, right);
        }
        return expr;
    }

    private Expression and() throws ParseError {
        Expression expr = equality();
        while (check(TokenType.AND)) {
            Token op = advance();
            Expression right = equality();
            expr = new Logical(expr, op, right);
        }
        return expr;
    }

    private Expression equality() throws ParseError {
        Expression expr = comparison();
        while (check(TokenType.NOT_EQUALS, TokenType.EQUALS)) {
            Token op = advance();
            Expression right = comparison();
            expr = new Binary(op, expr, right);
        }
        return expr;
    }

    private Expression comparison() throws ParseError {
        Expression expr = term();
        while (check(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token op = advance();
            Expression right = term();
            expr = new Binary(op, expr, right);
        }
        return expr;
    }

    private Expression term() throws ParseError {
        if (check(TokenType.ADD, TokenType.SUB, TokenType.MULT, TokenType.DIV)) {
            Token op = advance();
            consume(TokenType.LEFT_PAREN, "Expected '(' after operator.");
            Expression left = term();
            Expression right = term();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after operator.");
            return new Binary(op, left, right);
        }
        return unary();
    }

    private Expression unary() throws ParseError {
        if (check(TokenType.NOT, TokenType.MINUS)) {
            Token op = advance();
            Expression right = unary();
            return new Unary(op, right);
        }
        return call();
    }

    private Expression call() {
        if (check(TokenType.ID) && checkAhead(TokenType.LEFT_PAREN)) {
            Token name = advance();
            advance();
            List<Expression> args = args();
            return new Call(name, args);
        }
        return cast();
    }

    private Expression cast() throws ParseError {
        if (match(TokenType.CAST)) {
            Token type = consume(new TokenType[]{TokenType.NUMBER_TYPE, TokenType.STRING_TYPE, TokenType.BOOL_TYPE}, "Expected casted type.");
            return new Cast(type, primary());
        }
        return primary();
    }

    private Expression primary() throws ParseError {
        if (check(TokenType.ID)) return new Variable(advance());
        if (check(TokenType.NUMBER, TokenType.STRING, TokenType.TRUE, TokenType.FALSE))
            return new Literal(advance());
        consume(TokenType.LEFT_PAREN, "Expected expression.");
        Expression expr = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.");
        return expr;
    }

    private List<Expression> args() throws ParseError {
        List<Expression> args = new LinkedList<>();
        if (match(TokenType.RIGHT_PAREN)) return args;
        args.add(expression());
        while (!match(TokenType.RIGHT_PAREN) && !isAtEnd()) {
            consume(TokenType.COMMA, "Expected ',' after argument.");
            args.add(expression());
        }
        return args;
    }

    private boolean match(TokenType... types) {
        if (check(types)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean check(TokenType... types) {
        for (TokenType type : types) {
            if (peek().type == type) return true;
        }
        return false;
    }

    private boolean checkAhead(TokenType type) {
        return peekAhead().type == type;
    }

    private Token advance() {
        return tokens.get(current++);
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token peekAhead() {
        if (isAtEnd()) return peek();
        return tokens.get(current + 1);
    }

    private Token consume(TokenType type, String message) throws ParseError {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private Token consume(TokenType[] types, String message) throws ParseError {
        if (check(types)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        ErrorHandler.error(token.line, "Parse error at '" + token.lexeme + "': " + message);
        return new ParseError(message);
    }

    private void synchronize() {
        while (!isAtEnd()) {
            if (match(TokenType.SEMICOLON)) return;
            advance();
        }
    }
}
