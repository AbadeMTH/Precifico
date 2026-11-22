# Precifico

Sistema web para auxiliar vendedores no gerenciamento, na precificação e na divulgação promocional de produtos, composto por uma **API REST em Spring Boot** e um **frontend em HTML, CSS e JavaScript puro**, estruturados com princípios **SOLID** e padrões de projeto (**DAO**, **Builder** e **Decorator**).

---

## Funcionalidades

- Cadastro, login e logout de vendedores (autenticação por sessão `HttpSession`).
- Cadastro, listagem, visualização detalhada, edição e exclusão de produtos vinculados ao vendedor logado.
- Simulação em tempo real de preço sugerido, lucro estimado, margem efetiva sobre o custo e diagnóstico comercial.
- Alteração rápida da situação do produto entre `DISPONIVEL` e `VENDIDO`.
- Aplicação e remoção de **promoções** (por valor promocional ou percentual de desconto), com cálculo de lucro promocional e validação de consistência ao atualizar o preço do produto.
- Associação de **brindes** (`BALA`, `PIRULITO`, `ADESIVO`) aos produtos utilizando o padrão **Decorator** para composição da descrição comercial.
- Imagem padrão de fallback caso a foto do produto não carregue.
- Suíte de **testes automatizados** cobrindo o fluxo de cadastro, atualização e deleção de produtos nas camadas de Serviço, Persistência (DAO) e Controller (REST).

---

## Tecnologias

- **Backend:** Java 17, Spring Boot 3.4.3 (Spring Web + Tomcat embutido), Maven, JDBC puro e MySQL Connector/J
- **Testes Automatizados:** Spring Boot Starter Test (JUnit 5, Mockito, AssertJ, MockMvc) e banco em memória H2 (`<scope>test</scope>`)
- **Frontend:** HTML5, CSS3 e JavaScript puro (ES6)
- **Banco de Dados:** MySQL 8.0 (com suporte a Docker Compose)

---

## Arquitetura e Princípios SOLID

### Backend (`backend/`)
Estruturado em camadas desacopladas por interfaces e injeção de dependência via construtor:

- `controller/` — Endpoints REST (`ControleProduto`, `ControleVendedor`) e controle de sessão HTTP.
- `service/` — Regras de negócio e coordenação (`IProdutoService`, `IPrecificacaoService`, `IPromocaoService`, `IVendedorService` e implementações `@Service`).
- `dao/` — Persistência isolada por tabela em JDBC (`IProdutoDAO`, `IPrecificacaoDAO`, `IProdutoBrindeDAO`, `IVendedorDAO` e implementações `@Repository`).
- `database/` — Abstração de conexão JDBC (`IConexaoFactory` e `FabricaConexao`, configurável via propriedades Spring).
- `decorator/` — Composição dinâmica de brindes (`ProdutoDecoravel`, `BrindeDecorator`, `BalaDecorator`, `PirulitoDecorator`, `AdesivoDecorator` e `CompositorBrindes`).
- `model/` e `builder/` — Entidades de domínio (`Vendedor`, `Produto`, `Precificacao`, `SituacaoProduto`, `TipoBrinde`) e `ProdutoBuilder`.
- `dto/` — Objetos de entrada JSON (`ProdutoRequisicao`, `PromocaoRequisicao`, `SimulacaoRequisicao`, `LoginRequisicao`, `CadastroVendedorRequisicao`) e resposta padronizada (`RespostaApi`).
- `config/` — Configuração de CORS (`CorsConfig`).

### Frontend (`frontend/`)
JavaScript modularizado em `frontend/js/` com responsabilidade única e injeção via construtor:

