package vvpl.semantics;

import java.util.*;
import vvpl.ast.*;
import vvpl.ast.expression.*;
import vvpl.ast.function.*;
import vvpl.ast.statement.*;
import vvpl.ast.variable.*;
import vvpl.ast.visitors.Visitor;
import vvpl.errors.*;
import vvpl.scan.*;
import vvpl.semantics.symbol.*;

/*
Scope-checks and then (if successful) type-checks each statement and expression
 */

// TODO HANDLE VOIDS

public class Canary implements Visitor<SymbolType> {
    private SymbolTable scope = new SymbolTable(ScopeKind.GLOBAL, null);
    private List<Declaration> program;

    public Canary(List<Declaration> program) {
        this.program = program;
    }

    public void check() {

        // First go - out of order global function declarations
        for (Declaration decl : program) {
            if (decl instanceof FuncDecl f){
                if (scope.get(f.name.lexeme) != null) {
                    scopeError(f.name, "Function already defined.");
                    continue; // keep on checking using the first definition
                }
                List<SymbolType> paramTypes = f.params.stream()
                    .map(param -> SymbolType.map.get(param.type.type))
                    .toList();
                SymbolType returnType = f.type != null ? SymbolType.map.get(f.type.type) : SymbolType.VOID;
                scope.add(f.name.lexeme, new Symbol(SymbolKind.FUNCTION, returnType, paramTypes));
            }
        }

        // Second go - scope checks and type checks
        for (Declaration decl : program) {
            decl.accept(this);
        }
    }

    @Override
    public SymbolType visitFuncDecl(FuncDecl func) {
        if (scope.level() != ScopeKind.GLOBAL) {
            scopeError(func.name, "Cannot define a function in a non-global scope.");
        } // keep checking

        scope = scope.newScope(ScopeKind.FUNCTION);

        SymbolType returnType = func.type != null ? SymbolType.map.get(func.type.type) : SymbolType.VOID;
        scope.add("$", new Symbol(SymbolKind.RETURN_PLACEHOLDER, returnType));

        for (Param param : func.params) {
            if (scope.get(param.name.lexeme) != null) { // allows for shadowing globals because of get() definition
                scopeError(param.name, "Two parameters with the same name.");
            }
            scope.add(param.name.lexeme, new Symbol(SymbolKind.VARIABLE, SymbolType.map.get(param.type.type)));
        }

        func.body.accept(this);

        scope = scope.endScope();
        return null;
    }

    @Override
    public SymbolType visitVarDecl(VarDecl decl) {
        Boolean defined = scope.get(decl.name.lexeme) != null;
        SymbolType variableType = SymbolType.map.get(decl.type.type);

        if (defined) { // doesn't allow shadowing
            scopeError(decl.name, "Redeclaration of variable.");
        } // keep checking
        
        if (decl.initializer == null) {
            typeError(decl.name, "No initializer.");
            return null; // nothing to check
        }
        
        SymbolType initializerType = decl.initializer.accept(this);
        if (initializerType == null) {
            return null; // something went wrong in initializer check
        }
        
        if (initializerType != variableType) {
            typeError(decl.name, "Cannot assign " + initializerType.toString() + " to " + variableType.toString() + ".");
            return null; // incompatible types
        }
        
        if (!defined) { // don't redefine
            scope.add(decl.name.lexeme, new Symbol(SymbolKind.VARIABLE, variableType));
        }
        return null;
    }

