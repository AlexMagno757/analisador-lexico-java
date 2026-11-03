package ast.cmd;

import ast.expr.Expr;
import visitor.Visitor;

public class PrintCmd extends Cmd {
    public Expr expr;

    public PrintCmd(Expr expr, int linha, int coluna) {
        this.expr = expr;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitPrintCmd(this);
    }
}
