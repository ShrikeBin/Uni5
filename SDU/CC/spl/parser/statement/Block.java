package spl.parser.statement;

import java.util.List;

import spl.parser.*;

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