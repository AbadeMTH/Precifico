# Precifico

Sistema web em Java para auxiliar vendedores no gerenciamento e na precificação de produtos para venda.

---

## Funcionalidades

- Cadastro e login de vendedores.
- Cadastro, edição, visualização e exclusão de produtos.
- Sugestão automática de preço de venda e cálculo de lucro estimado.
- Alteração do status do produto entre disponível e vendido.
- Imagem padrão de fallback caso a foto do produto não carregue.

---

## Tecnologias

- Java (JSP e Servlets)
- MySQL
- Apache Tomcat

---

## Como Rodar Localmente

### 1. Clonar o Repositório
```bash
git clone https://github.com/AbadeMTH/Precifico.git
```

### 2. Configurar o Banco de Dados
1. Abra seu cliente MySQL (como o MySQL Workbench ou terminal).
2. Execute o script `scripts_banco.sql` que está na raiz do projeto para criar o banco e as tabelas.
3. Se a senha do seu MySQL não for `root`, altere o usuário e a senha no arquivo `src/database/FabricaConexao.java`.

### 3. Executar o Projeto
1. Abra a pasta do projeto na sua IDE (NetBeans, IntelliJ IDEA ou Eclipse).
2. Execute o projeto utilizando o servidor Apache Tomcat.

### 4. Acessar
Abra o navegador e acesse:
```
http://localhost:8080/Precificov1/
```
(ou `http://localhost:8080/`, dependendo da configuração da sua IDE).
