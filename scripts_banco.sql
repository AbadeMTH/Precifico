CREATE DATABASE IF NOT EXISTS precifico;

USE precifico;

CREATE TABLE IF NOT EXISTS vendedores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    idade INT,
    endereco VARCHAR(200),
    cpf VARCHAR(14)
);

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

CREATE TABLE IF NOT EXISTS precificacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_produto INT NOT NULL UNIQUE,
    valor_venda DOUBLE NOT NULL,
    custo_produto DOUBLE NOT NULL,
    lucro DOUBLE NOT NULL,
    CONSTRAINT fk_precificacao_produto FOREIGN KEY (id_produto) 
        REFERENCES produtos(id) ON DELETE CASCADE
);
