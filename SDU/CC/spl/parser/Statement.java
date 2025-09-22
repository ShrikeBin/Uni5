package spl.parser;

public abstract class Statement extends Declaration 
{
    public abstract <T> T accept(Visitor<T> visitor);
}

