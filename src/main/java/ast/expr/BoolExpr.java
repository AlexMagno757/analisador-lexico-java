package ast.expr;

import visitor.Visitor;

public class BoolExpr extends Expr {
    public String value;

    public BoolExpr(String value, int linha, int coluna) {
        this.value = value;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitBoolExpr(this);
    }
}
