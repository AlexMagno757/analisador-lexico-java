public class MainTestes {
    public static void main(String[] args) {
        //testarCasosValidos();
        //testarCasosDeErro();
        testarCodigoRealista();
    }

    private static void testarCasosValidos() {

        String source = "int idade = 21; string nome = \"Alex\";";

        Scanner scanner = new Scanner(source);
        Token token;

        do {
            token = scanner.scanToken();
            System.out.println("Token lido: " + token.type + " | Lexema: '" + token.lexeme + "'");
        } while (token.type != TokenType.EOF);
    }

    private static void testarCasosDeErro() {

        String source = "@\n" +
                "string falha = \"texto quebrado\n" +
                "int y = 5;\n" +
                "string fim = \"texto ate o EOF";

        Scanner scanner = new Scanner(source);
        Token token;

        do {
            token = scanner.scanToken();
            System.out.println("Token lido: " + token.type + " | Lexema: '" + token.lexeme + "'");
        } while (token.type != TokenType.EOF);
    }

    private static void testarCodigoRealista() {

        String source = """
            # Funcao principal
            fun main() {
                int x = 10;
                #! Comentario
                   de Bloco !#
                if (x >= 5) {
                    print("maior");
                }
            }
            """;

        Scanner scanner = new Scanner(source);
        Token token;

        do {
            token = scanner.scanToken();
            System.out.println("Token lido: " + token.type + " | Lexema: '" + token.lexeme + "'");
        } while (token.type != TokenType.EOF);

    }

}
