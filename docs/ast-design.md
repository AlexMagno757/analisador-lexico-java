# Projeto da AST

## 1. Hierarquia de Nós

### Raiz do Programa
| Nome do Nó | Atributos (Tipo) | Produção(ões) Geradora(s) |
| :--- | :--- | :--- |
| `Program` | `declarations (List<Decl>)` | `<Programa> ::= <ListaDecl> EOF` |

### Declarações (`Decl`)
*Classe abstrata base: `Decl`*

| Nome do Nó | Atributos (Tipo) | Produção(ões) Geradora(s) |
| :--- | :--- | :--- |
| `FunDecl` | `id (String)`, `params (List<Param>)`, `body (BlockCmd)` | `<DeclaracaoFuncao> ::= "fun" IDENTIFICADOR "(" <ListaParams> ")" <Bloco>` |
| `VarDecl` | `type (String)`, `id (String)`, `expr (Expr)` | `<DeclaracaoVariavel> ::= <Tipo> IDENTIFICADOR "=" <Expressao> ";"` |
| `Param` | `type (String)`, `id (String)` | `<Parametro> ::= <Tipo> IDENTIFICADOR` |

### Comandos (`Cmd`)
*Classe abstrata base: `Cmd`*

| Nome do Nó  | Atributos (Tipo)                                                              | Produção(ões) Geradora(s) |
|:------------|:------------------------------------------------------------------------------| :--- |
| `BlockCmd`  | `cmds (List<Cmd>)`                                                            | `<Bloco> ::= "{" <ListaCmd> "}"` |
| `IfCmd`     | `condition (Expr)`, `thenBlock (BlockCmd)`, `elseBlock (BlockCmd)`            | `<ComandoIf>`, `<ComandoIfContinua>` |
| `WhileCmd`  | `condition (Expr)`, `body (BlockCmd)`                                         | `<ComandoWhile> ::= "while" "(" <Expressao> ")" <Bloco>` |
| `ForCmd`    | `init (AtribCmd)`, `condition (Expr)`, `update (AtribCmd)`, `body (BlockCmd)` | `<ComandoFor> ::= "for" "(" <AtribuicaoFor> ";" <Expressao> ";" <AtribuicaoFor> ")" <Bloco>` |
| `ReturnCmd` | `expr (Expr)`                                                                 | `<ComandoReturn> ::= "return" <Expressao> ";"` |
| `PrintCmd`  | `expr (Expr)`                                                                 | `<ComandoPrint> ::= "print" "(" <Expressao> ")" ";"` |
| `AtribCmd`  | `id (String)`, `expr (Expr)`                                                  | `<ComandoAtribuicao>`, `<AtribuicaoFor>` |

### Expressões (`Expr`)
*Classe abstrata base: `Expr`*

| Nome do Nó | Atributos (Tipo)                             | Produção(ões) Geradora(s)                                                                  |
| :--- |:---------------------------------------------|:-------------------------------------------------------------------------------------------|
| `NumExpr` | `value (String)`                             | `<Fator> ::= NUMERO`                                                                       |
| `IdExpr` | `id (String)`                                | `<Fator> ::= IDENTIFICADOR`                                                                |
| `StringExpr` | `value (String)`                             | `<Fator> ::= STRING`                                                                       |
| `BoolExpr` | `value (boolean)`                            | `<Fator> ::= "true" \| "false"`                                                            |
| `UnaryExpr` | `op (String)`, `expr (Expr)`                 | `<Fator> ::= "!" <Fator>`                                                                  |
| `BinExpr` | `left (Expr)`, `op (String)`, `right (Expr)` | `<ExpOr>`, `<ExpAnd>`, `<ExpIgualdade>`, `<ExpRelacional>`, `<ExpAritmetica>`, `<Termo>`, `<ExpOrContinua>`, `<ExpAndContinua>`, `<ExpIgualdadeContinua>`, `<ExpRelacionalContinua>`, `<ExpAritmeticaContinua>`, `<TermoContinua>` |

