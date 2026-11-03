package ast.cmd;

import ast.expr.Expr;
import visitor.Visitor;

public class ForCmd extends Cmd {
    public AtribCmd init;
    public Expr condition;
    public AtribCmd update;
    public BlockCmd body;

    public ForCmd(AtribCmd init, Expr condition, AtribCmd update, BlockCmd body, int linha, int coluna) {
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
        this.linha = linha;
        this.coluna = coluna;

    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitForCmd(this);
    }
}
