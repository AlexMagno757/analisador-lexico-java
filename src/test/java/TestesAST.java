import lexer.Scanner;
import parser.Parser;
import ast.Program;
import visitor.ASTPrinter;
import visitor.SourcePrinter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class TestesAST {
    public static void main(String[] args) {
        System.out.println("=== TESTES DE ESTRUTURA DA AST (CP3) ===");
        //testarParentesesRedundantes();
        //testarPrecedenciaAssociatividade();
        //testarRoundTrip();
        testarASTArquivos();
    }

    private static Program executar(String source) {

        return new Parser(new Scanner(source)).parse();
    }

    private static void testarParentesesRedundantes() {
        System.out.println("\n--- 1. Parênteses Redundantes ---");
        Program astSem = executar("int x = a;");
        Program astCom = executar("int x = (((a)));");

        String strSem = astSem.accept(new ASTPrinter());
        String strCom = astCom.accept(new ASTPrinter());

        System.out.println("Árvore SEM parênteses:\n");
        System.out.println(strSem);
        System.out.println("---------------------------------");
        System.out.println("Árvore COM parênteses:\n");
        System.out.println(strCom);
        System.out.println("---------------------------------");

        if (strSem.equals(strCom)) {
            System.out.println("[PASSOU] Árvore de (((a))) é idêntica à de a.");
        } else {
            System.out.println("[FALHOU] As árvores ficaram diferentes.");
        }
    }

    private static void testarPrecedenciaAssociatividade() {
        System.out.println("\n--- 2. Precedência e Associatividade ---");

        Program p1 = executar("int x = a + b * c;");
        Program p2 = executar("int x = a - b - c;");
        Program p3 = executar("int x = a - (b - c);");

        ASTPrinter impressor = new ASTPrinter();
        String a1 = p1.accept(impressor);
        String a2 = p2.accept(impressor);
        String a3 = p3.accept(impressor);

        if (!a2.equals(a3)) {
            System.out.println("[PASSOU] a - b - c gerou árvore DIFERENTE de a - (b - c).");
            System.out.println("Árvore a - b - c:\n"+ a2);
            System.out.println("Árvore a - (b - c):\n"+ a3);
            System.out.println("Visualização da precedência a + b * c:\n" + a1);
        } else {
            System.out.println("[FALHOU] Associatividade não funcionou.");
        }
    }

    private static void testarRoundTrip() {
        System.out.println("\n--- 3. Round-Trip ---");
        String codigo = "fun calc(int a) { return a * 2; }";

        Program ast1 = executar(codigo);
        String texto1 = ast1.accept(new SourcePrinter());

        Program ast2 = executar(texto1);
        String texto2 = ast2.accept(new SourcePrinter());

        if (texto1.equals(texto2)) {
            System.out.println("[PASSOU] O texto reimpresso é idêntico na 2ª passagem.\n");
            System.out.println("Texto 1: " + texto1);
            System.out.println("Texto 2: " + texto2);
        } else {
            System.out.println("[FALHOU] Os textos não batem.\n");
            System.out.println("Texto 1: " + texto1);
            System.out.println("Texto 2: " + texto2);
        }
    }

    private static void testarASTArquivos() {
        System.out.println("\n--- 4. AST x Arquivo de Texto Esperado ---");
        String prog1 = "int a = 5; double b = 3.14; bool c = true;";
        verificarArquivo(prog1, "src/test/esperado/teste1.txt", "Teste 1 (Tipos)");

        String prog2 = "fun controle() { if (a > 0) { a = 1; } else { a = 0; } }";
        verificarArquivo(prog2, "src/test/esperado/teste2.txt", "Teste 2 (Controle)");

        String prog3 = "fun soma(int x, int y) { return x + y; }";
        verificarArquivo(prog3, "src/test/esperado/teste3.txt", "Teste 3 (Funções)");
    }

    private static void verificarArquivo(String codigo, String caminho, String nomeTeste) {
        Program ast = executar(codigo);
        String arvoreGerada = ast.accept(new ASTPrinter());

        try {
            String arvoreEsperada = Files.readString(Paths.get(caminho));

            String geradaNorm = arvoreGerada.trim().replace("\r", "");
            String esperadaNorm = arvoreEsperada.trim().replace("\r", "");

            if (geradaNorm.equals(esperadaNorm)) {
                System.out.println("[PASSOU] " + nomeTeste + " - Árvore gerada bate com o arquivo!");
            } else {
                System.out.println("[FALHOU] " + nomeTeste + " - A árvore gerada é diferente do arquivo.");
                System.out.println("Esperada:\n" + esperadaNorm + "\nGerada:\n" + geradaNorm);
            }
        } catch (IOException e) {
            System.out.println("\n[AVISO DE SETUP] Arquivo '" + caminho + "' não encontrado!");
        }
    }
}