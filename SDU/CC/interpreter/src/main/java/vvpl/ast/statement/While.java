package vvpl.ast.statement;

import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;
import vvpl.ast.*;

public final class While extends Statement
{
    public final Token keyword;

    public final Expression condition;
    public final Statement body;

    public While(Token keyword, Expression condition, Statement body) 
    { 
        this.keyword = keyword;
        this.condition = condition; this.body = body; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitWhileStmt(this); 
    }
}
