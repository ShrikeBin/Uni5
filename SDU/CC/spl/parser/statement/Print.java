package spl.parser.statement;

import spl.parser.*;

public final class Print extends Statement 
{
    public final Expression expression;

    public Print(Expression expression) 
    { 
        this.expression = expression; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitPrintStmt(this); 
    }
}