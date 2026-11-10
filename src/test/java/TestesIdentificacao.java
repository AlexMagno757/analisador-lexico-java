import lexer.Scanner;
import parser.Parser;
import ast.Program;
import semantic.StackSymbolTable;
import semantic.Symbol;
import visitor.IdentificationVisitor;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class TestesIdentificacao {

    public static void main(String[] args) {
        System.out.println("----- Tabelas Isoladas -----");
        testarTabelaIsolada();
        System.out.println("----- Arquivos -----");
        testarArquivosObrigatorios();
        System.out.println("----- Programas -----");
        testarProgramasExtras();
    }

    private static IdentificationVisitor executarAnalise(String source) {
        Program ast = new Parser(new Scanner(source)).parse();
        if (ast == null) return null;

        IdentificationVisitor visitor = new IdentificationVisitor();
        ast.accept(visitor);
        return visitor;
    }

    private static void check(boolean condicao, String nomeDoTeste) {
        System.out.println((condicao ? "[PASSOU] " : "[FALHOU] ") + nomeDoTeste);
    }

    private static void testarTabelaIsolada() {
        StackSymbolTable table = new StackSymbolTable();
        check(table.level() == -1, "Level inicial é -1");

        table.openScope();
        Symbol s1 = new Symbol("x", Symbol.Kind.VAR, null, table.level());

        check(table.declare(s1), "Declaração inédita aceita");
        check(!table.declare(new Symbol("x", Symbol.Kind.CONST, null, table.level())), "Redeclaração bloqueada");

        table.openScope();
        check(table.lookup("x") == s1, "Lookup normal acha variável global");
        check(table.lookupLocal("x") == null, "LookupLocal ignora variável global");
    }

    private static void testarArquivosObrigatorios() {
        verificarArquivo("int x = 10; \n fun principal() { int x = 20; }",
                "src/test/esperado2/teste1.txt", "Teste 1 (Sombreamento)");

        verificarArquivo("int x = 1; \n int x = 2;",
                "src/test/esperado2/teste2.txt", "Teste 2 (Redeclaração)");

        verificarArquivo("fun principal() { print(y); }",
                "src/test/esperado2/teste3.txt", "Teste 3 (Não Declarado)");
    }

    private static void testarProgramasExtras() {
        IdentificationVisitor v1 = executarAnalise("fun principal() { int a = 1; \n int a = 2; \n print(b); \n print(c); \n}");
        check(v1 != null && v1.errors.size() == 3, "Acumulou 3 erros sem travar");
        if (v1 != null) {
            for (String erro : v1.errors) System.out.println("  -> " + erro);
        }

        IdentificationVisitor v2 = executarAnalise("fun contar() { \n print(contar); \n }");
        check(v2 != null && v2.errors.isEmpty(), "Função recursiva aceita sem erros");

        IdentificationVisitor v3 = executarAnalise("fun principal() { if (true) { int z = 10; } \n print(z); }");
        check(v3 != null && v3.errors.size() == 1, "Erro ao usar variável fora do escopo");
        if (v3 != null) {
            for (String erro : v3.errors) System.out.println("  -> " + erro);
        }

        IdentificationVisitor v4 = executarAnalise("fun calcular(int x) { \n int x = 5; \n }");
        check(v4 != null && v4.errors.size() == 1, "Impediu redeclaração entre parâmetro e variável");
        if (v4 != null) {
            for (String erro : v4.errors) System.out.println("  -> " + erro);
        }
    }

    private static void verificarArquivo(String codigo, String caminho, String nomeTeste) {
        IdentificationVisitor visitor = executarAnalise(codigo);
        if (visitor == null) {
            System.out.println("[FALHOU] " + nomeTeste + " (Código não passou no Parser)");
            return;
        }

        String errosGerados = String.join("\n", visitor.errors).trim();
        try {
            String errosEsperados = Files.readString(Paths.get(caminho)).trim().replace("\r", "");
            check(errosGerados.equals(errosEsperados), nomeTeste);

            if (!errosGerados.equals(errosEsperados)) {
                System.out.println(" -> Diferença encontrada no arquivo de texto.");
            }
        } catch (IOException e) {
            System.out.println("Arquivo não encontrado: " + caminho);
        }
    }
}