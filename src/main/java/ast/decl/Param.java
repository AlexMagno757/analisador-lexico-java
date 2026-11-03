package ast.decl;

import visitor.Visitor;

public class Param extends Decl {
    public String type;
    public String id;

    public Param(String type, String id, int linha, int coluna) {
        this.type = type;
        this.id = id;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitParam(this);
    }
}