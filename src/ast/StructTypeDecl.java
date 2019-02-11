package ast;

import java.util.List;

// Struct declaration
// StructTypeDecl ::= StructType VarDecl*
// --------------------------------------
public class StructTypeDecl implements ASTNode {

    public final StructType structType;
    public final List<VarDecl> varDecls;

    public int byteSize = -1;
    public StructTypeDecl(StructType structType, List<VarDecl> varDecls){
        this.structType = structType;
        this.varDecls = varDecls;
    }
    
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructTypeDecl(this);
    }

}