    @Override
    public SymbolType visitBinaryExpr(Binary expr) {
        SymbolType left = expr.left.accept(this);
        SymbolType right = expr.right.accept(this);

        if (left == null || right == null) {
            return null; // something went wrong in operands check
        }

        switch (expr.operator.type) {
            case TokenType.ADD:
            case TokenType.SUB:
            case TokenType.MULT:
            case TokenType.DIV:
                if (left != SymbolType.NUMBER || right != SymbolType.NUMBER) {
                    typeError(expr.operator, "Operands must be both numbers. Got: " + left.toString() + " and " + right.toString() + ".");
                    return null;
                }
                return SymbolType.NUMBER;

            case TokenType.LESS:
            case TokenType.GREATER:
            case TokenType.LESS_EQUAL:
            case TokenType.GREATER_EQUAL:
                if (left != SymbolType.NUMBER || right != SymbolType.NUMBER) {
                    typeError(expr.operator, "Operands must be both numbers. Got: " + left.toString() + " and " + right.toString() + ".");
                    return null;
                }
                return SymbolType.BOOL;

            case TokenType.EQUALS:
            case TokenType.NOT_EQUALS:
                if (
                    !(left == SymbolType.NUMBER && right == SymbolType.NUMBER) &&
                    !(left == SymbolType.BOOL && right == SymbolType.BOOL)
                ) {
                    typeError(expr.operator, "Operands must be both numbers or booleans. Got: " + left.toString() + " and " + right.toString() + ".");
                    return null;
                }
                return SymbolType.BOOL;
            default:
                return null;
        }
    }

    @Override
    public SymbolType visitLiteralExpr(Literal expr) {
        return SymbolType.map.get(expr.value.type);
    }

    @Override
    public SymbolType visitVariableExpr(Variable expr) {
        Symbol var = scope.get(expr.name.lexeme);

        if (var == null) {
            scopeError(expr.name, "Variable not defined.");
            return null;
        }

        if (var.kind != SymbolKind.VARIABLE) {
            typeError(expr.name, expr.name.lexeme + " is not a variable.");
            return null;
        }

        return var.type;
    }

    @Override
    public SymbolType visitAssignExpr(Assignment expr) {
        Symbol var = scope.get(expr.ID.lexeme);

        if (var == null) {
            scopeError(expr.ID, "Variable not defined.");
        } // keep checking

        if (var != null && var.kind != SymbolKind.VARIABLE) {
            typeError(expr.ID, expr.ID.lexeme + " is not a variable.");
            var = null; // as good as undefined
        } // keep checking

        SymbolType valueType = expr.value.accept(this);
        if (valueType == null) {
            return null; // something went wrong in value check
        }

        if (var == null) {
            return null; // nothing to compare value type to
        }

        if (valueType != var.type) {
            typeError(expr.ID, "Cannot assign " + valueType.toString() + " to " + var.type.toString() + ".");
            return null; // incompatible types
        }
        return var.type;
    }

    @Override
    public SymbolType visitLogicalExpr(Logical expr) {
        SymbolType left = expr.left.accept(this);
        SymbolType right = expr.right.accept(this);
        
        if (left == null || right == null) {
            return null; // something went wrong in operands check
        }
        
        if (left != SymbolType.BOOL || right != SymbolType.BOOL) {
            typeError(expr.operator, "Operands must be both booleans. Got: " + left.toString() + " and " + right.toString() + ".");
            return null;
        }
        
        return SymbolType.BOOL;
    }

    @Override
    public SymbolType visitIfStmt(If stmt) {
        SymbolType condType = stmt.condition.accept(this);
        if (condType != null && condType != SymbolType.BOOL) {
            typeError(stmt.keyword, "Condition should be a boolean. Got: " + condType.toString() + ".");
        }
        stmt.thenBranch.accept(this);
        if (stmt.elseBranch != null) {
            stmt.elseBranch.accept(this);
        }

        return null;
    }

    @Override
    public SymbolType visitWhileStmt(While stmt) {
        SymbolType condType = stmt.condition.accept(this);
        if (condType != null && condType != SymbolType.BOOL) {
            typeError(stmt.keyword, "Condition should be a boolean. Got: " + condType.toString() + ".");
        }
        stmt.body.accept(this);

        return null;
    }

    @Override
    public SymbolType visitBlockStmt(Block stmt) {
        scope = scope.newScope(ScopeKind.BLOCK);

        for (Declaration decl : stmt.statements) {
            decl.accept(this);

            if (decl instanceof Return ret && decl != stmt.statements.getLast()) {
                typeError(ret.keyword, "Unreachable code after return statement.");
            }
        }

        scope = scope.endScope();
        return null;
    }

