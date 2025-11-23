package vvpl.ast;

import vvpl.ast.visitors.Visitor;

public abstract class Expression extends Statement
{
    public abstract <T> T accept(Visitor<T> visitor);
}
