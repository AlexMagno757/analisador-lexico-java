import java.util.Set;
import java.util.EnumSet;

public class Parser {
    private Scanner scanner;
    private Token lookahead;

    private static class ParseError extends RuntimeException {}
    private boolean hadError = false;

    private ParseError error(String message) {
        hadError = true;
        System.err.println("Erro Sintático na linha " + lookahead.line + ", coluna " + lookahead.col + ": " + message);
        return new ParseError();
    }

    Parser(Scanner scanner) {
        this.scanner = scanner;
        this.lookahead = scanner.scanToken();
    }

    private void sincronizar(Set<TokenType> sync) {
        while (!sync.contains(lookahead.type) && lookahead.type != TokenType.EOF) {
            lookahead = scanner.scanToken();
        }
    }


    public void parse(){
        try {
            parsePrograma();

            if(!hadError){
                System.out.println("Análise Sintática: Código Aceito com Sucesso!");
            }
            else System.out.println("Análise Sintática concluída, mas o código contém erros listados acima.");

        }catch (ParseError e){
            System.out.println("Análise interrompida precocemente devido a erros críticos.");
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

    private void parsePrograma(){
        parseListaDecl();
        match(TokenType.EOF);
    }

    private void parseListaDecl(){
        try {
            if (lookahead.type == TokenType.FUNCAO || checkTipo()) {
                parseDeclaracao();
                parseListaDecl();
            } else if (lookahead.type == TokenType.EOF) {
                return;
            } else {
                throw error("Esperado uma declaração ('fun' ou tipo de variável), mas encontrado '" + lookahead.lexeme + "'.");
            }
        } catch (ParseError e) {
            Set<TokenType> sync = EnumSet.of(
                    TokenType.FUNCAO, TokenType.INT, TokenType.DOUBLE,
                    TokenType.CHAR, TokenType.BOOL, TokenType.TIPO_STRING, TokenType.EOF
            );
            sincronizar(sync);
            parseListaDecl();
        }
    }

    private void parseDeclaracao(){

        if(lookahead.type == TokenType.FUNCAO){
            parseDeclaracaoFuncao();
        } else if (checkTipo()) {
            parseDeclaracaoVariavel();
        }else{
            throw error("Declaração inválida.");
        }
    }

    private void parseTipo(){
        if(checkTipo()){
            match(lookahead.type);
        }else throw error("Tipo de dado esperado.");
    }

    private void parseDeclaracaoFuncao(){
       match(TokenType.FUNCAO);
       match(TokenType.IDENTIFICADOR);
       match(TokenType.ABREPAR);
       parseListaParams();
       match(TokenType.FECHAPAR);
       parseBloco();
    }

    private void parseDeclaracaoVariavel(){
        parseTipo();
        match(TokenType.IDENTIFICADOR);
        match(TokenType.ATRIBUICAO);
        parseExpressao();
        match(TokenType.PONTOVIRGULA);
    }

    private void parseListaParams(){
        if(checkTipo()){
            parseParametro();
            parseListaParamsContinua();
        }else if(lookahead.type == TokenType.FECHAPAR){
            return;
        }else throw error("Parâmetro inválido.");

    }

    private void parseListaParamsContinua(){
        if(lookahead.type == TokenType.VIRGULA){
            match(TokenType.VIRGULA);
            parseParametro();
            parseListaParamsContinua();

        } else if (lookahead.type == TokenType.FECHAPAR) {
            return;
        }else throw error("Esperado: ',' ou ')'.");
    }

    private void parseParametro(){
        parseTipo();
        match(TokenType.IDENTIFICADOR);
    }

    private void parseBloco(){
        match(TokenType.ABRECHAVE);
        parseListaCmd();
        match(TokenType.FECHACHAVE);
    }

    private void parseListaCmd(){
        try {
            if (isInicioCmd()) {
                parseComando();
                parseListaCmd();
            } else if (lookahead.type == TokenType.FECHACHAVE) {
                return;
            } else {
                throw error("Comando inválido. Encontrado: " + lookahead.lexeme);
            }
        } catch (ParseError e) {
            Set<TokenType> sync = EnumSet.of(
                    TokenType.IF, TokenType.WHILE, TokenType.FOR, TokenType.RETURN,
                    TokenType.PRINT, TokenType.IDENTIFICADOR, TokenType.INT,
                    TokenType.DOUBLE, TokenType.CHAR, TokenType.BOOL,
                    TokenType.TIPO_STRING, TokenType.PONTOVIRGULA,
                    TokenType.FECHACHAVE, TokenType.EOF
            );
            sincronizar(sync);
            if (lookahead.type == TokenType.PONTOVIRGULA) {
                lookahead = scanner.scanToken();
            }
            parseListaCmd();
        }
    }

    private void parseComando(){
        TokenType t = lookahead.type;
        if (t == TokenType.IF) parseComandoIf();
        else if (t == TokenType.WHILE) parseComandoWhile();
        else if (t == TokenType.FOR) parseComandoFor();
        else if (t == TokenType.RETURN) parseComandoReturn();
        else if (t == TokenType.PRINT) parseComandoPrint();
        else if (t == TokenType.IDENTIFICADOR) parseComandoAtribuicao();
        else if (checkTipo()) parseDeclaracaoVariavel();
        else {
            throw error("Comando não reconhecido.");
        }
    }

    private void parseComandoIf(){
        match(TokenType.IF);
        match(TokenType.ABREPAR);
        parseExpressao();
        match(TokenType.FECHAPAR);
        parseBloco();
        parseComandoIfContinua();
    }

    private void parseComandoIfContinua(){
        if(lookahead.type == TokenType.ELSE){
            match(TokenType.ELSE);
            parseBloco();
        } else if (isInicioCmd() || lookahead.type == TokenType.FECHACHAVE) {
            return;
        }else throw error("Esperado: 'else' ou continuação do código.");
    }

    private void parseComandoWhile(){
        match(TokenType.WHILE);
        match(TokenType.ABREPAR);
        parseExpressao();
        match(TokenType.FECHAPAR);
        parseBloco();
    }

    private void parseComandoFor(){
        match(TokenType.FOR);
        match(TokenType.ABREPAR);
        parseAtribuicaoFor();
        match(TokenType.PONTOVIRGULA);
        parseExpressao();
        match(TokenType.PONTOVIRGULA);
        parseAtribuicaoFor();
        match(TokenType.FECHAPAR);
        parseBloco();
    }

    private void parseAtribuicaoFor(){
        match(TokenType.IDENTIFICADOR);
        match(TokenType.ATRIBUICAO);
        parseExpressao();
    }

    private void parseComandoReturn(){
        match(TokenType.RETURN);
        parseExpressao();
        match(TokenType.PONTOVIRGULA);
    }

    private void parseComandoPrint(){
        match(TokenType.PRINT);
        match(TokenType.ABREPAR);
        parseExpressao();
        match(TokenType.FECHAPAR);
        match(TokenType.PONTOVIRGULA);
    }

    private void parseComandoAtribuicao(){
        match(TokenType.IDENTIFICADOR);
        match(TokenType.ATRIBUICAO);
        parseExpressao();
        match(TokenType.PONTOVIRGULA);
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

    private void parseExpressao(){
        parseExpOr();
    }

    private void parseExpOr(){
        parseExpAnd();
        parseExpOrContinua();
    }

    private void parseExpOrContinua(){
        if(lookahead.type == TokenType.OR){
            match(TokenType.OR);
            parseExpAnd();
            parseExpOrContinua();
        } else if (isFollowExpOr()) {
            return;
        }else throw error("Expressão malformada: Esperado operador ou fim de instrução.");
    }

    private void parseExpAnd(){
        parseExpIgualdade();
        parseExpAndContinua();
    }

    private void parseExpAndContinua(){
        if(lookahead.type == TokenType.AND){
            match(TokenType.AND);
            parseExpIgualdade();
            parseExpAndContinua();
        } else if (isFollowExpAnd()) {
            return;
        }else throw error("Expressão malformada no AND.");
    }

    private void parseExpIgualdade(){
        parseExpRelacional();
        parseExpIgualdadeContinua();
    }

    private void parseExpIgualdadeContinua(){
        TokenType t = lookahead.type;

        if(t == TokenType.IGUALDADE || t == TokenType.DIFERENTE){
            match(t);
            parseExpRelacional();
            parseExpIgualdadeContinua();
        } else if (isFollowExpIgualdade()) {
            return;
        }else throw error("Expressão malformada na Igualdade.");
    }

    private void parseExpRelacional(){
        parseExpAritmetica();
        parseExpRelacionalContinua();
    }

    private void parseExpRelacionalContinua(){
        TokenType t = lookahead.type;

        if (t == TokenType.MAIOR || t == TokenType.MAIORIGUAL || t == TokenType.MENOR || t == TokenType.MENORIGUAL){
            match(t);
            parseExpAritmetica();
            parseExpRelacionalContinua();
        } else if (isFollowExpRelacional()) {
            return;
        }else throw error("Expressão malformada no Relacional.");
    }

    private void parseExpAritmetica(){
        parseTermo();
        parseExpAritmeticaContinua();
    }

    private void parseExpAritmeticaContinua(){
        TokenType t = lookahead.type;
        if (t == TokenType.SOMA || t == TokenType.SUBTRACAO) {
            match(t);
            parseTermo();
            parseExpAritmeticaContinua();
        } else if (isFollowExpAritmetica()) {
            return;
        }else throw error("Operador inválido em Expressão Aritmética.");
    }

    private void parseTermo(){
        parseFator();
        parseTermoContinua();
    }

    private void parseTermoContinua(){
        TokenType t = lookahead.type;
        if (t == TokenType.MULTIPLICACAO || t == TokenType.DIVISAO) {
            match(t);
            parseFator();
            parseTermoContinua();
        } else if (isFollowTermo()) {
            return;
        }else throw error("Operador inválido no Termo.");
    }

    private void parseFator(){
        TokenType t = lookahead.type;

        if (t == TokenType.NUMERO || t == TokenType.IDENTIFICADOR || t == TokenType.STRING || t == TokenType.TRUE || t == TokenType.FALSE) {
            match(t);
        }
        else if (t == TokenType.NEGACAO) {
            match(TokenType.NEGACAO);
            parseFator();
        }
        else if (t == TokenType.ABREPAR) {
            match(TokenType.ABREPAR);
            parseExpressao();
            match(TokenType.FECHAPAR);
        }
        else {
            throw error("Fator inválido. Esperado: Número, Identificador, String, Booleano, '!' ou '('. Encontrado: " + lookahead.lexeme);
        }
    }

    public boolean hasError() {
        return this.hadError;
    }
}
