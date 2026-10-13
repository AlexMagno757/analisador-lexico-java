# Gramática Final da Linguagem

```text
<Programa> ::= <ListaDecl> EOF
<ListaDecl> ::= <Declaracao> <ListaDecl> | ε

<Declaracao> ::= <DeclaracaoFuncao> | <DeclaracaoVariavel>

<DeclaracaoFuncao> ::= "fun" IDENTIFICADOR "(" <ListaParams> ")" <Bloco>

<ListaParams> ::= <Parametro> <ListaParamsContinua> | ε
<ListaParamsContinua> ::= "," <Parametro> <ListaParamsContinua> | ε
<Parametro> ::= <Tipo> IDENTIFICADOR

<Tipo> ::= "int" | "double" | "char" | "bool" | "string"

<Bloco> ::= "{" <ListaCmd> "}"
<ListaCmd> ::= <Comando> <ListaCmd> | ε

<Comando> ::= <ComandoIf> 
            | <ComandoWhile> 
            | <ComandoFor>
            | <ComandoReturn>
            | <ComandoPrint> 
            | <ComandoAtribuicao> 
            | <DeclaracaoVariavel>

<ComandoIf> ::= "if" "(" <Expressao> ")" <Bloco> <ComandoIfContinua>
<ComandoIfContinua> ::= "else" <Bloco> | ε

<ComandoWhile> ::= "while" "(" <Expressao> ")" <Bloco>

<ComandoFor> ::= "for" "(" <AtribuicaoFor> ";" <Expressao> ";" <AtribuicaoFor> ")" <Bloco>
<AtribuicaoFor> ::= IDENTIFICADOR "=" <Expressao>

<ComandoReturn> ::= "return" <Expressao> ";"    
<ComandoPrint> ::= "print" "(" <Expressao> ")" ";"
<ComandoAtribuicao> ::= IDENTIFICADOR "=" <Expressao> ";"
<DeclaracaoVariavel> ::= <Tipo> IDENTIFICADOR "=" <Expressao> ";"

<Expressao> ::= <ExpOr>

<ExpOr> ::= <ExpAnd> <ExpOrContinua>
<ExpOrContinua> ::= "|" <ExpAnd> <ExpOrContinua> | ε

<ExpAnd> ::= <ExpIgualdade> <ExpAndContinua>
<ExpAndContinua> ::= "&" <ExpIgualdade> <ExpAndContinua> | ε

<ExpIgualdade> ::= <ExpRelacional> <ExpIgualdadeContinua>
<ExpIgualdadeContinua> ::= ( "==" | "!=" ) <ExpRelacional> <ExpIgualdadeContinua> | ε

<ExpRelacional> ::= <ExpAritmetica> <ExpRelacionalContinua>
<ExpRelacionalContinua> ::= ( ">" | ">=" | "<" | "<=" ) <ExpAritmetica> <ExpRelacionalContinua> | ε

<ExpAritmetica> ::= <Termo> <ExpAritmeticaContinua>
<ExpAritmeticaContinua> ::= ( "+" | "-" ) <Termo> <ExpAritmeticaContinua> | ε

<Termo> ::= <Fator> <TermoContinua>
<TermoContinua> ::= ( "*" | "/" ) <Fator> <TermoContinua> | ε

<Fator> ::= NUMERO 
          | IDENTIFICADOR 
          | STRING 
          | "true" 
          | "false" 
          | "!" <Fator>
          | "(" <Expressao> ")"

``` 
# Tabela de First e Follow 

*   **[Tipos]** = `"int", "double", "char", "bool", "string"`
*   **[InícioCmd]** = `"if", "while", "for", "return", "print", IDENTIFICADOR, [Tipos]`
*   **[InícioFator]** = `NUMERO, IDENTIFICADOR, STRING, "true", "false", "!", "("`

