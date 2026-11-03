package ast.cmd;

import visitor.Visitor;

import java.util.List;

public class BlockCmd extends Cmd {
    public List<Cmd> cmds;

    public BlockCmd(List<Cmd> cmds, int linha, int coluna) {
        this.cmds = cmds;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitBlockCmd(this);
    }
}
