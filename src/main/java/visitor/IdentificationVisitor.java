package visitor;

import ast.*;
import ast.cmd.*;
import ast.decl.*;
import ast.expr.*;
import semantic.*;

import java.util.ArrayList;
import java.util.List;

public class IdentificationVisitor implements Visitor<Void>{

    public final SymbolTable table = new StackSymbolTable();
    public final List<String> errors = new ArrayList<>();

    private void reportRedeclaration(String id, int currentLine) {
        Symbol prev = table.lookupLocal(id);
        int prevLine = (prev != null && prev.decl != null) ? prev.decl.linha : -1;
        errors.add("linha " + currentLine + ": '" + id + "' já declarado neste escopo (linha " + prevLine + ")");
    }

    @Override
    public Void visitProgram(Program node) {
        table.openScope();
        for(Decl d: node.declarations){
            d.accept(this);
        }

        table.closeScope();
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl node) {
        Symbol s = new Symbol(node.id, Symbol.Kind.FUN, node, table.level());
        if (!table.declare(s)) {
            reportRedeclaration(node.id, node.linha);
        }

        table.openScope();

        for (Param p : node.params) {
            p.accept(this);
        }

        for (Cmd c : node.body.cmds) {
            c.accept(this);
        }

        table.closeScope();
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl node) {
        Symbol s = new Symbol(node.id, Symbol.Kind.VAR, node, table.level());
        if (!table.declare(s)) {
            reportRedeclaration(node.id, node.linha);
        }
        return null;
    }

    @Override
    public Void visitParam(Param node) {
        Symbol s = new Symbol(node.id, Symbol.Kind.PARAM, node, table.level());
        if (!table.declare(s)) {
            reportRedeclaration(node.id, node.linha);
        }
        return null;
    }

    @Override
    public Void visitVarDeclCmd(VarDeclCmd node) {
        Symbol s = new Symbol(node.decl.id, Symbol.Kind.VAR, node, table.level());
        if (!table.declare(s)) {
            reportRedeclaration(node.decl.id, node.linha);
        }

        if (node.decl.expr != null) {
            node.decl.expr.accept(this);
        }
        return null;
    }

    @Override
    public Void visitBlockCmd(BlockCmd node) {
        table.openScope();

        for (Cmd c : node.cmds) {
            c.accept(this);
        }

        table.closeScope();
        return null;
    }

    @Override
    public Void visitIfCmd(IfCmd node) {
        node.condition.accept(this);
        node.thenBlock.accept(this);

        if (node.elseBlock != null) {
            node.elseBlock.accept(this);
        }
        return null;
    }

    @Override
    public Void visitWhileCmd(WhileCmd node) {
        node.condition.accept(this);
        node.body.accept(this);
        return null;
    }

    @Override
    public Void visitForCmd(ForCmd node) {
        node.init.accept(this);
        node.condition.accept(this);
        node.update.accept(this);
        node.body.accept(this);
        return null;
    }

    @Override
    public Void visitReturnCmd(ReturnCmd node) {
        if (node.expr != null) {
            node.expr.accept(this);
        }
        return null;
    }

    @Override
    public Void visitPrintCmd(PrintCmd node) {
        node.expr.accept(this);
        return null;
    }

    @Override
    public Void visitAtribCmd(AtribCmd node) {
        Symbol s = table.lookup(node.id);
        if (s == null) {
            errors.add("linha " + node.linha + ": '" + node.id + "' não declarado");
        }
        if (node.expr != null) {
            node.expr.accept(this);
        }
        return null;
    }

    @Override
    public Void visitNumExpr(NumExpr node) {
        return null;
    }

    @Override
    public Void visitIdExpr(IdExpr node) {
        node.decl = table.lookup(node.id);
        if (node.decl == null) {
            errors.add("linha " + node.linha + ": '" + node.id + "' não declarado");
        }
        return null;
    }

    @Override
    public Void visitStringExpr(StringExpr node) {
        return null;
    }

    @Override
    public Void visitBoolExpr(BoolExpr node) {
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr node) {
        node.expr.accept(this);
        return null;
    }

    @Override
    public Void visitBinExpr(BinExpr node) {
        node.left.accept(this);
        node.right.accept(this);
        return null;
    }
}
