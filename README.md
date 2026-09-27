# Precifico 🏷️

Sistema web desenvolvido em Java para auxiliar vendedores e pequenos empreendedores no gerenciamento de estoque e no cálculo estratégico de precificação de produtos para venda.

---

## 📌 Funcionalidades

- **Autenticação de Vendedores**: Cadastro completo e login seguro com controle de sessão.
- **Gestão de Produtos (CRUD)**: Cadastro, listagem, visualização de detalhes, edição e exclusão de produtos.
- **Cálculo Automático de Precificação**: Sugestão inteligente de preço de venda com base no custo informado e na margem de lucro desejada.
- **Controle de Situação**: Alteração rápida do status do produto entre `DISPONÍVEL` e `VENDIDO`.
- **Tratamento Visual**: Fallback de imagem padrão caso a URL da imagem não carregue ou não seja informada.
- **Padrões de Projeto**:
  - **MVC** (*Model-View-Controller*) para separação de responsabilidades.
  - **Command + Factory Method** para orquestração modular das ações de negócio (`CadastrarProdutoAction`, `AlterarSituacaoProdutoAction`, etc.).
  - **Builder Pattern** (`ProdutoBuilder`) para construção segura de instâncias de produto.
  - **DAO Pattern** para persistência e abstração de consultas SQL.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java (JSP, Servlets, JSTL)
- **Banco de Dados**: MySQL 8.0
- **Servidor Web / Servlet Container**: Apache Tomcat (versão 8.5.x ou 9.x recomendada)
- **Containerização**: Docker e Docker Compose
- **Dependências**:
  - `javax.servlet-api-4.0.1`
  - `jstl-1.2`
  - `mysql-connector-j-8.x / 26.x`

---

## 📋 Pré-requisitos

Antes de iniciar, certifique-se de ter instalado em sua máquina:

