package spl.parser;

import spl.parser.statement.*;
import spl.parser.expression.*;

public interface Visitor<T> 
{
    // Expression Visitors
    T visitAssignExpr(Assign expr);
    T visitBinaryExpr(Binary expr);
    T visitLogicalExpr(Logical expr);
    T visitUnaryExpr(Unary expr);
    T visitLiteralExpr(Literal expr);
    T visitVariableExpr(Variable expr);

    // Statement Visitors
    T visitPrintStmt(Print stmt);
    T visitBlockStmt(Block stmt);
    T visitIfStmt(If stmt);
    T visitWhileStmt(While stmt);
    T visitVarDecl(VarDecl decl);

    // Function Visitors
    T visitFunDecl(FunDecl decl);
    T visitReturnStmt(Return stmt);
}