package ast;

// BaseType    ::= INT | CHAR | VOID
// ---------------------------------
public enum BaseType implements Type {

    INT, CHAR, VOID;

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBaseType(this);
    }
}
