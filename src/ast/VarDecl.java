package ast;

// Variable declaration
// VarDecl    ::= Type String
// --------------------------
public class VarDecl implements ASTNode {
    public final Type type;
    public final String varName;

    public int offset = 0;
    public boolean isGlobal = false;
    public int byteSize = -1;
    public String address = "$fp";

    public VarDecl(Type type, String varName) {
	    this.type = type;
	    this.varName = varName;
    }
     public <T> T accept(ASTVisitor<T> v) {
	return v.visitVarDecl(this);
    }
}
