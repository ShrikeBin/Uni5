package vvpl.ast.expression;

import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;
import vvpl.ast.*;

public class Binary extends Expression
{
    public final Token operator;
    public final Expression left; 
    public final Expression right;

    public Binary(Token operator, Expression left, Expression right) 
    {
        this.operator = operator; 
        this.left = left; 
        this.right = right; 
    }

    @Override
    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitBinaryExpr(this); 
    }  
}
