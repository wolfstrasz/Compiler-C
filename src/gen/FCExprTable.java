package gen;

import ast.FunCallExpr;
import ast.VarDecl;
import ast.VarExpr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FCExprTable {
    private HashMap<FunCallExpr, VarDecl> table = new HashMap<>();
    public void put(FunCallExpr fce, VarDecl vd){
        table.put(fce,vd);
    }
    public boolean contains(FunCallExpr fce){
        return table.get(fce)!=null;
    }

    public VarDecl get(FunCallExpr fce){
        return table.get(fce);
    }

    public void printTable(){
        Iterator it = table.entrySet().iterator();
        System.out.println("############################################################");
        System.out.println("#          PRINTING FUNCALL EXPRESSION TABLE               #");
        System.out.println();
        while(it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            FunCallExpr fce = (FunCallExpr)me.getKey();
            VarDecl vd = (VarDecl)me.getValue();
            System.out.println(fce.name + " : " + vd.varName + " (" + fce.toString() + ")");
        }
        System.out.println();
        System.out.println("############################################################");
    }
}
