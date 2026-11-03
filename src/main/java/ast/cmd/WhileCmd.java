package ast.cmd;

import ast.expr.Expr;
import visitor.Visitor;

public class WhileCmd extends Cmd {
    public Expr condition;
    public BlockCmd body;

    public WhileCmd(Expr condition, BlockCmd body, int linha, int coluna) {
        this.condition = condition;
        this.body = body;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitWhileCmd(this);
    }
}
