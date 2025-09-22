package spl.parser;

import java.util.List;

import spl.scanner.Token;

public class FunDecl extends Declaration
{
    public final Token name; 
    public final List<Token> params;
    public final List<Statement> body;
    public final Return result;
    
    public FunDecl(Token name, List<Token> params, List<Statement> body, Return result) 
    { 
        this.name = name; 
        this.params = params;
        this.body = body;
        this.result = result;
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitFunDecl(this); 
    }
    
}

