package vvpl.ast.function;

import java.util.List;

import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;
import vvpl.ast.*;


public class FuncDecl extends Declaration
{
    public final Token name;
    public final List<Param> params;
    public final Token type;
    public final Statement body;

    public FuncDecl(Token name, List<Param> params, Token type, Statement body) 
    { 
        this.name = name; 
        this.params = params;
        this.type = type;
        this.body = body;
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitFuncDecl(this); 
    } 
}
