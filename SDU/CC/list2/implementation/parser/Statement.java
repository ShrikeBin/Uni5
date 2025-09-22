package spl.parser;

import java.util.List;

public abstract class Statement extends Declaration 
{
    public static final class Print extends Statement 
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

    public static final class Block extends Statement 
    {
        public final List<Declaration> statements;

        public Block(List<Declaration> statements) 
        { 
            this.statements = statements; 
        }

        public <T> T accept(Visitor<T> visitor) 
        { 
            return visitor.visitBlockStmt(this); 
        }
    }

    public static final class If extends Statement
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

    public static final class While extends Statement 
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
}