| Não-Terminal | FIRST | FOLLOW |
| :--- | :--- | :--- |
| `<Programa>` | `"fun", [Tipos], EOF` | *Vazio* |
| `<ListaDecl>` | `"fun", [Tipos], ε` | `EOF` |
| `<Declaracao>` | `"fun", [Tipos]` | `"fun", [Tipos], EOF` |
| `<DeclaracaoFuncao>` | `"fun"` | `"fun", [Tipos], EOF` |
| `<ListaParams>` | `[Tipos], ε` | `)` |
| `<ListaParamsContinua>` | `,`, `ε` | `)` |
| `<Parametro>` | `[Tipos]` | `,`, `)` |
| `<Tipo>` | `[Tipos]` | `IDENTIFICADOR` |
| `<Bloco>` | `{` | `"fun", [Tipos], EOF, [InícioCmd], "}", "else"` |
| `<ListaCmd>` | `[InícioCmd], ε` | `}` |
| `<Comando>` | `[InícioCmd]` | `[InícioCmd], "}"` |
| `<ComandoIf>` | `"if"` | `[InícioCmd], "}"` |
| `<ComandoIfContinua>` | `"else", ε` | `[InícioCmd], "}"` |
| `<ComandoWhile>` | `"while"` | `[InícioCmd], "}"` |
| `<ComandoFor>` | `"for"` | `[InícioCmd], "}"` |
| `<AtribuicaoFor>` | `IDENTIFICADOR` | `;`, `)` |
| `<ComandoReturn>` | `"return"` | `[InícioCmd], "}"` |
| `<ComandoPrint>` | `"print"` | `[InícioCmd], "}"` |
| `<ComandoAtribuicao>`| `IDENTIFICADOR` | `[InícioCmd], "}"` |
| `<DeclaracaoVariavel>`| `[Tipos]` | `[InícioCmd], "}"` |
| `<Expressao>` | `[InícioFator]` | `)`, `;` |
| `<ExpOr>` | `[InícioFator]` | `)`, `;` |
| `<ExpOrContinua>` | <code>&#124;</code>, `ε` | `)`, `;` |
| `<ExpAnd>` | `[InícioFator]` | <code>&#124;</code>, `)`, `;` |
| `<ExpAndContinua>` | `&`, `ε` | <code>&#124;</code>, `)`, `;` |
| `<ExpIgualdade>` | `[InícioFator]` | `&`, <code>&#124;</code>, `)`, `;` |
| `<ExpIgualdadeContinua>`| `==`, `!=`, `ε` | `&`, <code>&#124;</code>, `)`, `;` |
| `<ExpRelacional>` | `[InícioFator]` | `==`, `!=`, `&`, <code>&#124;</code>, `)`, `;` |
| `<ExpRelacionalContinua>`| `>`, `>=`, `<`, `<=`, `ε` | `==`, `!=`, `&`, <code>&#124;</code>, `)`, `;` |
| `<ExpAritmetica>` | `[InícioFator]` | `>`, `>=`, `<`, `<=`, `==`, `!=`, `&`, <code>&#124;</code>, `)`, `;` |
| `<ExpAritmeticaContinua>`| `+`, `-`, `ε` | `>`, `>=`, `<`, `<=`, `==`, `!=`, `&`, <code>&#124;</code>, `)`, `;` |
| `<Termo>` | `[InícioFator]` | `+`, `-`, `>`, `>=`, `<`, `<=`, `==`, `!=`, `&`, <code>&#124;</code>, `)`, `;` |
| `<TermoContinua>` | `*`, `/`, `ε` | `+`, `-`, `>`, `>=`, `<`, `<=`, `==`, `!=`, `&`, <code>&#124;</code>, `)`, `;` |
| `<Fator>` | `[InícioFator]` | `*`, `/`, `+`, `-`, `>`, `>=`, `<`, `<=`, `==`, `!=`, `&`, <code>&#124;</code>, `)`, `;` |

# Condição LL(1)

