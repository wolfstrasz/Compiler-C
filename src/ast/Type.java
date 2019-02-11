package ast;
// Type        ::= BaseType | PointerType | StructType | ArrayType
// ---------------------------------------------------------------
public interface Type extends ASTNode {

    public <T> T accept(ASTVisitor<T> v);

}
