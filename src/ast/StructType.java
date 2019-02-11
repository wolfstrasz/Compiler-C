package ast;

// StructType  ::= String            // represent a struct type (the String is the name of the declared struct type)
// -----------------------------------------------------------------------------------------------------------------
public class StructType implements Type {

    public final String string;

    public StructType (String string){
        this.string = string;
    }
    
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }
}
