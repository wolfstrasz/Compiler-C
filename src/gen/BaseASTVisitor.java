package gen;
import ast.*;

public class BaseASTVisitor<T> implements ASTVisitor<T> {

    // PROGRAM
    // --------------------------------------------------------
    @Override
    public T visitProgram(Program p) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + p.toString() );
        return null;
    }

    // DECLARATIONS
    // --------------------------------------------------------
    @Override
    public T visitStructTypeDecl(StructTypeDecl std) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + std.toString() );
        return null;
    }

    @Override
    public T visitVarDecl(VarDecl vd) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + vd.toString() );
        return null;
    }

    @Override
    public T visitFunDecl(FunDecl fd)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + fd.toString() );
        return null;
    }


    // STATEMENTS
    // --------------------------------------------------------
    @Override
    public T visitStmt(Stmt s)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + s.toString() );
        return null;
    }

    @Override
    public T visitExprStmt(ExprStmt es) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + es.toString() );
        return null;
    }

    @Override
    public T visitIf(If i)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + i.toString() );
        return null;
    }

    @Override
    public T visitWhile(While w) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + w.toString() );
        return null;
    }

    @Override
    public T visitAssign(Assign a) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + a.toString() );
        return null;
    }

    @Override
    public T visitReturn(Return r) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + r.toString() );
        return null;
    }

    @Override
    public T visitBlock(Block b)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + b.toString() );
        return null;
    }


    // OPERATIONS
    // --------------------------------------------------------
    @Override
    public T visitBinOp(BinOp bo)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + bo.toString() );
        return null;
    }

    @Override
    public T visitOp(Op o)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + o.toString() );
        return null;
    }


    // EXPRESSIONS
    // --------------------------------------------------------
    @Override
    public T visitExpr(Expr e) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + e.toString() );
        return null;
    }

    @Override
    public T visitVarExpr(VarExpr v) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + v.toString() );
        return null;
    }
    @Override
    public T visitFunCallExpr(FunCallExpr fce)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + fce.toString() );
        return null;
    }

    @Override
    public T visitArrayAccessExpr(ArrayAccessExpr aae) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + aae.toString() );
        return null;
    }

    @Override
    public T visitFieldAccessExpr(FieldAccessExpr fae) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + fae.toString() );
        return null;
    }

    @Override
    public T visitTypecastExpr(TypecastExpr te)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + te.toString() );
        return null;
    }

    @Override
    public T visitValueAtExpr(ValueAtExpr vae)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + vae.toString() );
        return null;
    }

    @Override
    public T visitSizeOfExpr(SizeOfExpr soe)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + soe.toString() );
        return null;
    }

    // TYPES
    // --------------------------------------------------------
    @Override
    public T visitType(Type t)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + t.toString() );
        return null;
    }

    @Override
    public T visitStructType(StructType st) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + st.toString() );
        return null;
    }

    @Override
    public T visitArrayType(ArrayType at)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + at.toString() );
        return null;
    }

    @Override
    public T visitPointerType(PointerType pt) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + pt.toString() );
        return null;
    }

    @Override
    public T visitBaseType(BaseType bt)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + bt.toString() );
        return null;
    }


    // LITERALS
    // --------------------------------------------------------
    @Override
    public T visitIntLiteral(IntLiteral il)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + il.toString() );
        return null;
    }

    @Override
    public T visitStrLiteral(StrLiteral sl) {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + sl.toString() );
        return null;
    }
    @Override
    public T visitChrLiteral(ChrLiteral cl)  {
        System.out.println("BaseASTVisitor{ \n \t UNDEFINED VISIT: " + this 
                + "\n \t IN ENTRY: " + cl.toString() );
        return null;
    }
}