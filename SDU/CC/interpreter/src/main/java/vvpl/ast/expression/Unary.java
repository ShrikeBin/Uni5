package vvpl.ast.expression;

import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;
import vvpl.ast.*;

public class Unary extends Expression
{
    public final Token operator; 
    public final Expression right;

    public Unary(Token operator, Expression right) 
    { 
        this.operator = operator; 
        this.right = right; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitUnaryExpr(this); 
    } 
}
