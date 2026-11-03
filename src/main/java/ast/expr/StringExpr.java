package ast.expr;

import visitor.Visitor;

public class StringExpr extends Expr{
    public String value;

    public StringExpr(String value, int linha, int coluna) {
        this.value = value;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitStringExpr(this);
    }
}
