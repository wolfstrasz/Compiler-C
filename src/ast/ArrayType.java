package ast;

// ArrayType   ::= Type int          // Type represent the element type, int represents the number of elements (number of elements)
// --------------------------------------------------------------------------------------------------------------------------------
public class ArrayType implements Type {

    public final Type type;
    public final Integer length;

    public boolean isGlobal = false;
    public Integer offsetPerItem = 0;

    // Constructor
    public ArrayType (Type type, Integer length){
        this.type = type;
        this.length = length;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayType(this);
    }
}
