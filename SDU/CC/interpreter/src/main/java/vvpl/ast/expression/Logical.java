package vvpl.ast.expression;

import vvpl.ast.*;
import vvpl.ast.visitors.Visitor;
import vvpl.scan.*;

public class Logical extends Expression 
{
    public final Expression left;
    public final Token operator;
    public final Expression right;

    public Logical(Expression left, Token operator, Expression right)
    {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <T> T accept(Visitor<T> visitor)
    {
        return visitor.visitLogicalExpr(this);
    }
}

