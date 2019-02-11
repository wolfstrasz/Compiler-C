package gen;

import ast.*;

import java.util.HashMap;
import java.util.Map;

public class AddressabilityFinder extends BaseASTVisitor<String> {
    private Map<String, StructTypeDecl> structs = new HashMap<>();      // holds all structs

    public AddressabilityFinder(Map<String, StructTypeDecl> structs){
        this.structs = structs;
    }

    private boolean isDereferencing = false;

    @Override
    public String visitBaseType(BaseType bt) {
        if (bt == BaseType.CHAR) return "$BYTE";
        return "$WORD";
    }

    @Override
    public String visitPointerType(PointerType pt) {
     //   System.out.println("ADD Finder + PT: " + pt.type.toString());
        if (isDereferencing )
            return pt.type.accept(this);
        return "$POINTER";
    }

    @Override
    public String visitStructType(StructType stype) {
        return stype.string;
    }

    @Override
    public String visitArrayType(ArrayType at) {
        if(at.type == BaseType.CHAR) return "$BYTE";
        // if it is an array of pointers those should be cosidered as WORDS when accessing
      // if(at.type.getClass() == PointerType.class) return "$WORD";
        return at.type.accept(this);
    }

    @Override
    public String visitVarExpr(VarExpr v) {
        return v.vd.type.accept(this);
    }

    @Override
    public String visitFunCallExpr(FunCallExpr fce) {
        // Malloc creates an address and returns it as "$WORD" to be stored to pointer
        if (fce.fd.name == "mcmalloc") return "$WORD";
        return fce.fd.type.accept(this);
    }

    @Override
    public String visitArrayAccessExpr(ArrayAccessExpr aae) {
        return aae.array.accept(this);
    }

    @Override
    public String visitFieldAccessExpr(FieldAccessExpr fae) {
        // Get the struct name
        String structName = fae.structure.accept(this);
        // Find the struct decl
        StructTypeDecl std = structs.get(structName);

        // Get the field name we going to use
        String field = fae.fieldname;

        // Search for fieldname type
        for(VarDecl vd : std.varDecls){
            if (vd.varName.equals(field)){
                return vd.type.accept(this);
            }
        }
        // SHOULD NOT REACH HERE
        return structName;
    }

    @Override
    public String visitValueAtExpr(ValueAtExpr vae) {
        isDereferencing = true;
        String addressability = vae.value.accept(this);
        isDereferencing = false;
        return addressability;
    }

    @Override
    public String visitSizeOfExpr(SizeOfExpr soe) {
        return "$WORD";
    }

    @Override
    public String visitTypecastExpr(TypecastExpr te) {
        return te.expr.accept(this);
    }

    @Override
    public String visitIntLiteral(IntLiteral il) {
        return "$WORD";
    }

    @Override
    public String visitStrLiteral(StrLiteral sl) {
        return "$BYTE";
    }

    @Override
    public String visitChrLiteral(ChrLiteral cl) {
        return "$WORDC";
    }

    @Override
    public String visitBinOp(BinOp bo) {
        return bo.expr_left.accept(this);
    }

    // NOT IMPLEMENTED
    // ------------------------------------------------------
    @Override
    public String visitStmt(Stmt s) {
        return null;
    }

    @Override
    public String visitExprStmt(ExprStmt es) {
        return null;
    }

    @Override
    public String visitWhile(While w) {
        return null;
    }

    @Override
    public String visitIf(If i) {
        return null;
    }

    @Override
    public String visitAssign(Assign a) {
        return null;
    }

    @Override
    public String visitReturn(Return r) {
        return null;
    }

    @Override
    public String visitBlock(Block b) {
        return null;
    }

    @Override
    public String visitOp(Op o) {
        return null;
    }

    @Override
    public String visitProgram(Program p) {
        return null;
    }

    @Override
    public String visitType(Type t) {
        return null;
    }

    @Override
    public String visitStructTypeDecl(StructTypeDecl st) {
        return null;
    }

    @Override
    public String visitVarDecl(VarDecl vd) {
        return null;
    }

    @Override
    public String visitFunDecl(FunDecl p) {
        return null;
    }

    @Override
    public String visitExpr(Expr e) {
        return null;
    }

}
