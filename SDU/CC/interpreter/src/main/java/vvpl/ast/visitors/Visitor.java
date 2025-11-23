package vvpl.ast.visitors;

import vvpl.ast.statement.*;
import vvpl.ast.expression.*;
import vvpl.ast.variable.*;
import vvpl.ast.function.*;

/**
 * @author Jan Ryszkiewicz
 * @version CompilerConstruction FT 2025
 */

/*
 * JUST SO YOU KNOW
 * THIS PROBALBY NEEDS A MAJOR OVERHAUL
 * LIKE MAKING VISITOR AN ABSTRACT CLASS
 * AND THEN HAVING DIFFERENT VISITORS EXTEND IT
 * FOR LIKE STATEMENTS, EXPRESSIONS, DECLARATIONS, ETC
 */

 /*
  * Example of it would be:

    public abstract class Visitor<T>
    { 
        public abstract T visit(Declaration decl);
    }

    public abstract class WhileVisitor<T> implements Visitor<T>
    {
        @Override
        public T visit(Declaration decl) { ... }
    }

  */
public interface Visitor<T> 
{
    // Expression Visitors
    T visitAssignExpr(Assignment expr);
    T visitLogicalExpr(Logical expr);
    T visitUnaryExpr(Unary expr);
    T visitCallExpr(Call expr);
    T visitBinaryExpr(Binary expr);
    T visitLiteralExpr(Literal expr);
    T visitVariableExpr(Variable expr);
    T visitCastExpr(Cast expr);

    // Statement Visitors
    T visitExprStmt(Expr stmt);
    T visitPrintStmt(Print stmt);
    T visitBlockStmt(Block stmt);
    T visitIfStmt(If stmt);
    T visitWhileStmt(While stmt);
    T visitVarDecl(VarDecl decl);

    // Function Visitors
    T visitFuncDecl(FuncDecl decl);
    T visitParamDecl(Param decl);
    T visitReturnStmt(Return stmt);
}