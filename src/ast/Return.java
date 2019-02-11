package ast;

// Return statement : (the Expr is optional)
// Return     ::= [Expr]
// -----------------------------------------
public class Return extends Stmt {

    public Expr expr;

    public Return (Expr expr){
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitReturn(this);
    }
}