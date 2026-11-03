package ast.decl;

import ast.expr.Expr;
import visitor.Visitor;

public class VarDecl extends Decl {
    public String type;
    public String id;
    public Expr expr;

    public VarDecl(String type, String id, Expr expr, int linha, int coluna) {
        this.type = type;
        this.id = id;
        this.expr = expr;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitVarDecl(this);
    }
}
