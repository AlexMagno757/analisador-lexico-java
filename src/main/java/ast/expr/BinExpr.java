package ast.expr;

import visitor.Visitor;

public class BinExpr extends Expr {
    public Expr left;
    public String op;
    public Expr right;

    public BinExpr(Expr left, String op, Expr right, int linha, int coluna) {
        this.left = left;
        this.op = op;
        this.right = right;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitBinExpr(this);
    }

}
