package ast.cmd;

import ast.expr.Expr;
import visitor.Visitor;

public class IfCmd extends Cmd {
    public Expr condition;
    public BlockCmd thenBlock;
    public BlockCmd elseBlock;

    public IfCmd(Expr condition, BlockCmd thenBlock, BlockCmd elseBlock, int linha, int coluna) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitIfCmd(this);
    }
}
