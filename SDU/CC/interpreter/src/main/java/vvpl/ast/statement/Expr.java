package vvpl.ast.statement;

import vvpl.ast.Expression;
import vvpl.ast.Statement;
import vvpl.ast.visitors.Visitor;

public class Expr extends Statement {
    public final Expression expr;

    public Expr(Expression expr) {
        this.expr = expr;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitExprStmt(this);
    }
}
