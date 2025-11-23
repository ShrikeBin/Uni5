package vvpl.ast.expression;

import java.util.List;

import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;
import vvpl.ast.*;

// That is the bottom most class I think
// its either a primary or a call
// where primary is either a literal, variable or another expression in parentheses
public class Call extends Expression
{
    // public final Token name
    public final Token ID; // may have type
    public final List<Expression> args;

    public Call(Token ID, List<Expression> args) 
    {
        this.ID = ID;
        this.args = args;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) 
    {
        return visitor.visitCallExpr(this);
    }
}
