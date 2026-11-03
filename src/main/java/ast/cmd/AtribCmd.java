package ast.cmd;

import ast.expr.Expr;
import visitor.Visitor;

public class AtribCmd extends Cmd {
    public String id;
    public Expr expr;

    public AtribCmd(String id, Expr expr, int linha, int coluna) {
        this.id = id;
        this.expr = expr;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitAtribCmd(this);
    }
}
