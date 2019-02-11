package sem;

import java.util.HashMap;
import java.util.Map;

public class Scope {
	private Scope outer;
	private Map<String, Symbol> symbolTable = new HashMap<>();

	public Scope(Scope outer) {
		this.outer = outer;
	}

	public Scope() { this(null); }

	public Symbol lookup(String name) {
		// Check if in current scope
		Symbol symbol = lookupCurrent(name);
		if (symbol == null) {	// If not in current scop
			// Check if there is an outer scope
			if (outer == null) return null;
			// Check if it is defined in an outer scope
			return outer.lookup(name);
		}
		return symbol;
	}

	public Symbol lookupCurrent(String name) {
		// Check if in the table of the scope
		return symbolTable.get(name);
	}

	public void put(Symbol sym) {
		symbolTable.put(sym.name, sym);
	}
}
