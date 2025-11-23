package vvpl.ast.statement;

import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;
import vvpl.ast.*;

public class Print extends Statement
{
    public final Token keyword;
    public final Expression expression;

    public Print(Token keyword, Expression expression) 
    { 
        this.keyword = keyword;
        this.expression = expression; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitPrintStmt(this); 
    }
}
