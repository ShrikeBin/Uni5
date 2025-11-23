package vvpl.ast;

import vvpl.ast.visitors.Visitor;

public abstract class Statement extends Declaration
{
    public abstract <T> T accept(Visitor<T> visitor);
}
