package vvpl.semantics.symbol;

import java.util.HashMap;
import java.util.Map;

import vvpl.scan.TokenType;

public enum SymbolType {
    NUMBER,
    STRING,
    BOOL,
    VOID;

    public static final Map<TokenType, SymbolType> map = new HashMap<>();
    static {
        map.put(TokenType.NUMBER_TYPE, NUMBER);
        map.put(TokenType.STRING_TYPE, STRING);
        map.put(TokenType.BOOL_TYPE, BOOL);
        map.put(TokenType.NUMBER, NUMBER);
        map.put(TokenType.STRING, STRING);
        map.put(TokenType.TRUE, BOOL);
        map.put(TokenType.FALSE, BOOL);
    }
}
