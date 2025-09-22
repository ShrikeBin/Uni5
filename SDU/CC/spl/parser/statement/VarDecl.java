package spl.parser.statement;

import spl.parser.*;
import spl.scanner.Token;

public final class VarDecl extends Statement
{
    public final Token name; 
    public final Expression initializer;
    
    public VarDecl(Token name, Expression initializer) 
    { 
        this.name = name; 
        this.initializer = initializer; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitVarDecl(this); 
    }
}