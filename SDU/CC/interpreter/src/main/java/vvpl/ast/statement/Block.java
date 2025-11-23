package vvpl.ast.statement;

import java.util.List;

import vvpl.ast.visitors.Visitor;
import vvpl.ast.*;

public final class Block extends Statement
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
