package gen;
import ast.*;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class StructAnalyser extends BaseASTVisitor<Integer> {

    private PrintWriter writer;
    private Map<String, StructTypeDecl> structs = new HashMap<>();

    // Entry Point
    public void analyse(Program p, PrintWriter writer, Map<String, StructTypeDecl> structs){
        this.structs = structs;
        this.writer = writer;
        p.accept(this);
    }

    // PROGRAM
    // --------------------------------------------------------------------------
    @Override
    public Integer visitProgram(Program p) {
        // GO THROUGH GLOBAL DATA
        writer.println("############################################################");
        writer.println("#                      STRUCT ANALYSER                     #");
        // Set offsets and data sizes for struct declarations
        for (StructTypeDecl std : p.structTypeDecls){
            std.accept(this);
        }

        writer.println("############################################################");
        return null;
    }

    // Struct type declaration
    @Override
    public Integer visitStructTypeDecl(StructTypeDecl std) {
        writer.println("# struct " + std.structType.string + " { ");
        int offset = 0;
        for (VarDecl vd : std.varDecls){
            vd.isGlobal = true;                     // all items inside a struct will be considered as global
            vd.offset = offset;                     // set field offset
            writer.print("\t# ");
            vd.byteSize = vd.accept(this);          // get size of variable in bytes
            offset += vd.byteSize;                  // increment offset
            writer.println(" " + vd.varName + " : offset = " + vd.offset + " : size = " + vd.byteSize);

        }
        writer.println("\t# } size = " + offset);

        // Put the struct decl in map
        std.byteSize = offset;
        structs.put(std.structType.string,std);

        return offset;
    }

    // Var declaration
    @Override
    public Integer visitVarDecl(VarDecl vd) {
        Integer size = vd.type.accept(this);
        size = (size % 4 == 0) ? size : size + (4 - size % 4);
        vd.byteSize = size;
        return size;
    }


    // TYPES
    // --------------------------------------------------------------------------
    @Override
    public Integer visitBaseType(BaseType bt) {
        if (bt == BaseType.INT ) {
            writer.print("INT ");
            return 4;
        }
        if (bt == BaseType.CHAR) {
            writer.print("CHAR ");
            return 1;
        }
        return 0;
    }
    @Override
    public Integer visitPointerType(PointerType pt) {
        writer.print("*");
        pt.type.accept(this);
        return 4;
    }
    @Override
    public Integer visitStructType(StructType stype) {
        StructTypeDecl std = structs.get(stype.string);
        writer.print(stype.string + " ");
        return std.byteSize;
    }
    @Override
    public Integer visitArrayType(ArrayType at) {
        Integer typeSize = at.type.accept(this);
        at.offsetPerItem = typeSize;
        writer.print("[" + at.length + "] ");
        return typeSize * at.length;
    }

}
