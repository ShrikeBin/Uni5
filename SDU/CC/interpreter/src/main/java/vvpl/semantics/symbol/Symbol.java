package vvpl.semantics.symbol;

import java.util.List;

public class Symbol {
    public final SymbolKind kind;
    public final SymbolType type;

    public final List<SymbolType> paramTypes; // for function symbol

    public Symbol(SymbolKind kind, SymbolType type, List<SymbolType> paramTypes) {
        this.kind = kind;
        this.type = type;
        this.paramTypes = paramTypes;
    }

    public Symbol(SymbolKind kind, SymbolType type) {
        this(kind, type, null);
    }
}
