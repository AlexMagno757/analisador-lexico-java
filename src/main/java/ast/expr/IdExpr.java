package ast.expr;

import semantic.Symbol;
import visitor.Visitor;

public class IdExpr extends Expr{
    public String id;
    public Symbol decl;

    public IdExpr(String id, int linha, int coluna) {
        this.id = id;
        this.linha = linha;
        this. coluna = coluna;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitIdExpr(this);
    }
}
