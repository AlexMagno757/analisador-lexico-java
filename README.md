# Compilador

Desenvolvimento de um compilador utilizando a linguagem Java

## Desenvolvimento

O projeto já contempla as seguintes fases do compilador: 

* **Analisador Léxico (Scanner):** Leitura de caracteres e geração de tokens.
* **Analisador Sintático (Parser LL(1)):** Análise descendente recursiva com construção de Árvore Sintática Abstrata (AST).
* **Recuperação de Erros (Modo Pânico):** Sincronização para reporte de múltiplos erros sintáticos em uma única passada sem travar a execução.
* **Análise Semântica (Passada de Identificação):** Verificação de regras de escopo, sombreamento de variáveis e detecção de redeclarações utilizando uma Tabela de Símbolos em formato de pilha de mapas.
* **Visitors:**
  * `ASTPrinter`: Impressão estruturada e hierárquica dos nós da árvore sintática.
  * `SourcePrinter`: Reconstrução exata do código-fonte a partir da AST (*Round-Trip*).
  * `IdentificationVisitor`:Percorre a AST para validar a existência das variáveis e "decorar" os nós com referências diretas para suas declarações na memória.

## Estrutura Principal de Arquivos
* `src/main/java/lexer/` - Componentes da análise léxica (`Scanner`, `Token`, `TokenType`).
* `src/main/java/parser/` - Componentes da análise sintática e tratamento de erros (`Parser`, `ParseError`).
* `src/main/java/ast/` - Estruturas dos nós da Árvore Sintática Abstrata.
* `src/main/java/semantic/` - Componentes da Tabela de Símbolos (`Symbol`, `StackSymbolTable`).
* `src/main/java/visitor/` - Implementação das classes de travessia (`ASTPrinter`, `SourcePrinter`).
* `src/test/esperado/` - Arquivos `.txt` de referência com as árvores esperadas para validação automática.
* `docs/` - Documentação técnica do projeto:
  * `ast-design.md`: Estrutura da Árvore Sintática Abstrata.
  * `especificacao_lexica.md`: Definição dos tokens e regras léxicas.
  * `gramatica-first-follow.md`: Regras da gramática e conjuntos First/Follow.
  * `escopo-design.md`: Documentação das 5 decisões de projeto sobre o escopo e comportamento da tabela de símbolos.
 
## Como Executar

No momento atual o projeto possui algumas classes para execução e testes de validação.

1. **`Main.java`**: Roda o fluxo completo de compilação em um código de exemplo (Lexer → Parser → ASTPrinter → SourcePrinter). Caminho: `src/main/java/Main.java`
2. **`TestesAST.java`**: Executa a suíte de testes estruturais (precedência, associatividade, parênteses redundantes e validação de arquivos). Caminho: `src/test/java/TestesAST.java`
3. **`MainParseTestes.java`**: Suíte de regressão sintática para validação de bloqueio de árvores inválidas e testes do modo pânico. Caminho: `src/test/java/MainParseTestes.java`
4. **`MainTestes.java`**:  Suíte de testes exclusiva do Analisador Léxico (Scanner), cobrindo casos válidos, recuperação de erros e códigos realistas. Caminho: `src/test/java/MainTestes.java`
5. **`TestesIdentificacao.java`**: Suíte de testes da Análise Semântica validando o funcionamento isolado da tabela, checagem de regras de escopo, funções recursivas e acúmulo de múltiplos erros. Caminho: `src/test/java/TestesIdentificacao.java`
