package ast;

// If statement: if (Expr) Stmt1 else Stmt2; (if the second Stmt is null, this means there is no else part)
// If         ::= Expr Stmt [Stmt]
// --------------------------------------------------------------------------------------------------------
public class If extends Stmt {

    public Expr expr;
    public Stmt stmt;
    public Stmt stmtElse;

    public If (Expr expr, Stmt stmt, Stmt stmtElse){
        this.expr = expr;
        this.stmt = stmt;
        this.stmtElse = stmtElse;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIf(this);
    }
}
