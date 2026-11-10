package semantic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class StackSymbolTable implements SymbolTable{

    private final Deque<Map<String,Symbol>> scopes = new ArrayDeque<>();

    @Override
    public void openScope() {
        scopes.push(new HashMap<>());
    }

    @Override
    public void closeScope() {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("Erro: Tentativa de fechar um escopo, mas a Tabela de Símbolos está vazia.");
        }
        scopes.pop();
    }

    @Override
    public boolean declare(Symbol s) {
        if (scopes.isEmpty()) return false;
        return scopes.peek().putIfAbsent(s.name, s) == null;
    }

    @Override
    public Symbol lookupLocal(String name) {
        if (scopes.isEmpty()) return null;
        return scopes.peek().get(name);
    }

    @Override
    public Symbol lookup(String name) {
        for (Map<String, Symbol> scope : scopes) {
            Symbol s = scope.get(name);
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    @Override
    public int level() {
        return scopes.isEmpty() ? -1 : scopes.size() - 1;
    }
}
