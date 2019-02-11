package gen;

import ast.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessAnalyser extends BaseASTVisitor<ASTNode> {

    // Data
    private Map<String, StructTypeDecl> structs = new HashMap<>();
    private AccessTable at;
    private CopyArgTable CAT;
    private boolean isDereferencing = false;
    // Entry Point
    public void analyse(Program p, AccessTable at, Map<String,StructTypeDecl> structs,CopyArgTable CAT){
        this.at = at;
        this.structs = structs;
        this.CAT = CAT;
        p.accept(this);
    }

    // Functionality
    // --------------------------------------------------------------------------
    @Override
    public ASTNode visitArrayAccessExpr(ArrayAccessExpr aae) {
        // Get the type
    //    System.out.println("IN AAE");
    //    System.out.println(aae.array.toString());
        ASTNode node = aae.array.accept(this);
   //     System.out.println(node.toString());

        if(node.getClass() == ArrayType.class){
    //        System.out.println("IN AAE TYPE");

            // Cast into type node
            ArrayType type =(ArrayType) node;

            // Store in table
            int itemOffset = type.offsetPerItem;
            at.addAccess(aae,itemOffset);
       //     System.out.println("IN AAE TYPE");
      //      System.out.println(type.type.toString());
            // Return the next level type
            return type.type;
        }

        if(node.getClass() == PointerType.class){
            // Cast into type node
            PointerType type = (PointerType) node;

            // Store in table
            int itemOffset = type.offsetPerItem;
            at.addAccess(aae,itemOffset);

            // Return the next level type
            return type.type;
        }

        // Error check if we did ot receive an array type (we need array type for array access)
            System.out.println("ERROR Access Analyser: vd is not of type ARRAY TYPE оr POINTER TYPE");

        return null;
    }

    @Override
    public ASTNode visitFieldAccessExpr(FieldAccessExpr fae) {
        // Get the type
 //       System.out.println("HERE");

        ASTNode node = fae.structure.accept(this);

    //    System.out.println("HERE");

        if (node.getClass() == StructType.class){
            // Cast into type node
            StructType type = (StructType)node;

            // Find the struct declaration to find the fieldname
            StructTypeDecl std = structs.get(type.string);
            if (std == null) {
                System.out.println("ERROR Access Analyser: DID NOT FIND STRUCT DECL in FAE");
                return null;
            }

            // Find the field name
            for (VarDecl v : std.varDecls){
                if(v.varName.equals(fae.fieldname)){
                    // Store in table
                    int itemOffset = v.offset;
                    at.addAccess(fae,itemOffset);
                    // Return next level type
                    return v.type;
                }
            }

            System.out.println("ERROR Access Analyser: Did not find matching fieldname in FAE");
            return null;
        }
        if (node.getClass() == PointerType.class){
            // Cast into type node
            PointerType type = (PointerType) node;
            if (type.type.getClass() == StructType.class) {
                StructType type2 = (StructType)type.type;
                // Find the struct declaration to find the fieldname
                StructTypeDecl std = structs.get(type2.string);
                if (std == null) {
                    System.out.println("ERROR Access Analyser: DID NOT FIND STRUCT DECL in FAE");
                    return null;
                }

                // Find the field name
                for (VarDecl v : std.varDecls) {
                    if (v.varName.equals(fae.fieldname)) {
                        // Store in table
                        int itemOffset = v.offset;
                        at.addAccess(fae, itemOffset);
                        // Return next level type
                        return v.type;
                    }
                }

                System.out.println("ERROR Access Analyser: Did not find matching fieldname in FAE");
                return null;
            }
        }

        System.out.println("ERROR Access Analyser: FAE vd is not of type STRUCT TYPE");
        return null;
    }
    @Override
    public ASTNode visitValueAtExpr(ValueAtExpr vae) {
        isDereferencing = true;
    //    System.out.println("AT VAE");
    //    System.out.println(vae.value.toString());
        ASTNode derNode = vae.value.accept(this);
    //    System.out.println("BACK AT VAE");
    //    System.out.println(derNode.toString());
       // return vae.value.accept(this);
        isDereferencing = false;
        return derNode;
    }
    @Override
    public ASTNode visitVarExpr(VarExpr v) {
        if (isDereferencing){
            if (v.vd.type.getClass() == PointerType.class)
                return ((PointerType)v.vd.type).type;
        }
        // VarExpr should return its VarDecl type
        return v.vd.type;
    }

    @Override
    public ASTNode visitStrLiteral(StrLiteral sl) {
        // String literal should forward its linked VarExpr
        return sl.varExpr.accept(this);
    }

    // FORWARDING
    // --------------------------------------------------------------------------
    @Override
    public ASTNode visitProgram(Program p) {
        for (FunDecl fd : p.funDecls)
            fd.accept(this);
        return null;
    }
    @Override
    public ASTNode visitFunDecl(FunDecl fd) {
        fd.block.accept(this);
        return null;
    }

    // Stmts
    @Override
    public ASTNode visitWhile(While w) {
        w.expr.accept(this);
        w.stmt.accept(this);
        return null;
    }
    @Override
    public ASTNode visitIf(If i) {
        i.expr.accept(this);
        i.stmt.accept(this);
        if(i.stmtElse != null)
            i.stmtElse.accept(this);
        return null;
    }
    @Override
    public ASTNode visitAssign(Assign a) {
        a.assign_to.accept(this);
        a.assign_from.accept(this);
        return null;
    }
    @Override
    public ASTNode visitReturn(Return r) {
        if (r.expr != null)
            r.expr.accept(this);
        return null;
    }
    @Override
    public ASTNode visitBlock(Block b) {
        for (Stmt stmt : b.stmts)
            stmt.accept(this);
        return null;
    }


    private boolean notABuiltin(FunCallExpr fce){
        if(fce.name.equals("print_i"))return false;
        if(fce.name.equals("print_c"))return false;
        if(fce.name.equals("print_s"))return false;
        if(fce.name.equals("read_i"))return false;
        if(fce.name.equals("read_c"))return false;
        if(fce.name.equals("mcmalloc"))return false;
        return true;
    }
    // Exprs
    @Override
    public ASTNode visitExprStmt(ExprStmt es) {
  //      System.out.println("In EXPR STMT");
        es.expr.accept(this);
        return null;
    }
    @Override
    public ASTNode visitBinOp(BinOp bo) {
        bo.expr_left.accept(this);
        bo.expr_right.accept(this);
        return null;
    }
    @Override
    public ASTNode visitFunCallExpr(FunCallExpr fce) {
   //     System.out.println("IN FCE");
        if(notABuiltin(fce)) {
            List<Assign> assignStmts = CAT.get(fce);
            for (Assign a : assignStmts) {
                a.assign_to.accept(this);
                a.assign_from.accept(this); // this is the same as fce.args(EXPR);
            }
        }else {
            for (Expr arg : fce.arguments)
                arg.accept(this);
        }
//
//        for( Expr expr : fce.arguments){
//            expr.accept(this);
//        }
        return null;
    }

    @Override
    public ASTNode visitTypecastExpr(TypecastExpr te) {
        return te.expr.accept(this);
    //    return null;
    }


    // NOT IMPLEMENTED
    // --------------------------------------------------------------------------
    // Decl
    @Override
    public ASTNode visitStructTypeDecl(StructTypeDecl st) { return null; }
    @Override
    public ASTNode visitVarDecl(VarDecl vd) { return null; }

    // Stmts
    @Override
    public ASTNode visitStmt(Stmt s) { return null; }

    // Exprs
    @Override
    public ASTNode visitExpr(Expr e) { return null; }
    @Override
    public ASTNode visitIntLiteral(IntLiteral il) { return null; }
    @Override
    public ASTNode visitChrLiteral(ChrLiteral cl) { return null; }
    @Override
    public ASTNode visitSizeOfExpr(SizeOfExpr soe) { return null; }
    @Override
    public ASTNode visitOp(Op o) { return null; }

    // Types
    @Override
    public ASTNode visitType(Type t) { return null; }
    @Override
    public ASTNode visitBaseType(BaseType bt) { return null; }
    @Override
    public ASTNode visitPointerType(PointerType pt) { return null; }
    @Override
    public ASTNode visitStructType(StructType stype) { return null; }
    @Override
    public ASTNode visitArrayType(ArrayType at) { return null; }

}
