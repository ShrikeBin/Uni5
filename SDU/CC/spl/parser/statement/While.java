package spl.parser.statement;

import spl.parser.*;

public final class While extends Statement 
{
    public final Expression condition;
    public final Statement body;

    public While(Expression condition, Statement body) 
    { 
        this.condition = condition; this.body = body; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitWhileStmt(this); 
    }
}