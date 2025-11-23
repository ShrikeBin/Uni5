package vvpl.ast.variable;

import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;
import vvpl.ast.*;

public class VarDecl extends Declaration
{
    public final Token name;  // Identifier
    public final Token type;  // Type annotation
    public final Expression initializer;
    
    public VarDecl(Token name, Token type, Expression initializer) 
    { 
        this.name = name;
        this.type = type; 
        this.initializer = initializer; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitVarDecl(this); 
    }
}
