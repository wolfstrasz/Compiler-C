package ast;

// Array access expression : Expr[Expr] (e.g. a[10])
// ArrayAccessExpr ::= Expr Expr // the first Expr is the array, the second one the index
// --------------------------------------------------------------------------------------
public class ArrayAccessExpr extends Expr {

    public final Expr array;
    public final Expr index;

    public ArrayAccessExpr (Expr array, Expr index){
        this.array = array;
        this.index = index;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayAccessExpr(this);
    }
}
