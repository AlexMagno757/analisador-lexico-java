package semantic;

import ast.ASTNode;

public class Symbol {

    public enum Kind { VAR, CONST, PARAM, FUN }
    public final String name;
    public final Kind kind;
    public final ASTNode decl;
    public final int level;
    public Object type;
    public int offset;

    public Symbol(String name, Kind kind, ASTNode decl, int level) {
        this.name = name;
        this.kind = kind;
        this.decl = decl;
        this.level = level;
    }
}
