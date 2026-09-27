USE precifico;

-- Adiciona novas colunas na tabela vendedores se já existir
ALTER TABLE vendedores 
    ADD COLUMN idade INT NULL,
    ADD COLUMN endereco VARCHAR(200) NULL,
    ADD COLUMN cpf VARCHAR(14) NULL;

-- Tabela de Produtos (1:N com Vendedores)
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

-- Tabela de Precificações (1:1 com Produtos)
CREATE TABLE IF NOT EXISTS precificacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_produto INT NOT NULL UNIQUE,
    valor_venda DOUBLE NOT NULL,
    custo_produto DOUBLE NOT NULL,
    lucro DOUBLE NOT NULL,
    CONSTRAINT fk_precificacao_produto FOREIGN KEY (id_produto) 
        REFERENCES produtos(id) ON DELETE CASCADE
);
