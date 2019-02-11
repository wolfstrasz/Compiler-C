package ast;

// Field access expression : Expr.String (e.g. *a.b)
// FieldAccessExpr ::= Expr String // the Expr represents the structure, the String represents the name of the field
// -------------------------------------------------
public class FieldAccessExpr extends Expr {

    public final Expr structure;
    public final String fieldname;

    public FieldAccessExpr (Expr structure, String fieldname){
        this.structure = structure;
        this.fieldname = fieldname;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFieldAccessExpr(this);
    }
}
