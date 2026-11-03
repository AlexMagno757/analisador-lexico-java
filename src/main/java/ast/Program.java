package ast;

import ast.decl.Decl;
import visitor.Visitor;

import java.util.List;

public class Program extends ASTNode {
    public List<Decl> declarations;

    public Program(List<Decl> declarations, int linha, int coluna) {
        this.declarations = declarations;
        this.linha = linha;   // Herdado de ast.ASTNode
        this.coluna = coluna; // Herdado de ast.ASTNode
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitProgram(this);
    }
}
