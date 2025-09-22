package spl.parser;

public abstract class Expression extends Statement 
{
    public abstract <T> T accept(Visitor<T> visitor);
}

