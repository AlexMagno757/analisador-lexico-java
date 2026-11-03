# Compilador

Desenvolvimento de um compilador utilizando a linguagem Java

## Desenvolvimento

O projeto já contempla as seguintes fases do compilador: 

* **Analisador Léxico (Scanner):** Leitura de caracteres e geração de tokens.
* **Analisador Sintático (Parser LL(1)):** Análise descendente recursiva com construção de Árvore Sintática Abstrata (AST).
* **Recuperação de Erros (Modo Pânico):** Sincronização para reporte de múltiplos erros sintáticos em uma única passada sem travar a execução.
* **Visitors:**
  * `ASTPrinter`: Impressão estruturada e hierárquica dos nós da árvore sintática.
  * `SourcePrinter`: Reconstrução exata do código-fonte a partir da AST (*Round-Trip*).
 
## Como Executar

No momento atual o projeto possui algumas classes para execução e testes de validação.

1. **`Main.java`**: Roda o fluxo completo de compilação em um código de exemplo (Lexer → Parser → ASTPrinter → SourcePrinter).
2. **`TestesAST.java`**: Executa a suíte de testes estruturais (precedência, associatividade, parênteses redundantes e validação de arquivos).
3. **`MainParseTestes.java`**: Suíte de regressão sintática para validação de bloqueio de árvores inválidas e testes do modo pânico.
4. **`MainTestes.java`**:  Suíte de testes exclusiva do Analisador Léxico (Scanner), cobrindo casos válidos, recuperação de erros e códigos realistas.
