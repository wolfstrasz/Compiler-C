package ast;

// Assignment statement: Expr = Expr; (e.g. x[3] = 2;)
// Assign     ::= Expr Expr
// ---------------------------------------------------
public class Assign extends Stmt {

    public final Expr assign_to;
    public final Expr assign_from;

    public Assign (Expr assign_to, Expr assign_from){
        this.assign_to = assign_to;
        this.assign_from = assign_from;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitAssign(this);
    }
}
