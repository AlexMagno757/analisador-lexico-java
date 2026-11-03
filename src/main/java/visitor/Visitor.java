package visitor;

import ast.*;
import ast.cmd.*;
import ast.decl.*;
import ast.expr.*;

public interface Visitor<T> {
    // Raiz
    T visitProgram(Program node);

    // Declarações
    T visitFunDecl(FunDecl node);
    T visitVarDecl(VarDecl node);
    T visitParam(Param node);

    T visitVarDeclCmd(VarDeclCmd node);

    // Comandos
    T visitBlockCmd(BlockCmd node);
    T visitIfCmd(IfCmd node);
    T visitWhileCmd(WhileCmd node);
    T visitForCmd(ForCmd node);
    T visitReturnCmd(ReturnCmd node);
    T visitPrintCmd(PrintCmd node);
    T visitAtribCmd(AtribCmd node);

    // Expressões
    T visitNumExpr(NumExpr node);
    T visitIdExpr(IdExpr node);
    T visitStringExpr(StringExpr node);
    T visitBoolExpr(BoolExpr node);
    T visitUnaryExpr(UnaryExpr node);
    T visitBinExpr(BinExpr node);
}
