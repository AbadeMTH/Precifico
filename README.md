# Precifico

Sistema web para auxiliar vendedores no gerenciamento e na precificação de produtos, composto por uma **API REST em Spring Boot** e um **frontend em HTML, CSS e JavaScript puro**, estruturados com princípios **SOLID**.

---

## Funcionalidades

- Cadastro, login e logout de vendedores (autenticação por sessão `HttpSession`).
- Cadastro, listagem, visualização detalhada, edição e exclusão de produtos vinculados ao vendedor logado.
- Simulação em tempo real de preço sugerido, lucro estimado, margem efetiva sobre o custo e diagnóstico comercial.
- Alteração rápida da situação do produto entre `DISPONIVEL` e `VENDIDO`.
- Imagem padrão de fallback caso a foto do produto não carregue.

---

## Tecnologias

- **Backend:** Java 17, Spring Boot 3.4.3 (Spring Web + Tomcat embutido), Maven, JDBC e MySQL Connector/J
- **Frontend:** HTML5, CSS3 e JavaScript puro (ES6)
- **Banco de Dados:** MySQL 8.0 (com suporte a Docker Compose)

---

## Arquitetura e Princípios SOLID

### Backend (`backend/`)
Estruturado em camadas desacopladas por interfaces e injeção de dependência via construtor:

- `controller/` — Endpoints REST (`ControleProduto`, `ControleVendedor`) e controle de sessão HTTP.
- `service/` — Regras de negócio e coordenação (`IProdutoService`, `IPrecificacaoService`, `IVendedorService` e implementações `@Service`).
- `dao/` — Persistência isolada por tabela (`IProdutoDAO`, `IPrecificacaoDAO`, `IVendedorDAO` e implementações `@Repository`).
- `database/` — Abstração de conexão JDBC (`IConexaoFactory` e `FabricaConexao`).
- `model/` e `builder/` — Entidades de domínio (`Vendedor`, `Produto`, `Precificacao`, `SituacaoProduto`) e `ProdutoBuilder`.
- `dto/` — Objetos de entrada JSON e resposta padronizada (`RespostaApi`).

### Frontend (`frontend/`)
JavaScript modularizado em `frontend/js/` com responsabilidade única e injeção via construtor:

- `httpClient.js` — Cliente HTTP genérico (`fetch`, JSON e `credentials: 'include'`).
- `sessao.js` — Armazenamento local do vendedor (`localStorage`) e mensagens temporárias (`sessionStorage`).
- `authServico.js` — Comunicação com endpoints de autenticação e cadastro de vendedor.
- `produtoServico.js` — Comunicação com endpoints de produtos e simulação de precificação.
- `ui.js` — Formatadores PT-BR, alertas, classificação visual do diagnóstico e proteção de rotas.
- `app.js` — Ponto central de instanciação dos serviços (`window.PrecificoApp`).

### Relacionamentos no Banco (`scripts_banco.sql`)
- **Vendedor `1 : N` Produto** (`produtos.id_vendedor` → `vendedores.id` com `ON DELETE CASCADE`)
- **Produto `1 : 1` Precificação** (`precificacoes.id_produto UNIQUE` → `produtos.id` com `ON DELETE CASCADE`)

---

## Estrutura do Projeto

```text
Precificov1/
├── backend/                 # API REST Spring Boot (Maven)
│   ├── pom.xml
│   └── src/main/
│       ├── java/br/com/precifico/
│       └── resources/application.properties
├── frontend/                # Interface web (HTML, CSS e JS modular)
│   ├── css/
│   ├── img/
│   ├── js/
│   └── *.html
├── docker-compose.yml       # Container MySQL 8.0 pré-configurado
├── scripts_banco.sql        # Criação do banco e tabelas
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
| `GET` | `/api/produtos/{id}` | Retorna detalhes e análise comercial de um produto |
| `POST` | `/api/produtos` | Cadastra um novo produto e sua precificação |
| `PUT` | `/api/produtos/{id}` | Atualiza um produto e sua precificação |
| `PATCH` | `/api/produtos/{id}/situacao` | Alterna a situação entre `DISPONIVEL` e `VENDIDO` |
| `DELETE` | `/api/produtos/{id}` | Exclui um produto |
| `POST` | `/api/produtos/simular` | Simula preço sugerido, lucro, margem e diagnóstico |

---

## Como Rodar Localmente

### 1. Clonar o Repositório
```bash
git clone https://github.com/AbadeMTH/Precifico.git
```

### 2. Configurar e Subir o Banco de Dados

**Opção A — Usando Docker Compose (recomendado):**
```bash
docker compose up -d
```
O container MySQL 8.0 será criado na porta `3306` executando automaticamente o script `scripts_banco.sql` (usuário `root`, senha `root`, banco `precifico`).

**Opção B — Usando MySQL instalado localmente:**
1. Execute o script `scripts_banco.sql` no seu cliente MySQL.
2. Caso o usuário ou senha do seu MySQL não sejam `root` / `root`, ajuste em `backend/src/main/java/br/com/precifico/database/FabricaConexao.java`.

### 3. Executar o Backend (Spring Boot)
- **Pela IDE (IntelliJ IDEA / Eclipse / VS Code):** abra o projeto Maven em `backend/` e execute a classe `br.com.precifico.PrecificoApplication`.
- **Pelo terminal (dentro da pasta `backend/`):**
```bash
mvn spring-boot:run
```
A API ficará disponível na porta `8080`.

### 4. Acessar o Frontend
- **Direto pelo Spring Boot (recomendado):** abra no navegador:
  ```text
  http://localhost:8080/
  ```
- **Ou via Live Server (VS Code):** abra `frontend/index.html` com o Live Server (`http://127.0.0.1:5500/...`). O arquivo `frontend/js/app.js` ajusta automaticamente o host da API para a porta `8080`.