### A. Regras com uma única alternativa 
Como não existem ramificações de escolha, não há colisões de FIRSTs nem múltiplas alternativas anuláveis.
*   `<Programa>`, `<DeclaracaoFuncao>`, `<Parametro>`, `<Bloco>`, `<ComandoIf>`, `<ComandoWhile>`, `<ComandoFor>`, `<AtribuicaoFor>`, `<ComandoReturn>`, `<ComandoPrint>`, `<ComandoAtribuicao>`, `<DeclaracaoVariavel>`, `<Expressao>`, `<ExpOr>`, `<ExpAnd>`, `<ExpIgualdade>`, `<ExpRelacional>`, `<ExpAritmetica>`, `<Termo>`.

### B. Múltiplas alternativas SEM derivação vazia (`ε`)
Verificamos a **Cláusula (a)**: As alternativas de cada produção têm de ter FIRSTs totalmente disjuntos.
*   `<Declaracao>`: FIRSTs são `"fun"` e `[Tipos]`. Nenhuma sobreposição.
*   `<Tipo>`: FIRSTs são literais isolados (`"int"`, `"double"`, `"char"`, `"bool"`, `"string"`). Disjuntos.
*   `<Comando>`: FIRSTs são `"if"`, `"while"`, `"for"`, `"return"`, `"print"`, `IDENTIFICADOR` e `[Tipos]`. Estritamente disjuntos.
*   `<Fator>`: FIRSTs são `NUMERO`, `IDENTIFICADOR`, `STRING`, `"true"`, `"false"`, `"!"` e `"("`. Estritamente disjuntos.

### C. Múltiplas alternativas COM derivação vazia (`ε`)
Verificamos a **Cláusula (b)**: Apenas uma alternativa deriva em `ε` por regra (Satisfeito em todos os casos pelas regras `<...Continua>`).

Verificamos a **Cláusula (c)**: A interseção do FIRST da alternativa não-nula com o FOLLOW do não-terminal tem de ser vazia

*   **`<ListaDecl>`**
    *   FIRST(não-vazia): `{"fun", [Tipos]}`
    *   FOLLOW: `{EOF}`
    *   *Interseção:* Vazia.
*   **`<ListaParams>`**
    *   FIRST(não-vazia): `{[Tipos]}`
    *   FOLLOW: `{)}`
    *   *Interseção:* Vazia.
*   **`<ListaParamsContinua>`**
    *   FIRST(não-vazia): `{,}`
    *   FOLLOW: `{)}`
    *   *Interseção:* Vazia.
*   **`<ListaCmd>`**
    *   FIRST(não-vazia): `{[InícioCmd]}`
    *   FOLLOW: `{}}`
    *   *Interseção:* Vazia.
*   **`<ComandoIfContinua>`**
    *   FIRST(não-vazia): `{"else"}`
    *   FOLLOW: `{[InícioCmd], "}"}`
    *   *Interseção:* Vazia.
*   **`<ExpOrContinua>`**
    *   FIRST(não-vazia): `{|}`
    *   FOLLOW: `{), ;}`
    *   *Interseção:* Vazia.
*   **`<ExpAndContinua>`**
    *   FIRST(não-vazia): `{&}`
    *   FOLLOW: `{|, ), ;}`
    *   *Interseção:* Vazia.
*   **`<ExpIgualdadeContinua>`**
    *   FIRST(não-vazia): `{==, !=}`
    *   FOLLOW: `{&, |, ), ;}`
    *   *Interseção:* Vazia.
*   **`<ExpRelacionalContinua>`**
    *   FIRST(não-vazia): `{>, >=, <, <=}`
    *   FOLLOW: `{==, !=, &, |, ), ;}`
    *   *Interseção:* Vazia.
*   **`<ExpAritmeticaContinua>`**
    *   FIRST(não-vazia): `{+, -}`
    *   FOLLOW: `{>, >=, <, <=, ==, !=, &, |, ), ;}`
    *   *Interseção:* Vazia.
*   **`<TermoContinua>`**
    *   FIRST(não-vazia): `{*, /}`
    *   FOLLOW: `{+, -, >, >=, <, <=, ==, !=, &, |, ), ;}`
    *   *Interseção:* Vazia.