---

## 2. Mapeamento da Gramática para a Árvore

### 2.1. Não-terminais que viram Nós ou Atributos
*   `<DeclaracaoFuncao>`, `<DeclaracaoVariavel>`, `<Parametro>`: Convertem-se nos nós `FunDecl`, `VarDecl` e `Param`.
*   `<ComandoIf>`, `<ComandoWhile>`, `<ComandoFor>`, `<ComandoReturn>`, `<ComandoPrint>`, `<ComandoAtribuicao>`, `<AtribuicaoFor>`: Convertem-se nos seus respetivos nós de comando (`IfCmd`, `WhileCmd`, etc.).
*   `<Tipo>`: Vira um atributo do tipo `String` dentro dos nós `VarDecl` e `Param`.
*   Listas (`<ListaDecl>`, `<ListaParams>`, `<ListaCmd>`): o seu conteúdo entra em atributos do tipo `List<T>` nos nós pais (ex: `BlockCmd` guarda uma `List<Cmd>`).

### 2.2. Não-terminais Omitidos (Não geram nós)

*   **(a) Não-terminais criados para a precedência:** `<ExpOr>`, `<ExpAnd>`, `<ExpIgualdade>`, `<ExpRelacional>`, `<ExpAritmetica>`, `<Termo>`.
    *   *Justificativa:* Servem exclusivamente para guiar o *parser* na resolução matemática/lógica. Na AST, o nó `BinExpr` engloba qualquer operação binária (esquerda, operador, direita), o que torna estes não-terminais redundantes.
*   **(b) Não-terminais criados para eliminar a recursão à esquerda:** `<ExpOrContinua>`, `<ExpAndContinua>`, `<ExpIgualdadeContinua>`, `<ExpRelacionalContinua>`, `<ExpAritmeticaContinua>`, `<TermoContinua>`.
    *   *Justificativa:* Foram criados apenas para evitar que o *parser* entre em *loop* infinito. Na AST, atuam como acumuladores, subindo o operando da esquerda para formar sub-árvores `BinExpr`.
*   **(c) Não-terminais criados pela fatoração:** `<ComandoIfContinua>`, `<ListaParamsContinua>`.
    *   *Justificativa:* Evitam colisões no conjunto FIRST. O `<ComandoIfContinua>` (que avalia o `else`), por exemplo, não é um comando independente, sendo apenas integrado como o atributo `elseBlock` do nó `IfCmd`. O mesmo acontece para a vírgula e próximos parâmetros.

### 2.3. Símbolos Terminais Descartados
*   Parênteses `( )`, chaves `{ }`, ponto e vírgula `;`, vírgulas `,` e operadores de atribuição `=` não viram nós nem atributos. A sua única função é delimitar blocos, precedências visuais e separar itens durante o *parsing*.
*   Palavras-chave estruturais (`if`, `while`, `for`, `fun`, `print`, `return`) não são guardadas textualmente. A sua presença é inferida pelo próprio tipo da classe instanciada (ex: se é um `WhileCmd` originou-se da palavra `while`).

---

## 3. Posição (Rastreamento de Origem)

*   Existe uma superclasse abstrata (`ASTNode`) que contém os atributos `linha (int)` e `coluna (int)`.
*   Todas as classes da AST herdam desta classe.
*   A posição registada num nó corresponde sempre à linha e coluna do **primeiro *token*** que desencadeou a criação desse nó.
    *   *Exemplo 1:* Ao instanciar um `IfCmd`, a linha e coluna guardadas são as do *token* da palavra `if`.
    *   *Exemplo 2:* Ao instanciar um `VarDecl` (`int idade = 21;`), a linha e coluna guardadas serão as do *token* `int`.

---

## 4. Formato de impressão da AST
* A árvore é impressa utilizando 2 espaços de indentação para cada nível de profundidade. Cada nó é impresso numa nova linha com o formato `NomeDoNo(atributos)`.