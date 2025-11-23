package vvpl.semantics;

import java.util.HashMap;
import java.util.Map;

import vvpl.semantics.symbol.Symbol;
import vvpl.semantics.symbol.SymbolKind;

public class SymbolTable 
{
    private final ScopeKind kind;
    private final SymbolTable parent;
    private final Map<String, Symbol> scope = new HashMap<>();

    public SymbolTable(ScopeKind kind, SymbolTable parent) {
        this.kind = kind;
        this.parent = parent;
    }

    public void add(String name, Symbol symbol) {
        scope.put(name, symbol);
    }

    public Symbol get(String name) {
        Symbol symbol = scope.get(name);
        if (symbol == null && parent != null) {
            symbol = parent.get(name);

            if (symbol == null || kind == ScopeKind.FUNCTION && symbol.kind == SymbolKind.VARIABLE) {
                return null; // functions cannot access global variables
            }
        }
        return symbol;
    }

    public SymbolTable newScope(ScopeKind kind) {
        return new SymbolTable(kind, this);
    }

    public SymbolTable endScope() {
        return parent;
    }

    public ScopeKind kind() {
        return kind != ScopeKind.BLOCK ? kind : parent.kind();
    }

    public ScopeKind level() {
        return kind;
    }
}
