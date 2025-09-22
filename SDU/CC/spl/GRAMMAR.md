program       := declaration* EOF ;

declaration   := funDecl
               | statement ;

varDecl       := "var" IDENTIFIER ( "=" expression )? ";" ;

funDecl       := "fun" function ;

function      := IDENTIFIER "(" parameters? ")" "{" (statement)* | return "}" ;

parameters    := IDENTIFIER ( "," IDENTIFIER )* ;

statement     := exprStmt
               | whileStmt
               | ifStmt
               | printStmt
               | varDecl
               | block ;

exprStmt      := expression ";" ;

ifStmt        := "if" "(" expression ")" statement ( "else" statement )? ;

printStmt     := "print" expression ";" ;

whileStmt     := "while" "(" expression ")" statement ;

block         := "{" declaration* "}" ;

return        := "return" primary? ";" ;

expression    := assignment ;

assignment    := IDENTIFIER "=" assignment
               | logic_or ;

logic_or      := logic_and ( "or" logic_and )* ;

logic_and     := equality ( "and" equality )* ;

equality      := comparison ( ( "!=" | "==" ) comparison )* ;

comparison    := term ( ( ">" | ">=" | "<" | "<=" ) term )* ;

term          := factor ( ( "-" | "+" ) factor )* ;

factor        := unary ( ( "/" | "*" ) unary )* ;

unary         := ( "!" | "-" ) unary | call | primary ;

call          := IDENTIFIER ( "(" arguments? ")" )* ;   -- FORBID USAGE OF "()" when naming variables!!!

arguments     := expression ( "," expression )* ;

primary       := "true"
               | "false"
               | NUMBER
               | STRING
               | IDENTIFIER
               | call
               | "(" expression ")" ;

