package spl.parser;

import spl.scanner.Token;

public final class Var extends Declaration 
{
    public final Token name; 
    public final Expression initializer;
    
    public Var(Token name, Expression initializer) 
    { 
        this.name = name; 
        this.initializer = initializer; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitVarDecl(this); 
    }
}