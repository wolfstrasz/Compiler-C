package ast;

// Expressions
// Expr       ::= IntLiteral | StrLiteral | ChrLiteral | VarExpr | FunCallExpr |
//                BinOp | ArrayAccessExpr | FieldAccessExpr | ValueAtExpr | SizeOfExpr | TypecastExpr
// ---------------------------------------------------------------------------------------------------
public abstract class Expr implements ASTNode {

    public Type type; // to be filled in by the type analyser
    public abstract <T> T accept(ASTVisitor<T> v);
}
