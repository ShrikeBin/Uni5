package spl.parser.expression;

import spl.parser.*;
import spl.scanner.Token;

public final class Assign extends Expression 
{
    public final Token name;
    public final Expression value;

    public Assign(Token name, Expression value) 
    { 
        this.name = name; this.value = value; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitAssignExpr(this); 
    }
}