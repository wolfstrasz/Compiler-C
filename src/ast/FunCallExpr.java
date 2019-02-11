package ast;

import java.util.List;

// Function call (the String corresponds to the name of the function to call and the Expr* is the list of arguments)
// FunCallExpr ::= String Expr*
// -----------------------------------------------------------------------------------------------------------------
public class FunCallExpr extends Expr {

    public final String name;
    public final List<Expr> arguments;
    public FunDecl fd;

    public VarDecl writeToVD;
    public FunCallExpr (String name, List<Expr> arguments){
        this.name = name;
        this.arguments = arguments;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFunCallExpr(this);
    }
}
