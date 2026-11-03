package visitor;

import ast.Program;
import ast.cmd.*;
import ast.decl.*;
import ast.expr.*;

public class SourcePrinter implements Visitor<String>{

    @Override
    public String visitProgram(Program node) {
        String res = "";

        if (node.declarations != null) {
            for (Decl decl : node.declarations) {
                res += decl.accept(this) + "\n\n";
            }
        }

        return res;
    }

    @Override
    public String visitFunDecl(FunDecl node) {
        String res = "fun " + node.id + "(";

        if (node.params != null) {
            for (int i = 0; i < node.params.size(); i++) {
                res += node.params.get(i).accept(this);

                if (i < node.params.size() - 1) {
                    res += ", ";
                }
            }
        }
        res += ") " + node.body.accept(this);

        return res;
    }

    @Override
    public String visitVarDecl(VarDecl node) {
        String res = node.type + " " + node.id;

        if (node.expr != null) {
            res += " = " + node.expr.accept(this);
        }

        return res + ";";
    }

    @Override
    public String visitParam(Param node) {
        return node.type + " " + node.id;
    }

    @Override
    public String visitBlockCmd(BlockCmd node) {
        String res = "{\n";
        if (node.cmds != null) {
            for (Cmd cmd : node.cmds) {
                res += "  " + cmd.accept(this) + "\n";
            }
        }
        return res + "}";
    }

    @Override
    public String visitIfCmd(IfCmd node) {
        String res = "if (" + node.condition.accept(this) + ") " + node.thenBlock.accept(this);
        if (node.elseBlock != null) {
            res += " else " + node.elseBlock.accept(this);
        }
        return res;
    }

    @Override
    public String visitWhileCmd(WhileCmd node) {
        return "while (" + node.condition.accept(this) + ") " + node.body.accept(this);
    }

    @Override
    public String visitForCmd(ForCmd node) {
        String res = "for (";

        if (node.init != null) {
            res += node.init.accept(this) + " ";
        }

        if (node.condition != null) {
            res += node.condition.accept(this);
        }
        res += "; ";

        if (node.update != null) {
            String updateStr = node.update.accept(this);
            if (updateStr.endsWith(";")) {
                updateStr = updateStr.substring(0, updateStr.length() - 1);
            }
            res += updateStr;
        }

        res += ") " + node.body.accept(this);
        return res;
    }

    @Override
    public String visitReturnCmd(ReturnCmd node) {
        if (node.expr != null) {
            return "return " + node.expr.accept(this) + ";";
        }
        return "return;";
    }

    @Override
    public String visitPrintCmd(PrintCmd node) {
        return "print(" + node.expr.accept(this) + ");";
    }

    @Override
    public String visitAtribCmd(AtribCmd node) {
        return node.id + " = " + node.expr.accept(this) + ";";
    }

    @Override
    public String visitNumExpr(NumExpr node) {
        return node.value;
    }

    @Override
    public String visitIdExpr(IdExpr node) {
        return node.id;
    }

    @Override
    public String visitStringExpr(StringExpr node) {
        return "\"" + node.value + "\"";
    }

    @Override
    public String visitBoolExpr(BoolExpr node) {
        return node.value;
    }

    @Override
    public String visitUnaryExpr(UnaryExpr node) {
        return "(" + node.op + node.expr.accept(this) + ")";
    }

    @Override
    public String visitBinExpr(BinExpr node) {
        return "(" + node.left.accept(this) + " " + node.op + " " + node.right.accept(this) + ")";
    }
}
