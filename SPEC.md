# Especificação Técnica do Sistema — Precifico v1
**Metodologia:** Spec Driven Development (SDD)  
**Projeto:** Gestor de Vendas e Precificação de Produtos  
**Instituição:** UMC (Universidade de Mogi das Cruzes)  
**Disciplina:** Programação / Padrões de Projeto  

---

## 1. Visão Geral e Objetivos do Sistema

O **Precifico v1** é uma aplicação web desenvolvida em Java (JSP/Servlets) e MySQL, focada em auxiliar vendedores no gerenciamento e na precificação assertiva de produtos para venda. 

O sistema implementa regras de negócio para cálculo automático de preço sugerido e lucro estimado a partir do custo do produto e do percentual de margem desejada, oferecendo diagnóstico comercial em tempo real. Cada vendedor tem acesso exclusivo aos seus próprios produtos.

O desenvolvimento adota estritamente:
- **Código simples, limpo e direto** (nível acadêmico), evitando complexidade desnecessária.
- **Linguagem em Português Brasileiro (pt-BR)** para todo o domínio, métodos e variáveis, mantendo em inglês apenas termos canônicos de Design Patterns (`ICommand`, `Action`, `Builder`, `Factory`).
- **Padrão MVC com JSP** como camada de apresentação visual, baseado no design de `Prototipo.html`.

---

## 2. Requisitos Acadêmicos Obrigatórios

Em conformidade com o arquivo `especificações_faculdade.md`:

| Item | Exigência | Atendimento no Precifico v1 |
|---|---|---|
| **4.1 Entidades** | Pelo menos 10 atributos no conjunto | **18 atributos** divididos entre `Produto`, `Precificacao` e `Vendedor` |
| **4.2 Design Patterns** | Implementar DAO, MVC, COMMAND, FACTORY METHOD e BUILDER | Todos os 5 padrões implementados conforme exemplos do professor |
| **4.3 Operações** | Inserir, Deletar, Atualizar, Consultar por ID e Consultar todos | Implementadas no CRUD de Produtos e Vendedores |
| **4.3 Automação** | Funcionalidade que automatize processo de negócio | **Motor de Precificação Inteligente e Diagnóstico Comercial Automático** |
| **4.4 Relacionamentos** | Conter relacionamento 1:1 e relacionamento 1:N | **1:1** (`Produto` $\longleftrightarrow$ `Precificacao`) e **1:N** (`Vendedor` $\longleftrightarrow$ `Produto`) |
| **4.7 Usabilidade** | Boa usabilidade e interface amigável | Baseada na UI completa e responsiva do `Prototipo.html` |
| **4.8 Qualidade** | SOLID, Calisthenics, Alta Coesão e Baixo Acoplamento | Classes com responsabilidade única, métodos curtos e nomes expressivos |

---

## 3. Modelo de Entidades e Atributos

### 3.1. `Produto` (7 atributos)
Representa o produto colocado à venda pelo vendedor:
- `id` (int): Identificador único do produto (Primary Key).
- `codigoProduto` (String): Código de identificação / SKU do produto.
- `nome` (String): Nome comercial do produto.
- `descricao` (String): Descrição detalhada do produto.
- `imagem` (String): URL pública da foto do produto (JPEG, PNG ou WebP).
- `situacao` (String): Status atual ('DISPONIVEL' ou 'VENDIDO').
- `vendedor` (Vendedor): Vendedor proprietário do produto (chave estrangeira `id_vendedor`).
- `precificacao` (Precificacao): Associação 1:1 com os dados de precificação.

### 3.2. `Precificacao` (4 atributos) — Relacionamento 1:1
Representa a precificação e margem financeira vinculada ao produto:
- `id` (int): Identificador único da precificação (Primary Key).
- `valorVenda` (double): Preço de venda praticado.
- `custoProduto` (double): Valor de custo do produto.
- `lucro` (double): Lucro estimado em reais ($valorVenda - custoProduto$).

*Métodos de negócio embutidos:*
- `calcularLucro(double valorVenda, double custo)`: Retorna $valorVenda - custo$.
- `calcularMargemEfetiva(double lucro, double custo)`: Retorna $(lucro / custo) \times 100$.
- `calcularPrecoSugerido(double custo, double percentualLucro)`: Retorna $custo \times (1 + percentualLucro / 100)$.
- `classificarPreco(double precoVenda, double precoSugerido, double custo)`: Retorna o diagnóstico textual de viabilidade.

### 3.3. `Vendedor` (7 atributos) — Relacionamento 1:N
Representa o usuário responsável pelos produtos:
- `id` (int): Identificador único do vendedor (Primary Key).
- `nome` (String): Nome completo do vendedor.
- `email` (String): E-mail único de autenticação.
- `senha` (String): Senha de acesso.
- `idade` (Integer): Idade do vendedor.
- `endereco` (String): Endereço cadastrado.
- `cpf` (String): CPF do vendedor.

---

## 4. Regras de Negócio e Cálculos

### 4.1. Cálculo do Preço Sugerido (Regra 6.1)
$$\text{Preço Sugerido} = \text{Custo} + \left(\text{Custo} \times \frac{\text{Percentual de Lucro Desejado}}{100}\right)$$

