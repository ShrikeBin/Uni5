package vvpl.ast.expression;

import vvpl.ast.Expression;
import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;

public class Variable extends Expression
{
    public final Token name;

    public Variable(Token name)
    {
        this.name = name;
    }

    @Override
    public <T> T accept(Visitor<T> visitor)
    {
        return visitor.visitVariableExpr(this);
    }
}