- `httpClient.js` — Cliente HTTP genérico (`fetch`, JSON e `credentials: 'include'`).
- `sessao.js` — Armazenamento local do vendedor (`localStorage`) e mensagens temporárias (`sessionStorage`).
- `guardaAutenticacao.js` — Proteção de rotas autenticadas e redirecionamento.
- `authServico.js` — Comunicação com endpoints de autenticação e cadastro de vendedor.
- `produtoServico.js` — Comunicação com endpoints de produtos e simulação de precificação.
- `promocaoServico.js` — Comunicação com endpoints de aplicação e remoção de promoções.
- `promocaoCalculadora.js` — Cálculos de valor promocional, percentual de desconto e lucro promocional.
- `formatadores.js` — Formatadores PT-BR de moeda, percentuais e textos.
- `brindesUi.js` — Renderização e leitura dos brindes selecionados na interface.
- `ui.js` — Alertas, classificação visual do diagnóstico e utilitários de interface.
- `app.js` — Ponto central de instanciação dos serviços (`window.PrecificoApp`).
- `paginas/` — Controladores dedicados a cada tela (`loginPagina.js`, `cadastroVendedorPagina.js`, `produtosPagina.js`, `formularioProdutoPagina.js`, `detalheProdutoPagina.js`, `promoverProdutoPagina.js`).

### Relacionamentos no Banco (`scripts_banco.sql`)
- **Vendedor `1 : N` Produto** (`produtos.id_vendedor` → `vendedores.id` com `ON DELETE CASCADE`)
- **Produto `1 : 1` Precificação** (`precificacoes.id_produto UNIQUE` → `produtos.id` com `ON DELETE CASCADE`, incluindo `valor_promocional` e `percentual_desconto`)
- **Produto `1 : N` Produto Brinde** (`produto_brindes.id_produto` → `produtos.id` com `ON DELETE CASCADE` e restrição `UNIQUE (id_produto, tipo_brinde)`)

---

## Estrutura do Projeto

```text
Precificov1/
├── backend/                     # API REST Spring Boot (Maven)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/br/com/precifico/
│       │   │   ├── builder/
│       │   │   ├── config/
│       │   │   ├── controller/
│       │   │   ├── dao/
│       │   │   ├── database/
│       │   │   ├── decorator/
│       │   │   ├── dto/
│       │   │   ├── model/
│       │   │   └── service/
│       │   └── resources/application.properties
│       └── test/
│           ├── java/br/com/precifico/
│           │   ├── controller/  # Testes REST com MockMvc + H2
│           │   ├── dao/         # Testes de persistência JDBC com H2
│           │   └── service/     # Testes unitários com JUnit 5 + Mockito
│           └── resources/
│               ├── application-test.properties
│               └── schema-test.sql
├── frontend/                    # Interface web (HTML, CSS e JS modular)
│   ├── css/
│   ├── img/
│   ├── js/
│   │   └── paginas/
│   └── *.html
├── docker-compose.yml           # Container MySQL 8.0 pré-configurado
├── scripts_banco.sql            # Criação do banco e tabelas
└── README.md
```

---

