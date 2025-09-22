package spl.parser.statement;

import spl.parser.*;
import spl.scanner.Token;

public final class If extends Statement
    {
    public final Expression condition; 
    public final Statement thenBranch; 
    public final Statement elseBranch;

    public If(Expression condition, Statement thenBranch, Statement elseBranch) 
    { 
        this.condition = condition; 
        this.thenBranch = thenBranch; 
        this.elseBranch = elseBranch; 
    }

    public <T> T accept(Visitor<T> visitor) 
    {
        return visitor.visitIfStmt(this); 
    }
}