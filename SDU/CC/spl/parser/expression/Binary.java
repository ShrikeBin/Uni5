package spl.parser.expression;

import spl.parser.*;
import spl.scanner.Token;

public final class Binary extends Expression 
{
    public final Expression left; 
    public final Token operator; 
    public final Expression right;

    public Binary(Expression left, Token operator, Expression right) 
    { 
        this.left = left; 
        this.operator = operator; 
        this.right = right; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitBinaryExpr(this); 
    }
}
