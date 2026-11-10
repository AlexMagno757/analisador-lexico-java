package ast.cmd;

import ast.decl.VarDecl;
import visitor.Visitor;

public class VarDeclCmd extends Cmd{
    public VarDecl decl;

    public VarDeclCmd(VarDecl decl, int linha, int coluna) {
        this.decl = decl;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitVarDeclCmd(this);
    }
}