## Endpoints da API REST

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/vendedores` | Cadastra um novo vendedor e inicia a sessão |
| `POST` | `/api/auth/login` | Autentica o vendedor e inicia a sessão |
| `POST` | `/api/auth/logout` | Encerra a sessão atual |
| `GET` | `/api/produtos` | Lista todos os produtos do vendedor logado |
| `GET` | `/api/produtos/{id}` | Retorna detalhes, brindes e análise comercial de um produto |
| `POST` | `/api/produtos` | Cadastra um novo produto, sua precificação e seus brindes |
| `PUT` | `/api/produtos/{id}` | Atualiza um produto, sua precificação e seus brindes |
| `PATCH` | `/api/produtos/{id}/situacao` | Alterna a situação entre `DISPONIVEL` e `VENDIDO` |
| `DELETE` | `/api/produtos/{id}` | Exclui um produto (e em cascata sua precificação e brindes) |
| `POST` | `/api/produtos/simular` | Simula preço sugerido, lucro, margem e diagnóstico |
| `PUT` | `/api/produtos/{id}/promocao` | Aplica uma promoção (valor promocional ou percentual de desconto) |
| `DELETE` | `/api/produtos/{id}/promocao` | Remove a promoção ativa do produto |

---

## Pré-requisitos

- **Java 17+** (JDK)
- **Maven 3.8+** (instalado no terminal ou embutido em uma IDE como IntelliJ IDEA / Eclipse / VS Code)
- **Docker** (opcional, recomendado para subir o MySQL 8.0 rapidamente) ou **MySQL 8.0** instalado localmente

---

## Como Rodar Localmente

### 1. Clonar o Repositório
```bash
git clone https://github.com/AbadeMTH/Precifico.git
cd Precifico
```

### 2. Configurar e Subir o Banco de Dados

**Opção A — Usando Docker Compose (recomendado):**
```bash
docker compose up -d
```
O container MySQL 8.0 será criado na porta `3306` executando automaticamente o script `scripts_banco.sql` (usuário `root`, senha `root`, banco `precifico`).

> **Atenção:** caso você já tenha criado o container anteriormente (com um volume `mysql_dados` antigo sem a tabela `produto_brindes` ou as colunas de promoção), recrie o volume para reaplicar o `scripts_banco.sql`:
> ```bash
> docker compose down -v
> docker compose up -d
> ```

**Opção B — Usando MySQL instalado localmente:**
1. Execute o script `scripts_banco.sql` no seu cliente MySQL.
2. Caso a URL, o usuário ou a senha do seu MySQL sejam diferentes de `jdbc:mysql://localhost:3306/precifico` / `root` / `root`, basta definir as propriedades abaixo em `backend/src/main/resources/application.properties` (sem precisar editar código Java):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/precifico
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   ```

### 3. Executar o Backend (Spring Boot)
- **Pela IDE (IntelliJ IDEA / Eclipse / VS Code):** abra o projeto Maven em `backend/` e execute a classe `br.com.precifico.PrecificoApplication`.
- **Pelo terminal (dentro da pasta `backend/`):**
  ```bash
  cd backend
  mvn spring-boot:run
  ```
A API ficará disponível na porta `8080`.

### 4. Acessar o Frontend
- **Direto pelo Spring Boot (recomendado):** abra no navegador:
  ```text
  http://localhost:8080/
  ```
- **Ou via Live Server (VS Code):** abra `frontend/index.html` com o Live Server (`http://127.0.0.1:5500/...`). O arquivo `frontend/js/app.js` ajusta automaticamente o host da API para a porta `8080`.

---

## Como Executar os Testes Automatizados

Os testes utilizam **JUnit 5**, **Mockito**, **MockMvc** e banco em memória **H2** (perfil `test` em `backend/src/test/resources/application-test.properties`), portanto **não exigem o MySQL em execução**.

- **Pelo terminal (dentro da pasta `backend/`):**
  ```bash
  cd backend
  mvn test
  ```
- **Pela IDE (IntelliJ IDEA / VS Code / Eclipse):** execute a pasta `backend/src/test/java` (ou qualquer classe de teste individualmente).

### Cobertura atual (fluxo de Cadastro, Atualização e Deleção de Produto)
- **`ProdutoServiceTest`** (`service/`): 19 testes unitários com Mockito validando regras de negócio, Decorator de brindes, preservação/remoção de promoções e respostas `201`, `200`, `400`, `401`, `403` e `404`.
- **`ProdutoFluxoDAOTest`** (`dao/`): 6 testes de persistência SQL real no H2 validando `ProdutoDAO`, `PrecificacaoDAO`, `ProdutoBrindeDAO`, violação de chave estrangeira e exclusão em cascata (`ON DELETE CASCADE`).
- **`ControleProdutoTest`** (`controller/`): 11 testes de API REST com `MockMvc` + H2 validando os endpoints `POST /api/produtos`, `PUT /api/produtos/{id}` e `DELETE /api/produtos/{id}` com sessão HTTP.
