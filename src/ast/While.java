package ast;

// While loop statement : while (Expr) Stmt;
// While      ::= Expr Stmt
// -----------------------------------------
public class While extends Stmt {

    public final Expr expr;
    public Stmt stmt;

    public While (Expr expr, Stmt stmt){
        this.expr = expr;
        this.stmt = stmt;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitWhile(this);
    }
}