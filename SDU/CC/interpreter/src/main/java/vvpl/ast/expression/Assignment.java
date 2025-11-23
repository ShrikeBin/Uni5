package vvpl.ast.expression;

import vvpl.ast.Expression;
import vvpl.scan.*;

public final class Assignment extends Expression
{
    // assignment := ID "is" assignment | logicOr ; <- WHAT TO DO WITH THAT?
    public final Token ID;
    public final Expression value;

    public Assignment(Token ID, Expression value) 
    {
        this.ID = ID;
        this.value = value;
    }

    @Override
    public <T> T accept(vvpl.ast.visitors.Visitor<T> visitor)
    {
        return visitor.visitAssignExpr(this);
    }
}
