# Especificação Léxica da Linguagem

### Aluno: Alexandre Magno Silva Júnior <br> Matrícula: 12321BCC005

## 1. Categorias de Token
Abaixo encontra-se a tabela de especificação dos tokens:

| Token                  | Notação (regex/EBNF)                                  | Exemplos válidos                | Decisões de Implementação                                                                                                                                                           |
|:-----------------------|:------------------------------------------------------|:--------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Identificador**      | `letra (letra \| digito \| "_")*`                     | `idade`, `x1`, `valor_total`    | É case-sensitive. O caractere `_` é permitido. Não existe limite máximo de tamanho explícito.                                                                                       |
| **Palavra reservada**  | Padrão do identificador + Tabela de Busca (`HashMap`) | `if`, `while`, `int`            | Lista fechada gerida em memória.                                                                                                                                                    |
| **String**             | `" (qualquer caractere ≠ ") * "`                      | `"Alex"`, `"Erro 404"`          | Não processa escapes complexos. Se o ficheiro terminar (EOF) ou ocorrer quebra de linha sem fecho das aspas, um erro é reportado e o scanner recupera pulando para o próximo token. |
| **Operador**           | `1 ou 2 caracteres`                                   | `=`, `==`, `<`, `<=`, `+`, `&&` | Simples: `+`, `-`, `*`, `/`, `=`, `<`, `>`, `!`.<br>Compostos: `==`, `!=`, `<=`, `>=`, `&&`, `\|\|`.                                                                                |
| **Literal numérico**   | `digito+ ("." digito+)?`                              | `21`, `2500.50`, `0`            | Suporta números inteiros e decimais reais. Não suporta notação científica nem formato hexadecimal.                                                                                  |

**1. Alfabeto de entrada**<br>
O alfabeto baseia-se nos caracteres ASCII padrão, cobrindo letras (A-Z, a-z), dígitos (0-9), operadores matemáticos/lógicos e pontuação.

**2. Regra de Case-sensitive**<br>
A linguagem é estritamente *case-sensitive*. As palavras reservadas devem ser escritas inteiramente em minúsculas (ex: `int`, `while`). Os identificadores seguem a mesma regra de distinção, significando que `Idade` e `idade` são reconhecidos como dois identificadores completamente distintos.

**3. Espaços em branco e Comentários**<br>
*   **Espaços em branco:** O scanner ignora espaços `' '`, tabulações `\t`, *carriage returns* `\r` e novas linhas `\n`.
*   **Comentários de Linha:** Iniciam-se com o caractere `#` e o scanner ignora todo o texto até encontrar uma quebra de linha `\n`.
*   **Comentários de Bloco:** São delimitados pela abertura `#!` e fecho `!#`. O scanner regista a posição inicial e caso alcance o final do ficheiro sem encontrar o fecho, reporta um erro léxico com linha e coluna.
*   **Aninhamento:** Os comentários de bloco **não** podem ser aninhados. O primeiro `!#` encontrado encerrará imediatamente o bloco atual.

**4. Regra de Desambiguação (Maximal Munch)**<br>
A arquitetura do scanner implementa a regra do *Maximal Munch*. Ao encontrar um caractere que pode iniciar um operador composto (como `=`, `!`, `<`, `>`), a máquina de estados inspeciona o próximo caractere no fluxo (`peek()`). Se este formar um operador válido (como `=`), o scanner consome o segundo caractere e emite o token composto (ex: `==` ou `!=`). Caso contrário, emite o operador simples sem avançar o cursor de leitura extra.

**5. Palavras Reservadas da Linguagem**<br>
A lista fechada de palavras reservadas, reconhecidas através de consulta direta no `HashMap` da implementação, contém exatamente as seguintes quatorzes chaves:
`if`, `else`, `while`, `for`, `return`, `print`, `true`, `false`, `int`, `double`, `char`, `bool`, `string`, `fun`.

## 2. Tratamento e Recuperação de Erros
Caracteres inválidos (não pertencentes ao alfabeto), strings não fechadas ou números mal formatados não travam o compilador com exceções fatais. O analisador reporta a natureza do erro juntamente com a linha e coluna exatas no terminal, descarta o caractere ou lexema problemático, e prossegue automaticamente a execução tentando ler o próximo token válido.