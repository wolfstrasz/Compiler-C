package ast;

// StrLiteral ::= String  // String stored the value of the String
// ---------------------------------------------------------------
public class StrLiteral extends Expr {

    public final String value;

    public VarExpr varExpr;
    public StrLiteral (String value){
        this.value = value;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStrLiteral(this);
    }
}
