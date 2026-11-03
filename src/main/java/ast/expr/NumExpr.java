package ast.expr;

import visitor.Visitor;

public class NumExpr extends Expr{
    public String value;

    public NumExpr(String value, int linha, int coluna) {
        this.value = value;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitNumExpr(this);
    }
}
