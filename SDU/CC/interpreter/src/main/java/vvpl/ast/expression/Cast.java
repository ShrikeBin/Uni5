package vvpl.ast.expression;

import vvpl.ast.Expression;
import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;

public class Cast extends Expression {
    public final Token type;
    public final Expression value;

    public Cast(Token type, Expression value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitCastExpr(this);
    }
}
