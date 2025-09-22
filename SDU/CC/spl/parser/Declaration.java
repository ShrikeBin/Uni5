package spl.parser;

public abstract class Declaration 
{
    public abstract <T> T accept(Visitor<T> visitor);
}