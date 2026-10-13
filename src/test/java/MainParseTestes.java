public class MainParseTestes {
    public static void main(String[] args) {
        //testesValidos();
        //testesInvalidos();
        testeModoPanico();
    }

    private static void executarParser(String source) {
        Scanner scanner = new Scanner(source);
        Parser parser = new Parser(scanner);
        parser.parse();
        System.out.println("--------------------------------------------------");
    }

    private static void testesValidos() {
        String valido1 = "int a = 10; string b = \"texto\"; double c = 3.14;";
        executarParser(valido1);

        String valido2 = """
            fun testarFluxo() {
                if (a == 10) { print(a); } else { a = 0; }
                while (a > 0) { a = a - 1; }
            }
            """;
        executarParser(valido2);

        String valido3 = """
            fun calcular(int x, double y) {
                double res = x + y * 2 / (3 - 1);
                return res;
            }
            """;
        executarParser(valido3);
    }

    private static void testesInvalidos() {
        String invalido1 = """
            fun erroIf() {
                if x > 5 ) { print(x); }
            }
            """;
        executarParser(invalido1);

        String invalido2 = """
            int x = 10
            string nome = "Maria";
            """;
        executarParser(invalido2);

        String invalido3 = """
            fun somar(int a, b) {
                return a + b;
            }
            """;
        //executarParser(invalido3);
    }

    private static void testeModoPanico(){
        String panico = """
            int a = 5 + ; # Erro 1: Falta o número depois do '+'
            
            fun testar() {
                print "Ola"; # Erro 2: Falta os parênteses no print
                
                while () { # Erro 3: Condição vazia no while
                    a = a + 1;
                }
            }
            """;
        executarParser(panico);
    }
}
