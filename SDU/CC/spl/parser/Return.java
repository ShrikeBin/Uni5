package spl.parser;

public final class Return 
{
    public final Expression value;

    public Return(Expression value) 
    { 
        this.value = value; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitReturnStmt(this); 
    }
    
}
