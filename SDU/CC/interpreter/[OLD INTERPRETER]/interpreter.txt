package vvpl.interpret;

import java.util.List;
import java.util.ArrayList;

import vvpl.ast.Declaration;
import vvpl.ast.Statement;
import vvpl.ast.Expression;

import vvpl.ast.expression.*;
import vvpl.ast.function.*;
import vvpl.ast.statement.*;
import vvpl.ast.variable.*;
import vvpl.ast.visitors.Visitor;

import vvpl.errors.*;

import vvpl.scan.Token;
import vvpl.scan.TokenType;

public class Interpreter implements Visitor<Object>
{
    boolean allowNestedFunctions = false;
    boolean allowVariableRedeclaration = false;

    public Environment env = new Environment(null);
    public Boolean inFunction = false;

    public void interpret(List<Declaration> program) throws SyntaxError, ScopeError, TypeError
    {
        // ===== Global Function Scope =====
        if(!inFunction)
        {
            for (Declaration decl : program) 
            {
                if (decl instanceof FuncDecl) 
                {
                    FuncDecl funcDecl = (FuncDecl) decl;
                    if(funcDecl.type == null)
                    {
                        Function function = new Function(funcDecl.name.lexeme, funcDecl.params, "void", funcDecl.body);
                        env.put(funcDecl.name.lexeme, function);                    
                    }
                    else
                    {
                        Function function = new Function(funcDecl.name.lexeme, funcDecl.params, funcDecl.type.lexeme, funcDecl.body);
                        env.put(funcDecl.name.lexeme, function);
                    }
                }
            }
        }

        // ===== Actuall Interpretation =====
        for(Declaration decl : program)
        {
            if (decl instanceof FuncDecl) 
            {
                // global functions are already handled
                continue;
            }
            decl.accept(this);
        }
    }

    private Object evaluate(Expression expr) 
    {
        return expr.accept(this);
    }

    private void execute(Statement stmt) 
    {
        stmt.accept(this);
    }

    @Override
    public Void visitFuncDecl(FuncDecl funcDecl)
    {
        if(!allowNestedFunctions)
        {
            throw new SyntaxError("We only allow global function declarations " + funcDecl.name.lexeme + " is defined in scope");
        }
        else
        {
            if(funcDecl.type == null)
            {
                Function function = new Function(funcDecl.name.lexeme, funcDecl.params, "void", funcDecl.body);
                env.put(funcDecl.name.lexeme, function);                    
            }
            else
            {
                Function function = new Function(funcDecl.name.lexeme, funcDecl.params, funcDecl.type.lexeme, funcDecl.body);
                env.put(funcDecl.name.lexeme, function);
            }
            return null;
        }
    }

    @Override
    public Void visitVarDecl(VarDecl decl)
    {
        Object value = null;
        if(!allowVariableRedeclaration && (env.get(decl.name.lexeme) != null))
        {
            throw new ScopeError("Variable '" + decl.name.lexeme + "' is already defined in the current scope.");
        }
        if (decl.initializer != null) 
        {
            value = evaluate(decl.initializer);
            String type = getTypeString(decl.type.type);
            if (!typeMatch(value, type)) 
            {
                throw new TypeError("Type mismatch for variable '" +
                                    decl.name.lexeme + "': expected " +
                                    type + ", got " +
                                    getName(value));
            }
        } 
        else 
        {
            throw new SyntaxError("Uninitialized variable: " + decl.name.lexeme);
        }

        env.put(decl.name.lexeme, value);

        return null;
    }

    @Override
    public Object visitAssignExpr(Assignment expr) 
    { 
        Object value = evaluate(expr.value);
        String name = expr.ID.lexeme;

        Object variable = env.get(name);
        if(variable == null)
        {
            throw new RuntimeError("variable '" + name + "' does not exist");
        }
        if(variable instanceof Function)
        {
            throw new SyntaxError("Cannot assign value to function: " + name);
        }
        if(!typeMatch(value, getName(value)))
        {
            throw new TypeError("Type mismatch in assignment to '" + name + 
                "': expected " + getName(variable) +
                ", got " + getName(value));
        }
        env.set(name, value);

        return value;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) 
    { 
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        if (left == null || right == null)
            throw new RuntimeError("Something evaluated to null in Logical expression null");

        if (!left.getClass().equals(right.getClass())) 
        {
            throw new SyntaxError("Type mismatch in logical expression: l: " 
                + getName(left) +
                ", r: " + getName(right));
        }

        if(left instanceof Boolean)
        {
            switch (expr.operator.type) 
            {
                case AND:
                    return (Boolean)left && (Boolean)right;
                case OR:
                    return (Boolean)left || (Boolean)right;
                default:
                    throw new SyntaxError("Unknown/Invalid logical operator for Booleans: " + expr.operator.lexeme);
            }
        }
        else
        {
            throw new SyntaxError("Unsupported type for logical expression: " 
                + getName(left));
        }
    }

