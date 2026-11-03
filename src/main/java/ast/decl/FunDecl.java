package ast.decl;

import ast.cmd.BlockCmd;
import visitor.Visitor;

import java.util.List;

public class FunDecl extends Decl {
    public String id;
    public List<Param> params;
    public BlockCmd body;

    public FunDecl(String id, List<Param> params, BlockCmd body, int linha, int coluna) {
        this.id = id;
        this.params = params;
        this.body= body;
        this.linha = linha;
        this.coluna= coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitFunDecl(this);
    }
}
