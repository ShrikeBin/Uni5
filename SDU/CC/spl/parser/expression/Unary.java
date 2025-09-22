package spl.parser.expression;

import spl.parser.*;
import spl.scanner.Token;

public final class Unary extends Expression 
{
    public final Token operator; 
    public final Expression right;

    public Unary(Token operator, Expression right) 
    { 
        this.operator = operator; 
        this.right = right; 
    }

    public  <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitUnaryExpr(this); 
    }
}