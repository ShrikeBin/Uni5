package spl.parser;

import spl.scanner.Token;

public abstract class Declaration 
{
    public abstract <T> T accept(Visitor<T> visitor);
}