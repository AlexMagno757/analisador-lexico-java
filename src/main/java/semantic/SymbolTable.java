package semantic;

public interface SymbolTable {
    void openScope();                 // entra em um bloco
    void closeScope();                // sai do bloco
    boolean declare(Symbol s);        // false: já existe neste escopo
    Symbol lookupLocal(String name);  // só no escopo do topo
    Symbol lookup(String name);       // do mais interno ao mais externo
    int level();                      // profundidade atual (0 = global)
}
