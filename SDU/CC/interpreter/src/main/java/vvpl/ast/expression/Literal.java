package vvpl.ast.expression;

import vvpl.ast.Expression;
import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;

public class Literal extends Expression
{
    public final Token value;

    public Literal(Token value)
    {
        this.value = value;
    }

    @Override
    public <T> T accept(Visitor<T> visitor)
    {
        return visitor.visitLiteralExpr(this);
    }
}
