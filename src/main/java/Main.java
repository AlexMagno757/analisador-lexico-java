public class Main {
    public static void main(String[] args) {
        String codigo ="""
                        #! 
                           Arquivo de Teste Oficial do Scanner
                           Projeto Compiladores - UFU
                        !#
                        
                        # Declaracao de variaveis
                        int idade = 21;
                        double salario = 2500.50;
                        string nome = "Alex Magno";
                        bool ativo = true;
                        
                        # Teste de operadores relacionais e logicos
                        if (idade >= 18 && salario != 0.0) {
                            print "Usuario valido";
                        } else {
                            print "Invalido";
                        }
                        
                        # Teste de loops e operacoes matematicas
                        while (ativo == true || idade <= 30) {
                            idade = idade + 1;
                            salario = salario * 1.5; 
                        }
                        
                        return 0;
                        """;


        Scanner scanner = new Scanner(codigo);
        Token token;

        do {
            token = scanner.scanToken();
            System.out.println("Token lido: " + token.type + " | Lexema: '" + token.lexeme + "'");
        } while (token.type != TokenType.EOF);
    }
}