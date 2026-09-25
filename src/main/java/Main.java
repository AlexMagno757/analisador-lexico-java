public class Main {
    public static void main(String[] args) {
        String codigo ="print 22.54+32 &";
        Scanner scanner = new Scanner(codigo);
        Token token;

        do {
            token = scanner.scanToken();
            System.out.println("Token lido: " + token.type + " | Lexema: '" + token.lexeme + "'");
        } while (token.type != TokenType.EOF);
    }
}