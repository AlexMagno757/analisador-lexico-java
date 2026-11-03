package visitor;

import ast.Program;
import ast.cmd.*;
import ast.decl.*;
import ast.expr.*;

public class ASTPrinter implements Visitor<String> {

    private String indent = "";

    private String indentAcres(){
        String old = indent;
        indent += "  ";
        return old;
    }

    @Override
    public String visitProgram(Program node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "Program\n";

        if (node.declarations != null) {
            for (Decl decl : node.declarations) {
                res += decl.accept(this);
            }
        }

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitFunDecl(FunDecl node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "FunDecl(" + node.id + ")\n";

        if (node.params != null) {
            for (Param param : node.params) {
                res += param.accept(this);
            }
        }


        res += node.body.accept(this);


        indent = currentIndent;
        return res;
    }

    @Override
    public String visitVarDecl(VarDecl node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "VarDecl(type=\"" + node.type + "\", id=\"" + node.id + "\")\n";

        if (node.expr != null) {
            res += node.expr.accept(this);
        }

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitParam(Param node) {

        return indent + "Param(type=\"" + node.type + "\", id=\"" + node.id + "\")\n";
    }

    @Override
    public String visitBlockCmd(BlockCmd node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "BlockCmd\n";

        if(node.cmds != null){
            for (Cmd cmd : node.cmds) {
                res += cmd.accept(this);
            }
        }

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitIfCmd(IfCmd node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "IfCmd\n";

        res += node.condition.accept(this);
        res += node.thenBlock.accept(this);

        if(node.elseBlock != null){
            res += node.elseBlock.accept(this);
        }

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitWhileCmd(WhileCmd node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "WhileCmd\n";

        res += node.condition.accept(this);
        res += node.body.accept(this);

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitForCmd(ForCmd node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "ForCmd\n";

        if(node.init != null){
            res += node.init.accept(this);
        }
        if(node.condition != null){
            res += node.condition.accept(this);
        }
        if(node.update != null){
            res += node.update.accept(this);
        }
        if(node.body != null){
            res += node.body.accept(this);
        }

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitReturnCmd(ReturnCmd node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "ReturnCmd\n";

        if(node.expr != null){
            res += node.expr.accept(this);
        }

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitPrintCmd(PrintCmd node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "PrintCmd\n";

        res += node.expr.accept(this);

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitAtribCmd(AtribCmd node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "AtribCmd(" + node.id + ")\n";

        res += node.expr.accept(this);

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitNumExpr(NumExpr node) {
        return indent + "NumExpr(" + node.value + ")\n";
    }

    @Override
    public String visitIdExpr(IdExpr node) {
        return indent + "IdExpr(" + node.id + ")\n";
    }

    @Override
    public String visitStringExpr(StringExpr node) {
        return indent + "StringExpr(\"" + node.value + "\")\n";
    }

    @Override
    public String visitBoolExpr(BoolExpr node) {
        return indent + "BoolExpr(value=\"" + node.value + "\")\n";
    }

    @Override
    public String visitUnaryExpr(UnaryExpr node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "UnaryExpr(" + node.op + ")\n";
        res += node.expr.accept(this);

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitBinExpr(BinExpr node) {
        String currentIndent = indent;
        indentAcres();

        String res = currentIndent + "BinExpr(" + node.op + ")\n";
        res += node.left.accept(this);
        res += node.right.accept(this);

        indent = currentIndent;
        return res;
    }

    @Override
    public String visitVarDeclCmd(VarDeclCmd node) {
        return node.decl.accept(this);
    }
}
