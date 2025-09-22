package spl.parser;

public interface Visitor<T> 
{
    // Expression Visitors
    T visitAssignExpr(Expression.Assign expr);
    T visitBinaryExpr(Expression.Binary expr);
    T visitLogicalExpr(Expression.Logical expr);
    T visitUnaryExpr(Expression.Unary expr);
    T visitLiteralExpr(Expression.Literal expr);
    T visitVariableExpr(Expression.Variable expr);

    // Statement Visitors
    T visitPrintStmt(Statement.Print stmt);
    T visitBlockStmt(Statement.Block stmt);
    T visitIfStmt(Statement.If stmt);
    T visitWhileStmt(Statement.While stmt);

    // Declaration Visitors
    T visitVarDecl(Var decl);
}