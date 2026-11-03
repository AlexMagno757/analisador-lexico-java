package parser;

import ast.Program;
import ast.cmd.*;
import ast.decl.*;
import ast.expr.*;
import lexer.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.EnumSet;

public class Parser {
    private Scanner scanner;
    private Token lookahead;

    private static class ParseError extends RuntimeException {}
    private boolean hadError = false;

    private ParseError error(String message) {
        hadError = true;
        System.out.println("Erro Sintático na linha " + lookahead.line + ", coluna " + lookahead.col + ": " + message);
        return new ParseError();
    }

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.lookahead = scanner.scanToken();
    }

    private void sincronizar(Set<TokenType> sync) {
        while (!sync.contains(lookahead.type) && lookahead.type != TokenType.EOF) {
            lookahead = scanner.scanToken();
        }
    }


    public Program parse(){
        try {
            Program raiz = parsePrograma();

            if(!hadError){
                //System.out.println("Análise Sintática: Código Aceito com Sucesso!");
                return raiz;
            }
            else {
                System.out.println("Análise Sintática concluída, mas o código contém erros listados acima.");
                return null;
            }

        }catch (ParseError e){
            System.out.println("Análise interrompida precocemente devido a erros críticos.");
            return null;
        }
    }

    private void match(TokenType expected){
        if(lookahead.type ==  expected){
            lookahead = scanner.scanToken();
        }else {
            throw error("Esperado '" + expected + "', mas encontrado '" + lookahead.type + "' (" + lookahead.lexeme + ").");
        }
    }

    private boolean isInicioCmd() {
        TokenType t = lookahead.type;
        return t == TokenType.IF || t == TokenType.WHILE || t == TokenType.FOR ||
                t == TokenType.RETURN || t == TokenType.PRINT ||
                t == TokenType.IDENTIFICADOR || checkTipo();
    }

    private boolean checkTipo() {
        return lookahead.type == TokenType.INT ||
                lookahead.type == TokenType.DOUBLE ||
                lookahead.type == TokenType.CHAR ||
                lookahead.type == TokenType.BOOL ||
                lookahead.type == TokenType.TIPO_STRING;
    }

    private Program parsePrograma(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        List< Decl> declaracoes = parseListaDecl();
        match(TokenType.EOF);
        return new Program(declaracoes, linha, coluna);
    }

    private List<Decl> parseListaDecl(){
        List<Decl> lista = new ArrayList<>();
        while (lookahead.type != TokenType.EOF) {
            try {
                if (lookahead.type == TokenType.FUNCAO || checkTipo()) {
                    Decl declaracao = parseDeclaracao();
                    if (declaracao != null) {
                        lista.add(declaracao);
                    }
                } else {
                    throw error("Esperado uma declaração ('fun' ou tipo de variável), mas encontrado '" + lookahead.lexeme + "'.");
                }
            } catch (ParseError e) {
                Set<TokenType> sync = EnumSet.of(
                        TokenType.FUNCAO, TokenType.INT, TokenType.DOUBLE,
                        TokenType.CHAR, TokenType.BOOL, TokenType.TIPO_STRING, TokenType.EOF
                );
                sincronizar(sync);
            }
        }
        return lista;
    }

    private Decl parseDeclaracao(){

        if(lookahead.type == TokenType.FUNCAO){
            return parseDeclaracaoFuncao();
        } else if (checkTipo()) {
            return parseDeclaracaoVariavel();
        }else{
            throw error("Declaração inválida.");
        }
    }

    private String parseTipo(){
        if(checkTipo()){
            String tipo = lookahead.lexeme;
            match(lookahead.type);
            return tipo;
        }else{
            throw error("Tipo de dado esperado.");
        }
    }

    private FunDecl parseDeclaracaoFuncao(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        match(TokenType.FUNCAO);
        String id = lookahead.lexeme;
        match(TokenType.IDENTIFICADOR);
        match(TokenType.ABREPAR);
        List<Param> params = parseListaParams();
        match(TokenType.FECHAPAR);
        BlockCmd body = parseBloco();

        return new FunDecl(id,params,body, linha, coluna);
    }
    private VarDecl parseDeclaracaoVariavel(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        String tipo = parseTipo();
        String id = lookahead.lexeme;
        match(TokenType.IDENTIFICADOR);

        Expr expr = null;
        if(lookahead.type == TokenType.ATRIBUICAO) {
            match(TokenType.ATRIBUICAO);
            expr = parseExpressao();
        }
        match(TokenType.PONTOVIRGULA);
        return new VarDecl(tipo, id , expr, linha,coluna);
    }

    private List<Param> parseListaParams(){
        List<Param> params = new ArrayList<>();

        if(checkTipo()){
            params.add(parseParametro());
            parseListaParamsContinua(params);

        }else if(lookahead.type == TokenType.FECHAPAR) {
            return params;

        }else throw error("Parâmetro inválido.");

        return params;
    }

    private void parseListaParamsContinua(List<Param> params){
        if(lookahead.type == TokenType.VIRGULA){
            match(TokenType.VIRGULA);
            params.add(parseParametro());
            parseListaParamsContinua(params);

        } else if (lookahead.type == TokenType.FECHAPAR) {
            return;
        }else throw error("Esperado: ',' ou ')'.");
    }

    private Param parseParametro(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        String tipo = parseTipo();
        String id = lookahead.lexeme;
        match(TokenType.IDENTIFICADOR);

        return  new Param(tipo,id, linha, coluna);
    }

    private BlockCmd parseBloco(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        match(TokenType.ABRECHAVE);

        List<Cmd> cmds = new ArrayList<>();
        parseListaCmd(cmds);
        match(TokenType.FECHACHAVE);

        return new BlockCmd(cmds,linha, coluna);
    }

    private void parseListaCmd(List<Cmd> cmds){
        try {
            if (isInicioCmd()) {
                Cmd  comando = parseComando();
                if(comando != null) cmds.add(comando);

                parseListaCmd(cmds);

            } else if (lookahead.type == TokenType.FECHACHAVE) {
                return;
            } else {
                throw error("Comando inválido. Encontrado: " + lookahead.lexeme);
            }
        } catch (ParseError e) {
            Set<TokenType> sync = EnumSet.of(
                    TokenType.IF, TokenType.WHILE, TokenType.FOR, TokenType.RETURN,
                    TokenType.PRINT, TokenType.PONTOVIRGULA, TokenType.FECHACHAVE, TokenType.EOF
            );
            sincronizar(sync);
            if (lookahead.type == TokenType.PONTOVIRGULA) {
                lookahead = scanner.scanToken();
            }

            if (lookahead.type == TokenType.EOF) {
                return;
            }
            parseListaCmd(cmds);
        }
    }

    private Cmd parseComando(){
        TokenType t = lookahead.type;
        if (t == TokenType.IF) return parseComandoIf();
        else if (t == TokenType.WHILE) return parseComandoWhile();
        else if (t == TokenType.FOR) return parseComandoFor();
        else if (t == TokenType.RETURN) return parseComandoReturn();
        else if (t == TokenType.PRINT) return parseComandoPrint();
        else if (t == TokenType.IDENTIFICADOR) return parseComandoAtribuicao();
        else if (checkTipo()) {
            VarDecl declaracao = parseDeclaracaoVariavel();
            return new VarDeclCmd(declaracao);
        }
        else {
            throw error("Comando não reconhecido.");
        }
    }

    private IfCmd parseComandoIf(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        match(TokenType.IF);
        match(TokenType.ABREPAR);
        Expr condition = parseExpressao();
        match(TokenType.FECHAPAR);
        BlockCmd thenBlock = parseBloco();
        BlockCmd elseBlock = parseComandoIfContinua();

        return new IfCmd(condition, thenBlock, elseBlock, linha, coluna);
    }

    private BlockCmd parseComandoIfContinua(){
        if(lookahead.type == TokenType.ELSE){
            match(TokenType.ELSE);
            return parseBloco();
        } else if (isInicioCmd() || lookahead.type == TokenType.FECHACHAVE) {
            return null;
        }else throw error("Esperado: 'else' ou continuação do código.");
    }

    private WhileCmd parseComandoWhile(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        match(TokenType.WHILE);
        match(TokenType.ABREPAR);
        Expr condition = parseExpressao();
        match(TokenType.FECHAPAR);
        BlockCmd body = parseBloco();

        return new WhileCmd(condition, body, linha, coluna);
    }

    private ForCmd parseComandoFor(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        match(TokenType.FOR);
        match(TokenType.ABREPAR);
        AtribCmd init = parseAtribuicaoFor();
        match(TokenType.PONTOVIRGULA);
        Expr condition = parseExpressao();
        match(TokenType.PONTOVIRGULA);
        AtribCmd update = parseAtribuicaoFor();
        match(TokenType.FECHAPAR);
        BlockCmd body = parseBloco();

        return new ForCmd(init, condition, update, body, linha, coluna);
    }

    private AtribCmd parseAtribuicaoFor(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        String id = lookahead.lexeme;
        match(TokenType.IDENTIFICADOR);
        match(TokenType.ATRIBUICAO);
        Expr expr = parseExpressao();

        return new AtribCmd(id, expr, linha, coluna);
    }

    private ReturnCmd parseComandoReturn(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        match(TokenType.RETURN);
        Expr expr = parseExpressao();
        match(TokenType.PONTOVIRGULA);

        return new ReturnCmd(expr, linha, coluna);
    }

    private PrintCmd parseComandoPrint(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        match(TokenType.PRINT);
        match(TokenType.ABREPAR);
        Expr expr = parseExpressao();
        match(TokenType.FECHAPAR);
        match(TokenType.PONTOVIRGULA);

        return new PrintCmd(expr, linha, coluna);
    }

    private AtribCmd parseComandoAtribuicao(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        String id = lookahead.lexeme;
        match(TokenType.IDENTIFICADOR);
        match(TokenType.ATRIBUICAO);
        Expr expr = parseExpressao();
        match(TokenType.PONTOVIRGULA);

        return new AtribCmd(id, expr, linha, coluna);
    }

    private boolean isFollowExpOr() {
        TokenType t = lookahead.type;
        return t == TokenType.FECHAPAR || t == TokenType.PONTOVIRGULA;
    }

    private boolean isFollowExpAnd() { return lookahead.type == TokenType.OR || isFollowExpOr(); }

    private boolean isFollowExpIgualdade() { return lookahead.type == TokenType.AND || isFollowExpAnd(); }

    private boolean isFollowExpRelacional() {
        TokenType t = lookahead.type;
        return t == TokenType.IGUALDADE || t == TokenType.DIFERENTE || isFollowExpIgualdade();
    }

    private boolean isFollowExpAritmetica() {
        TokenType t = lookahead.type;
        return t == TokenType.MAIOR || t == TokenType.MAIORIGUAL ||
                t == TokenType.MENOR || t == TokenType.MENORIGUAL || isFollowExpRelacional();
    }

    private boolean isFollowTermo() {
        TokenType t = lookahead.type;
        return t == TokenType.SOMA || t == TokenType.SUBTRACAO || isFollowExpAritmetica();
    }

    private Expr parseExpressao(){
        return parseExpOr();
    }

    private Expr parseExpOr(){
        Expr left = parseExpAnd();
        return parseExpOrContinua(left);
    }

    private Expr parseExpOrContinua(Expr left){
        if(lookahead.type == TokenType.OR){
            String op = lookahead.lexeme;
            match(TokenType.OR);
            Expr right = parseExpAnd();
            Expr novaEsquerda = new BinExpr(left, op, right, left.linha, left.coluna);
            return parseExpOrContinua(novaEsquerda);

        } else if (isFollowExpOr()) {
            return left;
        }else throw error("Expressão malformada: Esperado operador ou fim de instrução.");
    }

    private Expr parseExpAnd(){
        Expr left = parseExpIgualdade();
        return parseExpAndContinua(left);
    }

    private Expr parseExpAndContinua(Expr left){
        if(lookahead.type == TokenType.AND){
            String op = lookahead.lexeme;
            match(TokenType.AND);
            Expr right = parseExpIgualdade();
            Expr novaEsquerda = new BinExpr(left, op, right, left.linha, left.coluna);
            return parseExpAndContinua(novaEsquerda);

        } else if (isFollowExpAnd()) {
            return left;
        }else throw error("Expressão malformada no AND.");
    }

    private Expr parseExpIgualdade(){
        Expr left = parseExpRelacional();
        return parseExpIgualdadeContinua(left);
    }

    private Expr parseExpIgualdadeContinua(Expr left){
        TokenType t = lookahead.type;

        if(t == TokenType.IGUALDADE || t == TokenType.DIFERENTE){
            String op = lookahead.lexeme;
            match(t);
            Expr right = parseExpRelacional();

            Expr novaEsquerda = new BinExpr(left, op, right, left.linha, left.coluna);
            return parseExpIgualdadeContinua(novaEsquerda);

        } else if (isFollowExpIgualdade()) {
            return left;
        }else throw error("Expressão malformada na Igualdade.");
    }

    private Expr parseExpRelacional(){
        Expr left = parseExpAritmetica();
        return parseExpRelacionalContinua(left);
    }

    private Expr parseExpRelacionalContinua(Expr left){
        TokenType t = lookahead.type;

        if (t == TokenType.MAIOR || t == TokenType.MAIORIGUAL || t == TokenType.MENOR || t == TokenType.MENORIGUAL){
            String op = lookahead.lexeme;
            match(t);
            Expr right = parseExpAritmetica();

            Expr novaEsquerda = new BinExpr(left, op, right, left.linha, left.coluna);
            return parseExpRelacionalContinua(novaEsquerda);
        } else if (isFollowExpRelacional()) {
            return left;
        }else throw error("Expressão malformada no Relacional.");
    }

    private Expr parseExpAritmetica(){
        Expr left = parseTermo();
        return parseExpAritmeticaContinua(left);
    }

    private Expr parseExpAritmeticaContinua(Expr left){
        TokenType t = lookahead.type;
        if (t == TokenType.SOMA || t == TokenType.SUBTRACAO) {
            String op = lookahead.lexeme;
            match(t);
            Expr right = parseTermo();

            Expr novaEsquerda = new BinExpr(left, op, right, left.linha, left.coluna);
            return parseExpAritmeticaContinua(novaEsquerda);
        } else if (isFollowExpAritmetica()) {
            return left;
        }else throw error("Operador inválido em Expressão Aritmética.");
    }

    private Expr parseTermo(){
        Expr left = parseFator();
        return parseTermoContinua(left);
    }

    private Expr parseTermoContinua(Expr left){
        TokenType t = lookahead.type;
        if (t == TokenType.MULTIPLICACAO || t == TokenType.DIVISAO) {
            String op = lookahead.lexeme;
            match(t);
            Expr right = parseFator();

            Expr novaEsquerda = new BinExpr(left, op, right, left.linha, left.coluna);
            return parseTermoContinua(novaEsquerda);
        } else if (isFollowTermo()) {
            return left;
        }else throw error("Operador inválido no Termo.");
    }

    private Expr parseFator(){
        int linha = lookahead.line;
        int coluna = lookahead.col;

        TokenType t = lookahead.type;
        String lexeme = lookahead.lexeme;

        if (t == TokenType.NUMERO) {
            match(t);
            return new NumExpr(lexeme, linha, coluna);

        } else if (t == TokenType.IDENTIFICADOR) {
            match(t);
            return new IdExpr(lexeme, linha, coluna);

        } else if (t == TokenType.STRING) {
            match(t);
            return new StringExpr(lexeme, linha, coluna);

        } else if (t == TokenType.TRUE || t == TokenType.FALSE) {
            match(t);
            return new BoolExpr(lexeme, linha, coluna);

        } else if (t == TokenType.NEGACAO) {
            match(TokenType.NEGACAO);
            Expr expr = parseFator();
            return new UnaryExpr("!", expr, linha, coluna);

        } else if (t == TokenType.ABREPAR) {
            match(TokenType.ABREPAR);
            Expr expr = parseExpressao();
            match(TokenType.FECHAPAR);
            return expr;

        } else {
            throw error("Fator inválido. Esperado: Número, Identificador, String, Booleano, '!' ou '('. Encontrado: " + lookahead.lexeme);
        }
    }

}
