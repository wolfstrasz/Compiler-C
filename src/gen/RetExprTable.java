package gen;
import ast.*;

import java.util.*;

public class RetExprTable {

    private HashMap<String, VarExpr> table = new HashMap<>();
    public void put(String name, VarExpr ve){
        table.put(name,ve);
    }
    public boolean contains(String name){
        return table.get(name)!=null;
    }

    public VarExpr get(String name){
        return table.get(name);
    }

    public void printTable(){
        Iterator it = table.entrySet().iterator();
        System.out.println("############################################################");
        System.out.println("#           PRINTING RETURN EXPRESSION TABLE               #");
        System.out.println();
        while(it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            System.out.println(me.getKey() + " : " + ((VarExpr)me.getValue()).vd.varName);
        }
        System.out.println();
        System.out.println("############################################################");
    }
}
