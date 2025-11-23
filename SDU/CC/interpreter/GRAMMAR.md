# GRAMMAR.md

program       := decl* EOF ;

decl          := funcDecl
                | varDecl
                | statement ;

varDecl       := "variable" ID "has_type" type ("is" expr)? ";" ;

funcDecl      := "function" function ;

type          := "Number" | "String" | "Bool" ;

function      := ID "(" params? ")" ("has_type" type)? block ;

params        := ID "has_type" type ("," ID "has_type" type)* ;

statement     := exprStmt
                | ifStmt
                | whileStmt
                | printStmt
                | returnStmt
                | block ;

exprStmt      := expr ";" ;

ifStmt        := "if" "(" expr ")" statement ("else" statement)? ;

whileStmt     := "loop_while" "(" expr ")" statement ;

printStmt     := "write_to_console" expr ";" ;

returnStmt    := "return" expr? ";" ;

block         := "{" decl* "}" ;

expr          := assignment ;

assignment    := ID "is" assignment
                | logicOr ;

logicOr       := logicAnd ("OR" logicAnd)* ;

logicAnd      := equality ("AND" equality)* ;

equality      := compr (("NOT_EQUALS" | "EQUALS") compr)* ;

compr         := term (("GREATER" | "GREATER_EQUAL" | "LESS" | "LESS_EQUAL") term)* ;

term          := operator "(" term term ")" | unary ;

operator      := "add" | "subtract" | "divide" | "multiply" ;

unary         := ("NOT" | "-") unary | call ;

call          := primary ("(" args? ")")? ;

args          := expr ("," expr)* ;

primary       := ("cast_to" type)? ("true" | "false" | NUMBER | STRING | ID | "(" expr ")") ;
