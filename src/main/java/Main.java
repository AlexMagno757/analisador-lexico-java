import ast.Program;
import lexer.Scanner;
import parser.Parser;
import visitor.ASTPrinter;
import visitor.SourcePrinter;

public class Main {
    public static void main(String[] args) {
        String codigo ="""
                        #! 
                           Projeto Compiladores - UFU
                        !#
                        
                        # Declaracao de variaveis globais
                        int idade = 21;
                        double salario = 2500.50;
                        string nome = "Alex Magno";
                        bool ativo = true;
                        
                        fun main() {
                            # Teste de operadores relacionais e logicos
                            if (idade >= 18 & salario != 0.0) {
                                print("Usuario valido");
                            } else {
                                print("Invalido");
                            }
                            
                            # Teste de loops e operacoes matematicas
                            while (ativo == true | idade <= 30) {
                                idade = idade + 1;
                                salario = salario * 1.5; 
                            }
                            
                            return 0;
                        }
                        """;


        Scanner scanner = new Scanner(codigo);
        Parser parser = new Parser(scanner);
        Program ast = parser.parse();

        if (ast != null) {
            System.out.println("\n=== Parte 2: IMPRESSÃO DA AST ===");
            ASTPrinter astPrinter = new ASTPrinter();
            String estruturaArvore = ast.accept(astPrinter);
            System.out.println(estruturaArvore);

            System.out.println("\n=== Parte 3: RECONSTRUÇÃO DO CÓDIGO ===");
            SourcePrinter sourcePrinter = new SourcePrinter();
            String codigoReimpresso = ast.accept(sourcePrinter);
            System.out.println(codigoReimpresso);
        } else {
            System.out.println("\nA execução dos visitantes foi cancelada devido a erros na análise sintática.");
        }
    }
}