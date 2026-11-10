# Design da Tabela de Símbolos e Escopos

## 1. Desenho do Ambiente e Escopos

O programa abaixo foi desenhado para exercitar dois níveis de blocos aninhados, sombreamento de variáveis, uso após fechamento de bloco e funções com parâmetros.

**Código de Teste:**

```
 1 int x = 10;
 2 fun calcular(int y) {
 3     int x = y + 5; 
 4     {
 5         int z = x * 2; 
 6         print(z);
 7     }
 8     print(x); 
 9 }
10 print(x);
```
**Rastreio do Ambiente**

| Depois da linha | Ambiente (interno :: externo) |
| :--- | :--- |
| 1 | `{ x@1 }` |
| 2 (global) | `{ x@1, calcular@2 }` |
| 2 (bloco) | `{ y@2 } :: { x@1, calcular@2 }` |
| 3 | `{ x@3, y@2 } :: { x@1, calcular@2 }` |
| 4 | `{ } :: { x@3, y@2 } :: { x@1, calcular@2 }` |
| 5 | `{ z@5 } :: { x@3, y@2 } :: { x@1, calcular@2 }` |
| 7 | `{ x@3, y@2 } :: { x@1, calcular@2 }` |
| 9 | `{ x@1, calcular@2 }` |

**Resolução de Nomes**

| Uso | Linha | Resolve para |
| :--- | :--- | :--- |
| `y` | 3 | `y@2` (parâmetro) |
| `x` | 5 | `x@3` (x interno, sombreado) |
| `z` | 6 | `z@5` (z do bloco mais interno) |
| `x` | 8 | `x@3` (x interno) |
| `x` | 10 | `x@1` (x global) |

## 2. Estrutura do Símbolo (Symbol)

A classe `Symbol` foi desenhada para carregar apenas a informação necessária para a resolução de nomes e fases futuras:

*   **`String name`**: O identificador (ex: "x"), usado como chave de busca na Tabela de Símbolos.
*   **`Kind kind`**: Um enumerador (`VAR`, `CONST`, `PARAM`, `FUN`) para distinguir a entidade. Utilizado nas regras semânticas (ex: proibir atribuição a `CONST`).
*   **`Node decl`**: Referência ao nó original da AST onde a declaração ocorreu. Essencial para apontar a linha/coluna exata em mensagens de erro de redeclaração.
*   **`int level`**: Profundidade do escopo atual (0 para global, 1+ para locais). Usado para detectar redeclarações irregulares no mesmo bloco.
*   **`Type type`**: Reservado para a checagem de tipos.
*   **`int offset`**: Reservado para a alocação de memória na fase de geração de código.

## 3. Decisões de Projeto

**3.1. Sombreamento**
*   **Escolha:** Permitido entre blocos diferentes, mas redeclaração no mesmo bloco é erro. O método usado será o `lookupLocal` para impedir conflitos no nível atual.
*   **Alternativa descartada:** Proibir completamente a repetição de nomes em qualquer nível.
*   **Motivo:** Seguir o padrão de linguagens modernas (C/Java), permitindo que desenvolvedores criem variáveis curtas (como `i` em loops) sem se preocupar com colisões globais.
* **Teste:**
    `int a = 1; { int a = 2; }` *(Permitido)*

    `int b = 1; int b = 2;` *(Erro)*

**3.2. Declarar antes de usar**
*   **Escolha:** Sim, a declaração precisa anteceder o uso (passada única).
*   **Alternativa descartada:** Fazer duas passadas na AST (uma para coletar nomes, outra para resolver).
*   **Motivo:** Mantém a simplicidade arquitetural do compilador em uma única passada.
*   **Teste:** `print(x); int x = 10;` *(Erro: x não declarado)*

**3.3. Funções mutuamente recursivas**
*   **Escolha:** Não permitidas nativamente (ou exigem declaração prévia).
*   **Alternativa descartada:** Permitir através de múltiplas passadas.
*   **Motivo:** Como adotamos a declaração obrigatória antes do uso em uma única passada, a função `A` não consegue enxergar a função `B` se `B` for definida fisicamente depois dela no código fonte.
*   **Teste:** `fun A() { B(); } fun B() { A(); }` *(Erro em A: B não declarado)*

**3.4. Parâmetros e corpo da função**
*   **Escolha:** Parâmetros e as variáveis declaradas na raiz do corpo da função compartilham o mesmo escopo.
*   **Alternativa descartada:** Abrir um escopo extra apenas para as variáveis do corpo.
*   **Motivo:** Evita que o programador perca o acesso ao argumento passado criando uma variável local com o mesmo nome.
*   **Teste:** `fun f(int a) { int a = 5; }` *(Erro: a já declarado)*

**3.5. Espaços de nomes (Namespaces)**
*   **Escolha:** Único. Variáveis e funções dividem a mesma tabela.
*   **Alternativa descartada:** Tabelas separadas para funções e variáveis.
*   **Motivo:** Evitar ambiguidades visuais e semânticas no código fonte.
*   **Teste:** `int calc = 5; fun calc() {}` *(Erro: calc já declarado)*

## 4. Implementação da Tabela de Símbolos

**Escolha:** Pilha de mapas (`Deque>`).<br>
**Justificativa:** É a abordagem mais direta e simples de depurar. Como a profundidade de aninhamento da linguagem é pequena, percorrer a pilha do topo à base tem custo quase nulo, e o descarte automático do escopo ao fechar um bloco garante isolamento seguro.