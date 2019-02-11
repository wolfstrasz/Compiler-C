package ast;

// IntLiteral ::= int     // int stores the value of the integer
// -------------------------------------------------------------
public class IntLiteral extends Expr {

    public final Integer value;

    public IntLiteral (Integer value){
        this.value = value;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIntLiteral(this);
    }
}
