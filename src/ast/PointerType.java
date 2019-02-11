package ast;

// PointerType ::= Type              // use to represent pointers to other types
// -----------------------------------------------------------------------------
public class PointerType implements Type {

    public final Type type;

    public Integer offsetPerItem;
    public PointerType (Type type) {
        this.type = type;
    }
    
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitPointerType(this);
    }
}
