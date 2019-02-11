package ast;

import java.util.List;

// FunDecl definition (the String is the name of the FunDecl)
// FunDecl  ::= Type String VarDecl* Block
// ----------------------------------------------------------
public class FunDecl implements ASTNode {
    public final Type type;
    public final String name;
    public final List<VarDecl> params;
    public final Block block;

    public VarDecl returnVar;

    public int frameSize = -1;
    public int argumentsByteSize = -1;
    public int variablesByteSize = -1;
    public int oldFP_Offset = -1;
    public int retAddr_Offset = -1;
    public int retValAddress_Offset = -1;

    public FunDecl(Type type, String name, List<VarDecl> params, Block block) {
	    this.type = type;
	    this.name = name;
	    this.params = params;
	    this.block = block;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitFunDecl(this);
    }
}
