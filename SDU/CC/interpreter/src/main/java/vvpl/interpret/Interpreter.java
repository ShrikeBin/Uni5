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

import vvpl.scan.TokenType;

public class Interpreter implements Visitor<Object>
{
    public Environment env = new Environment(null);
    public Boolean inFunction = false;

    public void interpret(List<Declaration> program) throws RuntimeError
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

    @Override
    public Void visitVarDecl(VarDecl decl)
    {
        Object value = evaluate(decl.initializer);
        env.put(decl.name.lexeme, value);

        return null;
    }

    @Override
    public Object visitAssignExpr(Assignment expr) 
    { 
        Object value = evaluate(expr.value);
        String name = expr.ID.lexeme;
        env.set(name, value);

        return value;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) 
    { 
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        if (left == null || right == null)
        {
            throw runtimeError(expr.operator.line, "Something evaluated to null in Logical expression null");
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
                    return null;
            }
        }
        return null;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) 
    { 
        Object right = evaluate(expr.right);
        switch(expr.operator.type)
        {
            case NOT:
                return !(Boolean)right;
            case MINUS:
                return - (Double)right;
            default:
                return null;
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
        return null;
    }

    @Override
    public Object visitBinaryExpr(Binary expr) 
    { 
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        if (left == null || right == null)
        {
            throw runtimeError(expr.operator.line, "Something evaluated to null in Binary expression");
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
                    return null;
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
                        throw runtimeError(expr.operator.line ,"Division by zero idiot.");
                    }
                    return (Double)left / (Double)right;
                default:
                    return null;
            }
        }
        return null;
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
        return env.get(expr.name.lexeme);
    }

    @Override
    public Object visitCastExpr(Cast expr) 
    { 
        Object casted = evaluate(expr.value);
        String targetType = expr.type.lexeme.toLowerCase();
        if(casted == null)
        {
            throw runtimeError(expr.type.line, "Cast evaluated to null");
        }
        String castedType = casted.getClass().getSimpleName().toLowerCase();

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
            return null;
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
                    return null;
                }
            }
            return null;
        }
        else if(castedType.equals("bool"))
        {
            if(targetType.equals("string"))
            {
                return casted.toString();
            }
            return null;
        }
        return null;
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
        else
        {
            System.out.println(prinObject.toString());
        }
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) 
    { 
        Object condition = evaluate(stmt.condition);
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
                throw runtimeError(stmt.keyword.line, "WHILE condition evaluated to non Boolean value");
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
            throw runtimeError(stmt.keyword.line,"Return statements can only be used inside functions.");
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

    private RuntimeError runtimeError(int line, String text)
    {
        ErrorHandler.error(line, text);
        return new RuntimeError(text);
    }
}
