import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    public String source;
    public int start;
    public int current;
    public int line,col;
    public List<Token> tokens;

    private static final Map<String, TokenType> palavrasReservadas;

    static {
        palavrasReservadas = new HashMap<>();
        palavrasReservadas.put("print", TokenType.PRINT);
        palavrasReservadas.put("if", TokenType.IF);
        palavrasReservadas.put("else", TokenType.ELSE);
        palavrasReservadas.put("while", TokenType.WHILE);
        palavrasReservadas.put("for", TokenType.FOR);
        palavrasReservadas.put("true", TokenType.TRUE);
        palavrasReservadas.put("false", TokenType.FALSE);
        palavrasReservadas.put("return", TokenType.RETURN);
    }

    public Scanner(String source) {
        this.source = source;
        this.start = 0;
        this.current = 0;
        this.line = 1;
        this.col = 1;
        this.tokens = new ArrayList<>();
    }

    public boolean hasNext() {
        return current < source.length();
    }

    public char peek(){
        if(current >= source.length()) return '\0' ;

        return source.charAt(current);
    }


    public char advance(){
        char c = source.charAt(current++);
        if( c == '\n'){
            line++;
            col=1;
        }else{
            col++;
        }
        return c;
    }


    public void skipWhitespaceAndComments(){
        while(hasNext()){
            char c = peek();

            switch (c){
                case ' ':
                case '\r':
                case '\t':
                case '\n':
                    advance();
                    break;

                case '#':
                    while (hasNext() && peek() != '\n') advance();
                    break;

                default:
                    return;
            }

        }
    }

    public boolean isDigit(char c){
        return ( c >= '0' && c <='9');
    }

    public boolean isLetter(char c){
        return ((c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c == '_'));
    }

    public boolean isAlphaNumeric(char c){
        return isLetter(c) || isDigit(c);
    }

    public boolean isOperatorStart(char c){
        return (c == '+' || c == '-' || c == '*' || c =='/' ||
                c == '>' || c == '<' || c == '=' || c == '!' ||
                c== '&' || c == '|');
    }

    public boolean isSimbol(char c){
        return (c == '(' || c == ')' || c== '{' || c== '}' ||
                c== ',' || c == ';' );
    }

    private enum State {
        S0, S1, S2, S3
    }

    public Token scanIdentifier(){
        State state= State.S0;
        while(true){
            char c = peek();
            switch(state){
                case S0:
                    if(isLetter(c)){
                        advance();
                        state= State.S1;
                    }//else throw new LexicalException("Erro léxico: Esperava-se uma letra na linha " + line + " coluna " + col);
                    break;

                case S1:
                    if(isAlphaNumeric(c)) advance();
                    else{
                        String lexeme = source.substring(start, current);
                        TokenType type = palavrasReservadas.getOrDefault(lexeme, TokenType.IDENTIFICADOR);
                        return new Token(type,lexeme,line,col);
                    }
                    break;
            }
        }
    }

    public Token scanString(){
        State state= State.S0;
        int colunaInicial = col;

        while (true){
            char c = peek();

            switch (state){
                case S0:
                    if(c == '"'){
                        advance();
                        state = State.S1;
                    }
                    break;

                case S1:
                    if (!hasNext() || c == '\n'){
                        throw new LexicalException("Erro léxico: String iniciada na linha " + line + " (coluna " + colunaInicial + ") não foi fechada.");
                    } else if (c=='\\') {
                        advance();
                        advance();
                        
                    } else if( c != '"'){
                        advance();

                    }else {
                        advance();
                        String lexeme = source.substring(start,current);
                        return new Token(TokenType.STRING,lexeme,line,colunaInicial);
                    }
                    break;

            }
        }
    }

    public Token scanNumber(){
        State state= State.S0;

        while (true){
            char c = peek();

            switch (state){
                case S0:
                    if(isDigit(c)){
                        advance();
                        state = State.S1;
                    }
                    break;

                case S1:
                    if(c == '.'){
                        state= State.S2;
                        advance();
                    }else if (isDigit(c)){
                        advance();
                    }else{
                        if(isLetter(c)){
                            throw new LexicalException("Erro léxico: Número mal formatado com a letra '" + c + "' na linha " + line + " coluna " + col);
                        }
                        String lexeme = source.substring(start,current);
                        return new Token(TokenType.NUMERO, lexeme, line, col);
                    }
                    break;

                case S2:
                    if(isDigit(c)){
                        advance();
                        state =State.S3;
                    }
                    else{
                        throw new LexicalException("Esperava dígito na linha " + line + " coluna " + col );
                    }
                    break;


                case S3:
                    if(isDigit(c)){
                        advance();
                    }else{
                        if (isLetter(c)) {
                            throw new LexicalException("Erro léxico: Número mal formatado com a letra '" + c + "' na linha " + line + " coluna " + col);
                        }
                        String lexeme = source.substring(start,current);
                        return new Token(TokenType.NUMERO, lexeme, line, col);
                    }
                    break;
            }
        }
    }

    private TokenType determineTypeOp(String lexeme) {
        switch (lexeme) {
            case "+": return TokenType.SOMA;
            case "-": return TokenType.SUBTRACAO;
            case "*": return TokenType.MULTIPLICACAO;
            case "/": return TokenType.DIVISAO;
            case "=": return TokenType.ATRIBUICAO;
            case "==": return TokenType.IGUALDADE;
            case "<": return TokenType.MENOR;
            case "<=": return TokenType.MENORIGUAL;
            case ">": return TokenType.MAIOR;
            case ">=": return TokenType.MAIORIGUAL;
            case "!": return TokenType.NEGACAO;
            case "!=": return TokenType.DIFERENTE;
            case "&": return  TokenType.AND;
            case "|": return TokenType.OR;
            default:
                throw new LexicalException("Operador desconhecido: " + lexeme);
        }
    }

    public Token scanOperator(){
        State state = State.S0;

        while(true){
            char c = peek();

            switch (state){

                case S0:
                    if(c== '+'|| c=='-'|| c=='*' || c=='/' || c=='&' || c=='|'){
                        advance();
                        String lexeme = source.substring(start,current);
                        return new Token(determineTypeOp(lexeme), lexeme, line, col);
                    } else if (c== '=' || c== '!' || c=='<' || c=='>'){
                        state = State.S1;
                        advance();
                    }
                    break;

                case S1:
                    if(c == '='){
                        advance();
                        String lexeme = source.substring(start,current);
                        return new Token(determineTypeOp(lexeme), lexeme, line, col);
                    }else {
                        String lexeme = source.substring(start, current);
                        return new Token(determineTypeOp(lexeme), lexeme, line, col);
                    }

            }
        }

    }

    public Token scanSimbols() {
        char c = advance();
        String lexeme = source.substring(start, current);

        switch (c) {
            case '(': return new Token(TokenType.ABREPAR, lexeme, line, col);
            case ')': return new Token(TokenType.FECHAPAR, lexeme, line, col);
            case '{': return new Token(TokenType.ABRECHAVE, lexeme, line, col);
            case '}': return new Token(TokenType.FECHACHAVE, lexeme, line, col);
            case ';': return new Token(TokenType.PONTOVIRGULA, lexeme, line, col);
            case ',': return new Token(TokenType.VIRGULA, lexeme, line, col);
            default:
                throw new LexicalException("Símbolo de pontuação desconhecido: " + c + " na linha " + line + " coluna " + col);
        }
    }



    public Token scanToken(){
        skipWhitespaceAndComments();
        if(!hasNext()){
            return new Token(TokenType.EOF, "", line, col);
        }
        start = current;
        char c = peek();
        if(isLetter(c)){
            return scanIdentifier();
        }
        if(c =='"'){
            return scanString();
        }
        if(isDigit(c)){
            return scanNumber();
        }
        if(isOperatorStart(c)){
            return scanOperator();
        }
        if (isSimbol(c)){
            return scanSimbols();
        }

        throw new LexicalException("\nErro Léxico: \nCaractere inesperado: (" + c + ") na linha "+ line+ " coluna " +col);
    }

    public List<Token> scanAllTokens() {
        Token token;
        do {
            token = scanToken();
            tokens.add(token);
        } while (token.type != TokenType.EOF);
        return tokens;
    }
}


