package ast;

import java.util.List;

// The program top AST node (a list of struct type declaration, list of variable declarations, list of FunDecl definition)
// Program    ::= StructTypeDecl* VarDecl* FunDecl*
// -----------------------------------------------------------------------------------------------------------------------
public class Program implements ASTNode {

    public final List<StructTypeDecl> structTypeDecls;
    public final List<VarDecl> varDecls;
    public final List<FunDecl> funDecls;

    public Program(List<StructTypeDecl> structTypeDecls, List<VarDecl> varDecls, List<FunDecl> funDecls) {
        this.structTypeDecls = structTypeDecls;
	    this.varDecls = varDecls;
	    this.funDecls = funDecls;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitProgram(this);
    }
}
