package ast;

import visitor.Visitor;

public abstract class ASTNode {
    public int linha;
    public int coluna;

    public abstract <T> T accept(Visitor<T> v);
}
