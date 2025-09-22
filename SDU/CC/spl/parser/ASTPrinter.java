package spl.parser;

import java.util.List;

import spl.scanner.Token;
import spl.parser.statement.*;
import spl.parser.expression.*;

public class ASTPrinter implements Visitor<Void> 
{
    private final StringBuilder builder = new StringBuilder();
    private final StringBuilder prefix = new StringBuilder();

    public String print(List<Declaration> program) 
    {
        builder.append("P R O G R A M\n");
        for (int i = 0; i < program.size(); i++) 
        {
            printDeclaration(program.get(i), i == program.size() - 1);
        }
        return builder.toString();
    }

    @Override
    public Void visitFunDecl(FunDecl fun) 
    {
        builder.append("FUN\n");
        printToken(fun.name, false);

        for (Token param : fun.params) 
        {
            printToken(param, false);
        }

        for (Statement stmt : fun.body) 
        {
            printDeclaration(stmt, false);
        }

        fun.result.accept(this);

        return null;
    }

    @Override
    public Void visitReturnStmt(Return ret) 
    {
        builder.append("RETURN\n");
        if (ret.value != null) 
        {
            printDeclaration(ret.value, true);
        } 
        else 
        {
            builder.append(prefix)
                   .append("└── ")
                   .append("void\n");
        }
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl decl) 
    {
        builder.append("VAR\n");
        if (decl.initializer != null) 
        {
            printToken(decl.name, false);
            printDeclaration(decl.initializer, true);
        } 
        else 
        {
            printToken(decl.name, true);
        }
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) 
    {
        builder.append("IF\n");
        printDeclaration(stmt.condition, false);
        if (stmt.elseBranch != null) 
        {
            printDeclaration(stmt.thenBranch, false);
            printDeclaration(stmt.elseBranch, true);
        } 
        else 
        {
            printDeclaration(stmt.thenBranch, true);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) 
    {
        builder.append("PRINT\n");
        printDeclaration(stmt.expression, true);
        return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) 
    {
        builder.append("BLOCK\n");
        for (int i = 0; i < stmt.statements.size(); i++) 
        {
            printDeclaration(stmt.statements.get(i), i == stmt.statements.size() - 1);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) 
    {
        builder.append("WHILE\n");
        printDeclaration(stmt.condition, false);
        printDeclaration(stmt.body, true);
        return null;
    }

    @Override
    public Void visitAssignExpr(Assign expr) 
    {
        builder.append("ASSIGN\n");
        printToken(expr.name, false);
        printDeclaration(expr.value, true);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Binary expr) 
    {
        builder.append("BINARY\n");
        printDeclaration(expr.left, false);
        printToken(expr.operator, false);
        printDeclaration(expr.right, true);
        return null;
    }

    @Override
    public Void visitLogicalExpr(Logical expr) 
    {
        builder.append("LOGICAL\n");
        printDeclaration(expr.left, false);
        printToken(expr.operator, false);
        printDeclaration(expr.right, true);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Unary expr) 
    {
        builder.append("UNARY\n");
        printToken(expr.operator, false);
        printDeclaration(expr.right, true);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Literal expr) 
    {
        builder.append(expr.value.lexeme)
               .append("\n");
        return null;
    }

    @Override
    public Void visitVariableExpr(Variable expr) 
    {
        builder.append(expr.name.lexeme)
               .append("\n");
        return null;
    }

    private void printToken(Token token, boolean isLast) 
    {
        builder.append(prefix)
               .append(isLast ? "└── " : "├── ")
               .append(token.lexeme)
               .append("\n");
    }

    private void printDeclaration(Declaration decl, boolean isLast) 
    {
        builder.append(prefix);
        
        if (isLast) 
        {
            builder.append("└── ");
            prefix.append("    ");
        } 
        else 
        {
            builder.append("├── ");
            prefix.append("│   ");
        }

        decl.accept(this);
        prefix.setLength(prefix.length() - 4);
    }

    // private void printFunction(FunBlock body, Return result, boolean isLast) 
    // {
    //     builder.append("FUNCTION BLOCK\n");
    //     for (int i = 0; i < body.statements.size(); i++) 
    //     {
    //         printDeclaration(body.statements.get(i), i == body.statements.size() - 1);
    //     }

    //     result.accept(this);
    // }

}