package vvpl.ast.statement;

import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;
import vvpl.ast.*;

public final class Return extends Statement
{
    public final Token keyword;
    public final Expression value;

    public Return(Token keyword, Expression value) 
    { 
        this.keyword = keyword;
        this.value = value; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitReturnStmt(this); 
    }
}
