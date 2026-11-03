import ast.Program;
import lexer.Scanner;
import parser.Parser;

public class MainParseTestes {
    public static void main(String[] args) {
        System.out.println("--------- TESTES CP2 ----------");

        System.out.println("\nTESTES VÁLIDOS");
        testesValidos();

        System.out.println("\nTESTES INVÁLIDOS");
        testesInvalidos();

        System.out.println("\nMODO PÂNICO");
        testeModoPanico();
    }

    private static Program executarParser(String source) {
        Scanner scanner = new Scanner(source);
        Parser parser = new Parser(scanner);
        System.out.println("--------------------------------------------------");
        return parser.parse();
    }

    private static void testesValidos() {
        String valido = "int a = 10; double c = 3.14;";
        Program ast = executarParser(valido);

        if (ast == null) {
            throw new RuntimeException("Falha: Programa válido retornou null.");
        }
        System.out.println("testesValidos Passou - AST gerada corretamente.");
    }

    private static void testesInvalidos() {
        String invalido = "fun erroIf() { if x > 5 ) { print(x); } }";
        Program ast = executarParser(invalido);

        if (ast != null) {
            throw new RuntimeException("Falha: Programa inválido deve retornar null.");
        }
        System.out.println("testesInvalidos Passou - Árvore rejeitada corretamente.");
    }

    private static void testeModoPanico(){
        String panico = "int a = 5 + ; fun testar() { print \"Ola\"; }";
        Program ast = executarParser(panico);

        if (ast != null) {
            throw new RuntimeException("Falha: Modo pânico deve retornar null no final.");
        }
        System.out.println("testeModoPanico Passou - Erros recuperados, mas árvore rejeitada.");
    }
}
