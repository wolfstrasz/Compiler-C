package ast;

// Variable (the String is the name of the variable)
// VarExpr     ::= String
// -------------------------------------------------
public class VarExpr extends Expr {
    public final String name;
    public VarDecl vd; // to be filled in by the name analyser
    public Type type;
    public VarExpr(String name){
	this.name = name;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitVarExpr(this);
    }
}
