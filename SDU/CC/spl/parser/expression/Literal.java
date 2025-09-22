package spl.parser.expression;

import spl.parser.*;
import spl.scanner.Token;

public final class Literal extends Expression 
{
    public final Token value;

    public Literal(Token value) 
    { 
        this.value = value; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitLiteralExpr(this); 
    }
}
