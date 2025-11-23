package vvpl.ast.statement;

import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;
import vvpl.ast.*;

public final class If extends Statement
{
    public final Token keyword;

    public final Expression condition; 
    public final Statement thenBranch; 
    public final Statement elseBranch;

    public If(Token keyword, Expression condition, Statement thenBranch, Statement elseBranch) 
    { 
        this.keyword = keyword;
        this.condition = condition; 
        this.thenBranch = thenBranch; 
        this.elseBranch = elseBranch; 
    }

    public <T> T accept(Visitor<T> visitor) 
    {
        return visitor.visitIfStmt(this); 
    }
}
