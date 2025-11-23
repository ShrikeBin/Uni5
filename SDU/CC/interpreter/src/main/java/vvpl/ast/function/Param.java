package vvpl.ast.function;

import vvpl.scan.Token;
import vvpl.ast.*;
import vvpl.ast.visitors.Visitor;

public class Param extends Declaration
{
    public final Token name;  // Identifier
    public final Token type;  // Type annotation
    
    public Param(Token name, Token type) 
    { 
        this.name = name;
        this.type = type; 
    }

    @Override
    public <T> T accept(Visitor<T> visitor)
    {
        return visitor.visitParamDecl(this);
    }
}