    @Override
    public Object visitUnaryExpr(Unary expr) 
    { 
        Object right = evaluate(expr.right);
        switch(expr.operator.type)
        {
            case NOT:
                if(!(right instanceof Boolean))
                {
                    throw new SyntaxError("Logical NOT requires a boolean operand.");
                }
                return !(Boolean)right;
            case MINUS:
                if(!(right instanceof Double))
                {
                    throw new SyntaxError("Unary minus requires a numeric operand.");
                }
                else
                {
                    return - (Double)right;
                }
            default:
                throw new SyntaxError("Unknown unary operator: " + expr.operator.lexeme);
        }
    }

    @Override
    public Object visitCallExpr(Call expr) 
    { 
        String ID = expr.ID.lexeme;
        Object callee = env.get(ID);

        if(callee instanceof Function)
        {
            Function function = (Function) callee;
            List<Object> arguments = new ArrayList<>();

            for(Expression arg : expr.args)
            {   
                arguments.add(evaluate(arg));
            }

            Object result = function.call(this, arguments);
            return result;
        }
        else if (callee == null) 
        {
            throw new ScopeError("Undefined function name: " + ID);    
        }
        else
        {
            throw new SyntaxError("Attempted to call a non-function: " + ID);
        }
    }

    @Override
    public Object visitBinaryExpr(Binary expr) 
    { 
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        if (left == null || right == null)
        {
            throw new RuntimeError("Something evaluated to null in Binary expression");
        }
        if (!left.getClass().equals(right.getClass())) 
        {
            throw new SyntaxError("Type mismatch in binary expression: l: " 
                + getName(left) +
                ", r: " + getName(right));
        }
        
        if(left instanceof Boolean)
        {
            switch (expr.operator.type) 
            {
                case EQUALS:
                    return left.equals(right);
                case NOT_EQUALS:
                    return !left.equals(right);
                default:
                    throw new SyntaxError("Unknown/Invalid logical operator for Booleans: " + expr.operator.lexeme);
            }
        }
        else if (left instanceof Double)
        {
            switch (expr.operator.type) 
            {
                case EQUALS:
                    return left.equals(right);
                case NOT_EQUALS:
                    return !left.equals(right);
                case GREATER:
                    return (Double)left > (Double)right;
                case GREATER_EQUAL:
                    return (Double)left >= (Double)right;
                case LESS:
                    return (Double)left < (Double)right;
                case LESS_EQUAL:
                    return (Double)left <= (Double)right;
                case ADD:
                    return (Double)left + (Double)right;
                case SUB:
                    return (Double)left - (Double)right;
                case MULT:  
                    return (Double)left * (Double)right;
                case DIV:
                    if ((Double)right == 0) 
                    {
                        throw new SyntaxError("Division by zero idiot.");
                    }
                    return (Double)left / (Double)right;
                default:
                    throw new SyntaxError("Unknown binary operator for numbers: " + expr.operator.lexeme);
            }
        }
        else
        {
            throw new SyntaxError("Unsupported type for binary expression: " 
                + getName(left));
        }
    }

    @Override
    public Object visitLiteralExpr(Literal expr) 
    { 
        if(expr.value.type == TokenType.TRUE) return true;
        if(expr.value.type == TokenType.FALSE) return false;
        return expr.value.literal; 
    }

    @Override
    public Object visitVariableExpr(Variable expr) 
    { 
        Object var = env.get(expr.name.lexeme);
        if(var == null)
        {
            throw new ScopeError("Variable not defined: " + expr.name.lexeme);
        }

        return env.get(expr.name.lexeme);
    }

