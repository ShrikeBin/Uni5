package spl.parser.expression;

import spl.parser.*;
import spl.scanner.Token;

public final class Variable extends Expression 
{
    public final Token name;

    public Variable(Token name) 
    { 
        this.name = name; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitVariableExpr(this); 
    }
}