### 4.2. Cálculo do Lucro e Margem Efetiva (Regra 6.2)
$$\text{Lucro Estimado} = \text{Preço de Venda} - \text{Valor de Custo}$$
$$\text{Percentual Efetivo} = \left(\frac{\text{Lucro Estimado}}{\text{Valor de Custo}}\right) \times 100$$

### 4.3. Avaliação do Preço Informado (Regra 6.3)
Comparação entre o preço de venda informado e o preço sugerido:
- Se $\text{Preço de Venda} < \text{Custo}$: *"Atenção: este preço resultará em prejuízo."* (Destaque Vermelho/Danger)
- Mais de 20% abaixo da sugestão: *"Atenção: o preço está muito abaixo da sugestão."* (Destaque Amarelo/Warn)
- Entre 5% e 20% abaixo: *"O preço está abaixo da sugestão."*
- Diferença de até 5%: *"O preço está próximo do valor sugerido."* (Destaque Verde/Sucesso)
- Entre 5% e 20% acima: *"O preço está acima da sugestão."*
- Mais de 20% acima: *"Atenção: o preço está muito acima da sugestão."* (Destaque Amarelo/Warn)

---

## 5. Arquitetura dos 5 Design Patterns

### 5.1. DAO (Data Access Object)
- `database.FabricaConexao`: Gerencia a conexão JDBC com MySQL.
- `dao.VendedorDAO`: Operações de cadastro, login/autenticação e consulta de vendedores.
- `dao.PrecificacaoDAO`: Inserção, atualização, consulta e exclusão da precificação 1:1.
- `dao.ProdutoDAO`: CRUD completo de produtos associando a transação de precificação e filtrando por vendedor.

### 5.2. MVC (Model-View-Controller)
- **Model**: `model.Vendedor`, `model.Produto`, `model.Precificacao`.
- **View**: Telas JSP com visual estilizado baseado em `Prototipo.html`.
- **Controller**: Servlets que recebem requisições e delegam para o comando correspondente.

### 5.3. COMMAND
- Interface `command.ICommand`:
  ```java
  public interface ICommand {
      public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception;
  }
  ```
- Ações em `command.action`:
  - `CadastrarProdutoAction`
  - `AtualizarProdutoAction`
  - `DeletarProdutoAction`
  - `ConsultarTodosProdutoAction`
  - `ConsultarByIdProdutoAction`
  - `EditaProdutoAction`
  - `AlterarSituacaoProdutoAction`
  - `CadastrarVendedorAction`
  - `LoginVendedorAction`
  - `LogoutVendedorAction`

### 5.4. DISPATCHER / FACTORY POR REFLEXÃO & FACTORY METHOD
- **Reflexão Dinâmica nos Controladores**: Conforme padrão de `CRUD_WEB_MVC_DAO`, os controladores `ControleProduto` e `ControleVendedor` utilizam reflexão Java (`Class.forName("command.action." + paramAction + "...Action").newInstance()`) para instanciar dinamicamente cada comando sem necessidade de classes intermediárias de switch/case.
- **Factory Method**: Implementado na classe `database.FabricaConexao.getConexao()` para criação desacoplada de conexões JDBC.

### 5.5. BUILDER
- `builder.ProdutoBuilder`: Padrão de criação com interface fluente (`.comCodigo()`, `.comNome()`, `.comDescricao()`, `.comImagem()`, `.comSituacao()`, `.comVendedor()`, `.comPrecificacao()`, `.constroi()`), tornando a montagem de objetos de negócio legível e sem construtores sobrecarregados.

---

## 6. Esquema do Banco de Dados (MySQL)

```sql
-- Tabela de Vendedores
CREATE TABLE IF NOT EXISTS vendedores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    idade INT,
    endereco VARCHAR(200),
    cpf VARCHAR(14)
);

-- Tabela de Produtos (1:N com Vendedor)
CREATE TABLE IF NOT EXISTS produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_produto VARCHAR(50) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    imagem VARCHAR(500) NOT NULL,
    situacao VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',
    id_vendedor INT NOT NULL,
    CONSTRAINT fk_produto_vendedor FOREIGN KEY (id_vendedor) 
        REFERENCES vendedores(id) ON DELETE CASCADE
);

-- Tabela de Precificações (1:1 com Produto)
CREATE TABLE IF NOT EXISTS precificacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_produto INT NOT NULL UNIQUE,
    valor_venda DOUBLE NOT NULL,
    custo_produto DOUBLE NOT NULL,
    lucro DOUBLE NOT NULL,
    CONSTRAINT fk_precificacao_produto FOREIGN KEY (id_produto) 
        REFERENCES produtos(id) ON DELETE CASCADE
);
```

---

## 7. Mapeamento das Telas (View JSP)

- `web/login.jsp`: Formulário de login com e-mail e senha.
- `web/cadastro_vendedor.jsp`: Formulário de criação de conta do vendedor.
- `web/produtos.jsp`: Painel principal com tabela de produtos, status e botões de ação rápida.
- `web/formulario_produto.jsp`: Tela de cadastro e edição de produto com precificador interativo.
- `web/detalhe_produto.jsp`: Ficha técnica e financeira completa do produto.
- `web/resultado.jsp`: Mensagens de sucesso com link de retorno.
- `web/erro.jsp`: Mensagens de erro amigáveis sem exposição de dados técnicos.