    @Override
    public Object visitCastExpr(Cast expr) 
    { 
        Object casted = evaluate(expr.value);
        String targetType = expr.type.lexeme.toLowerCase();
        if(casted == null)
        {
            throw new RuntimeError("Cast evaluated to null");
        }
        String castedType = getName(casted);

        if(castedType.equals("integer")||castedType.equals("double"))
        {
            castedType = "number";
        }

        if(castedType.equals(targetType))
        {
            return casted;
        }

        // ==== Valid casts ====
        // • Number → String
        // • String → Number (if the String is a valid number)
        // • Number → Boolean (every number other than 13 is false, 13 is true)
        // • Boolean → String
        
        if(castedType.equals("number"))
        {
            if(targetType.equals("string"))
            {
                return casted.toString();
            }
            else if(targetType.equals("bool"))
            {
                return ((Double)casted == 13.0);
            }
            else
            {
                throw new TypeError("Invalid cast from Number to " + targetType);
            }
        }
        else if(castedType.equals("string"))
        {
            if(targetType.equals("number"))
            {
                try
                {
                    return Double.parseDouble((String)casted);
                }
                catch(NumberFormatException e)
                {
                    throw new TypeError("Invalid cast from String to Number: " + casted);
                }
            }
            else
            {
                throw new TypeError("Invalid cast from String to " + targetType);
            }
        }
        else if(castedType.equals("bool"))
        {
            if(targetType.equals("string"))
            {
                return casted.toString();
            }
            else
            {
                throw new TypeError("Invalid cast from String to " + targetType);
            }
        }
        else
        {
            throw new SyntaxError("Unsupported cast from " + castedType + " to " + targetType);
        }
    }

    @Override
    public Void visitExprStmt(Expr stmt) 
    { 
        evaluate(stmt.expr);
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) 
    { 
        Object prinObject = evaluate(stmt.expression);
        if(prinObject == null)
        {
            System.out.println("null");
        }
        else if(prinObject instanceof String)
        {
            System.out.println((String)prinObject);
        }
        else
        {
            //System.out.println(prinObject);
            throw new SyntaxError("Print statement can only print strings. (or nulls)");
        }
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) 
    { 
        Object condition = evaluate(stmt.condition);
        if(!(condition instanceof Boolean))
        {
            throw new SyntaxError("Condition must be a Boolean");
        }
        if((Boolean)condition)
        {
            execute(stmt.thenBranch);
        }
        else
        {
            if(stmt.elseBranch != null)
            {
                execute(stmt.elseBranch);
            }
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) 
    { 
        while (true)
        {
            Object condition = evaluate(stmt.condition);
            if(!(condition instanceof Boolean))
            {
                throw new SyntaxError("Condition must be a Boolean");
            }
            if(!(Boolean)condition)
            {
                break;
            }
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) 
    { 
        Environment previousEnv = this.env;
        this.env = new Environment(previousEnv);
        for(Declaration decl : stmt.statements)
        {
            decl.accept(this);
        }

        this.env = previousEnv;
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) 
    { 
        if(!inFunction)
        {
            throw new SyntaxError("Return statements can only be used inside functions.");
        }

        throw new Returnable(evaluate(stmt.value)); 
    }

    // ==== Those are deemed unneccessary and  ====
    // ==== are sentenced to return null; jail ====

    @Override
    public Void visitParamDecl(Param decl) 
    { 
        return null; 
    }

    private String getTypeString(TokenType type)
    {
        switch(type)
        {
            case NUMBER_TYPE:
                return "number";
            case STRING_TYPE:
                return "string";
            case BOOL_TYPE:
                return "boolean";
            case FUNCTION:
                return "function";
            default:
                return "unknown";
        }
    }

    private String getName(Object value)
    {
        if (value == null)
        {
            return "null";
        }
        return value.getClass().getSimpleName().toLowerCase();
    }

    private boolean typeMatch(Object value, String expectedType) 
    {
        if (value == null) return false;

        switch (expectedType.toLowerCase()) 
        {
            case "number":
                return value instanceof Double || value instanceof Integer;
            case "integer":
                return value instanceof Integer;
            case "double":
                return value instanceof Double;
            case "string":
                return value instanceof String;
            case "boolean":
                return value instanceof Boolean;
            case "function":
                return value instanceof Function;
            case "void":
                return value == null;
            default:
                return false;
        }
    }
}