    @Override
    public SymbolType visitReturnStmt(Return stmt) {
        if (scope.kind() != ScopeKind.FUNCTION) {
            typeError(stmt.keyword, "Return statement is not in a function.");
            return null;
        }

        SymbolType valueType = stmt.value != null ? stmt.value.accept(this) : SymbolType.VOID;
        if (valueType == null) {
            return null; // nothing to compare return type to
        }

        SymbolType returnType = scope.get("$").type; // return placeholder symbol
        if (valueType != returnType) {
            typeError(stmt.keyword, "Wrong return type. Expected: " + returnType.toString() + ", got: " + valueType.toString() + ".");
        }
        return null;
    }

    @Override 
    public SymbolType visitCallExpr(Call expr) { 
        Symbol function = scope.get(expr.ID.lexeme);

        if (function == null) {
            scopeError(expr.ID, "Function not defined");
        } // keep checking

        if (function != null && function.kind != SymbolKind.FUNCTION) {
            typeError(expr.ID, expr.ID.lexeme + " is not a function.");
        } // keep checking

        List<SymbolType> argTypes = expr.args.stream()
            .map(arg -> arg.accept(this)) // check args
            .toList();

        if (function == null || argTypes.contains(null)) {
            return null; // nothing to compare
        }

        if (!function.paramTypes.equals(argTypes)) {
            typeError(expr.ID, "Wrong arguments. Expected: " + function.paramTypes.toString() + ", got: " + argTypes.toString() + ".");
            return null; // arg types mismatch
        }

        return function.type;
    }

    @Override 
    public SymbolType visitUnaryExpr(Unary expr) { 
        SymbolType right = expr.right.accept(this);

        if (right == null) {
            return null; // something went wrong in operand check
        }

        switch (expr.operator.type) {
            case TokenType.NOT:
                if (right != SymbolType.BOOL) {
                    typeError(expr.operator, "Operand must be a boolean. Got: " + right.toString() + ".");
                    return null;
                }
                return SymbolType.BOOL;
            case TokenType.MINUS:
                if (right != SymbolType.NUMBER) {
                    typeError(expr.operator, "Operand must be a number. Got: " + right.toString() + ".");
                    return null;
                }
                return SymbolType.NUMBER;
            default:
                return null;
        }
    }

    @Override 
    public SymbolType visitCastExpr(Cast expr) { 
        SymbolType castedType = SymbolType.map.get(expr.type.type);
        SymbolType valueType = expr.value.accept(this);

        if (valueType == null) {
            return null; // something went wrong in value check
        }

        if (castedType == valueType) {
            return castedType; // can always cast between same types
        }

        if ( // allowed casts TODO: eval string? (its always hardcoded)
            (valueType == SymbolType.NUMBER && castedType == SymbolType.STRING) ||
            (valueType == SymbolType.BOOL   && castedType == SymbolType.STRING) ||
            (valueType == SymbolType.STRING && castedType == SymbolType.NUMBER) ||
            (valueType == SymbolType.NUMBER && castedType == SymbolType.BOOL)
        ) {
            return castedType; // pottentially castable, might fail at runtime
        }

        typeError(expr.type, "Cannot cast " + valueType.toString() + " as " + castedType.toString() + ".");

        return null;
    }

    @Override 
    public SymbolType visitExprStmt(Expr stmt) { 
        stmt.expr.accept(this); 
        return null; 
    }

    @Override 
    public SymbolType visitPrintStmt(Print stmt) {
        if (stmt.expression.accept(this) == SymbolType.VOID) {
            typeError(stmt.keyword, "Cannot print VOID type.");
        } // can print anything else
        return null; 
    }

    @Override 
    public SymbolType visitParamDecl(Param decl) {
        throw new UnsupportedOperationException("Type checking a parameter??");
    }

    private void scopeError(Token name, String message) {
        ErrorHandler.error(name.line, "Scope Error at '" + name.lexeme + "': " + message);
    }

    private void typeError(Token name, String message) {
        ErrorHandler.error(name.line, "Type Error at '" + name.lexeme + "': " + message);
    }
}
