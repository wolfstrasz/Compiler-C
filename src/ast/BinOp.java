package ast;

// BinOp      ::= Expr Op Expr
// ---------------------------
public class BinOp extends Expr {

    public final Expr expr_left;
    public final Op op;
    public final Expr expr_right;

    public BinOp (Expr expr_left,Op op, Expr expr_right){
        this.expr_left = expr_left;
        this.op = op;
        this.expr_right = expr_right;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBinOp(this);
    }
}
