package vvpl.ast.visitors;

import vvpl.ast.Declaration;
import vvpl.ast.expression.Literal;
import vvpl.scan.Token;

public class BoringASTPrinter extends ASTPrinter {
    public String print(Declaration decl) {
        builder.setLength(0);
        decl.accept(this);
        return builder.toString();
    }

    @Override
    protected void printToken(Token token, boolean isLast) {
        builder.append(prefix)
               .append("  ")
               .append(token.lexeme)
               .append("\n");
    }

    protected void printTokenLiteral(Token token, boolean isLast) {
        builder.append(prefix)
               .append("  ")
               .append(token.literal)
               .append("\n");
    }

    @Override
    public Void visitLiteralExpr(Literal expr) {
        builder.append("LiteralExpr\n");
        if (expr.value.literal instanceof Double) {
            printTokenLiteral(expr.value, true);
        } else {
            printToken(expr.value, true);
        }
        return null;
    }

    @Override
    protected void printDeclaration(Declaration decl, boolean isLast) {
        builder.append(prefix)
               .append("  ");
        prefix.append("  ");
        decl.accept(this);
        prefix.setLength(prefix.length() - 2);
    }
}
