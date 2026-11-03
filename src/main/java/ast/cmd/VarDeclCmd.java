package ast.cmd;

import ast.decl.VarDecl;
import visitor.Visitor;

public class VarDeclCmd extends Cmd{
    public VarDecl decl;

    public VarDeclCmd(VarDecl decl) {
        this.decl = decl;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitVarDeclCmd(this);
    }
}
