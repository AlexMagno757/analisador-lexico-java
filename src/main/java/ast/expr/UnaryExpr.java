package ast.expr;

import visitor.Visitor;

public class UnaryExpr extends Expr{
    public String op;
    public Expr expr;

    public UnaryExpr(String op, Expr expr, int linha , int coluna) {
        this.op = op;
        this.expr = expr;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitUnaryExpr(this);
    }
}