1. **Git**: Para clonar o repositório ([Download Git](https://git-scm.com/)).
2. **Java JDK**: Versão 8, 11, 17 ou superior instalada e configurada nas variáveis de ambiente (`JAVA_HOME`).
3. **Apache Tomcat**: Versão **8.5.x** ou **9.x** ([Download Tomcat](https://tomcat.apache.org/download-90.cgi)).
   > **Nota importante**: Como o projeto utiliza a especificação `javax.servlet`, utilize o Tomcat 8.5 ou 9. O Tomcat 10+ utiliza o pacote `jakarta.servlet` e requer adaptação.
4. **Docker & Docker Compose** *(Recomendado para o banco de dados)*: [Download Docker Desktop](https://www.docker.com/products/docker-desktop/).
   *(Caso não use Docker, é necessário ter o MySQL Server 8.0 instalado localmente).*
5. **IDE de sua preferência**: IntelliJ IDEA (recomendada), Eclipse IDE for Enterprise Java ou NetBeans.

---

## 🚀 Passo a Passo: Clonar e Rodar

### 1. Clonar o Repositório

Abra o terminal na pasta onde deseja salvar o projeto e execute:

```bash
# Clonar o repositório via HTTPS
git clone https://github.com/AbadeMTH/Precifico.git

# Acessar a pasta do projeto clonado
cd Precifico
```

---

### 2. Subir o Banco de Dados com Docker 🐳 *(Recomendado)*

O projeto inclui um arquivo `docker-compose.yml` pré-configurado para subir o container do MySQL 8.0 com todas as configurações necessárias:

- **Porta**: `3306`
- **Banco de dados**: `precifico`
- **Usuário**: `root`
- **Senha**: `root`

#### Iniciar o Container:
No terminal, dentro da raiz do projeto, execute:
```bash
docker compose up -d
```
*(ou `docker-compose up -d` em versões mais antigas do Docker).*

#### Verificar se o container está em execução:
```bash
docker ps
```
Você verá o container `meu_mysql` com status *Up*.

#### Criação das Tabelas:
Após subir o container, execute o script `scripts_banco.sql` na raiz do projeto para criar as tabelas no MySQL:

**No Windows (PowerShell):**
```powershell
Get-Content scripts_banco.sql | docker exec -i meu_mysql mysql -u root -proot precifico
```

**No Linux / macOS / Git Bash:**
```bash
docker exec -i meu_mysql mysql -u root -proot precifico < scripts_banco.sql
```

*(Ou, se preferir, conecte um cliente gráfico como **DBeaver** ou **MySQL Workbench** em `localhost:3306` com usuário `root` e senha `root`, e execute o conteúdo de `scripts_banco.sql`).*

#### Comandos úteis do Docker:
```bash
# Parar o container do banco sem perder dados
docker compose stop

# Iniciar o container já existente
docker compose start

# Parar e remover o container
docker compose down

# Ver os logs do banco em tempo real
docker compose logs -f
```

---

### 2.1 (Alternativa) Configurar com MySQL Local (sem Docker)

Caso prefira usar uma instalação local do MySQL:

1. Abra seu cliente SQL (**MySQL Workbench**, **DBeaver** ou terminal `mysql`).
2. Abra e execute o arquivo `scripts_banco.sql` localizado na raiz do projeto para criar a base `precifico` e as tabelas (`vendedores`, `produtos`, `precificacoes`).
3. Caso a senha do seu MySQL local seja diferente de `root`, altere as constantes no arquivo:
   [`src/database/FabricaConexao.java`](src/database/FabricaConexao.java):
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/precifico";
   private static final String USUARIO = "root";
   private static final String SENHA = "sua_senha_aqui";
   ```

---

### 3. Como Rodar a Aplicação Localmente

A aplicação é uma aplicação web Java (Servlet + JSP). O método mais prático e recomendado é utilizar o **IntelliJ IDEA** com o plugin **Smart Tomcat**, ou configurar o servidor Tomcat no Eclipse / NetBeans.

#### Opção A: Executando no IntelliJ IDEA com Smart Tomcat (Recomendado)

1. **Abrir o Projeto**:
   - Abra o IntelliJ IDEA.
   - Clique em **Open** e selecione a pasta raiz do projeto clonado.

2. **Configurar o JDK e Bibliotecas**:
   - Acesse o menu: **File** > **Project Structure** (atalho `Ctrl + Alt + Shift + S`).
   - Em **Project**: selecione a versão do seu JDK (ex: JDK 11, 17, 21 ou superior).
   - Em **Modules** > selecione o módulo do projeto > vá até a aba **Dependencies**:
     - Verifique se a pasta `lib` do projeto está listada.
     - Se não estiver, clique no ícone de `+` > **JARs or Directories...** > selecione a pasta `lib` do projeto e confirme.
   - Clique em **Apply** e **OK**.

3. **Instalar o Plugin Smart Tomcat**:
   - Vá em **File** > **Settings** (ou `Ctrl + Alt + S`) > **Plugins**.
   - Na aba **Marketplace**, pesquise por `Smart Tomcat` e clique em **Install**.
   - Reinicie a IDE caso solicitado.

4. **Configurar a Execução do Tomcat**:
   - Vá no menu superior em **Run** > **Edit Configurations...**.
   - Clique no botão `+` (Add New Configuration) e selecione **Smart Tomcat**.
   - Preencha os campos da configuração:
     - **Name**: `PrecificoTomCat` (ou o nome que preferir)
     - **Tomcat Server**: Selecione o caminho onde você descompactou o Apache Tomcat (versão 8.5.x ou 9.x).
     - **Deployment Directory**: Selecione a pasta `web` do projeto (exemplo: `C:\Users\...\Precifico\web`).
     - **Context Path**: `/Precificov1`
     - **Server Port**: `8080`
   - Clique em **Apply** e depois em **OK**.

5. **Iniciar a Aplicação**:
   - Selecione a configuração `PrecificoTomCat` criada no canto superior direito e clique no botão verde **Run** (ou atalho `Shift + F10`).
   - Acompanhe o log no console da IDE até a mensagem de inicialização com sucesso.

---

#### Opção B: Executando no Eclipse IDE

1. Abra o Eclipse e vá em **File** > **Import** > **General** > **Projects from Folder or Archive** e selecione a pasta do projeto.
2. Certifique-se de que os JARs da pasta `lib/` estão no *Java Build Path* do projeto.
3. Na aba **Servers**, clique com o botão direito > **New** > **Server** > selecione **Apache Tomcat v9.0 Server** e aponte para a pasta de instalação do Tomcat.
4. Adicione o projeto ao servidor (*Add and Remove...*).
5. Nas configurações do servidor no Eclipse, garanta que o *Path* do módulo esteja como `/Precificov1` e a pasta base aponte para `web`.
6. Inicie o servidor clicando em **Start**.

---

## 🌐 Acessando a Aplicação

Com o banco de dados rodando e o Tomcat iniciado, abra o navegador e acesse:

```
http://localhost:8080/Precificov1/
```

Você será direcionado para a tela de autenticação (`login.jsp`):

### Fluxo Inicial Sugerido para Testes:
1. **Cadastro de Vendedor**: Clique em **"Cadastre-se"**, preencha os dados (nome, e-mail, senha, CPF, etc.) e confirme.
2. **Login**: Faça login com as credenciais cadastradas.
3. **Gerenciamento de Produtos**:
   - Clique em **"Cadastrar Produto"**.
   - Preencha código, nome, descrição, link da imagem, custo do produto e a porcentagem de margem de lucro pretendida.
   - O sistema calculará o **lucro** e o **preço sugerido de venda** automaticamente.
4. **Listagem e Ações**:
   - Visualize a tabela de produtos.
   - Altere a situação do produto (disponível ou vendido).
   - Edite dados ou exclua produtos cadastrados.

---

## 📁 Estrutura do Projeto

```plaintext
Precifico/
├── docker-compose.yml          # Configuração do container MySQL 8.0
├── scripts_banco.sql           # Script DDL de criação do banco e tabelas
├── lib/                        # Bibliotecas JAR (Servlets, JSTL, MySQL Connector)
├── src/                        # Código-fonte Java
│   ├── builder/                # Padrão Builder (ProdutoBuilder)
│   ├── command/                # Padrão Command (ICommand e Actions de negócio)
│   │   └── action/             # Implementações específicas de cada ação
│   ├── controller/             # Servlets de controle (ControleProduto, ControleVendedor)
│   ├── dao/                    # Camada de persistência (ProdutoDAO, VendedorDAO, etc.)
│   ├── database/               # Conexão com o banco (FabricaConexao)
│   └── model/                  # Modelos de domínio (Produto, Vendedor, Precificacao)
├── web/                        # Camada de visualização (Páginas JSP, CSS, Imagens)
│   ├── css/                    # Folhas de estilo da interface
│   ├── img/                    # Imagens do sistema e fallback
│   ├── WEB-INF/                # Configurações de deployment (web.xml) e classes
│   ├── login.jsp               # Página de autenticação
│   ├── cadastro_vendedor.jsp   # Formulário de novo vendedor
│   ├── produtos.jsp            # Lista de produtos do vendedor
│   └── formulario_produto.jsp  # Cadastro e precificação de produto
└── README.md                   # Documentação do projeto
```

---

## 🤝 Dúvidas e Contribuições

Sinta-se à vontade para abrir uma *Issue* ou enviar um *Pull Request* caso queira contribuir com melhorias para o sistema.
