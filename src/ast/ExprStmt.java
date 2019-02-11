package ast;

// An expression statement (e.g. x+2;)
// ExprStmt ::= Expr
// -----------------------------------
public class ExprStmt extends Stmt {

    public final Expr expr;

    public ExprStmt (Expr expr){
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitExprStmt(this);
    }
}
