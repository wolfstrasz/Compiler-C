package ast;

// Typecast expression : (Type)Expr (e.g. (int*) malloc(4))
// TypecastExpr ::= Type Expr
// --------------------------------------------------------
public class TypecastExpr extends Expr {

    public final Type type;
    public final Expr expr;

    public TypecastExpr (Type type, Expr expr){
        this.type = type;
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitTypecastExpr(this);
    }
}