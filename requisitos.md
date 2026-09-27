# Requisitos do Projeto

## 1. Visão Geral do Projeto

O projeto propõe uma aplicação web para auxiliar vendedores no cadastro, na precificação e no gerenciamento de produtos destinados à venda. Ao cadastrar um produto, o vendedor poderá informar a foto, a descrição, o valor de custo e o percentual de lucro desejado. Com base nesses dados, o sistema calculará automaticamente uma sugestão de preço de venda.

O vendedor poderá aceitar a sugestão ou definir outro valor. Quando o preço informado estiver distante do valor calculado, a aplicação exibirá uma mensagem rápida indicando se ele está abaixo, próximo ou acima da sugestão. A mensagem será apenas informativa e não impedirá a conclusão do cadastro.

## 2. Objetivo

O objetivo é facilitar a gestão e a precificação de produtos, oferecendo ao vendedor uma referência calculada a partir do custo e do lucro desejado, sem retirar sua autonomia para definir o preço final.

## 3. Usuário do Sistema

A primeira versão terá um único perfil de usuário:

- **Vendedor:** responsável por cadastrar, consultar, editar, excluir, precificar e alterar a situação dos próprios produtos.

## 4. Requisitos Funcionais

Os requisitos funcionais descrevem as operações e os comportamentos que a aplicação deverá oferecer ao vendedor.

### Quadro 1 – Requisitos funcionais

| Código | Descrição do requisito |
|---|---|
| RF01 | O sistema deve permitir o cadastro do vendedor com nome, e-mail e senha. |
| RF02 | O sistema deve permitir que o vendedor realize login utilizando e-mail e senha. |
| RF03 | O sistema deve permitir que o vendedor encerre sua sessão por meio da opção de logout. |
| RF04 | O sistema deve permitir que o vendedor cadastre produtos para venda. |
| RF05 | O sistema deve exigir, no cadastro do produto, nome, descrição, valor de custo e percentual de lucro desejado. |
| RF06 | O sistema deve permitir que o vendedor faça o upload da foto do produto por meio de URL, aceitando apenas formatos como JPEG ou PNG. |
| RF07 | O sistema deve calcular automaticamente uma sugestão de preço de venda com base no custo e no percentual de lucro informado. |
| RF08 | O sistema deve apresentar a sugestão de preço antes da conclusão do cadastro. |
| RF09 | O sistema deve permitir que o vendedor aceite o preço sugerido ou informe manualmente outro preço de venda. |
| RF10 | O sistema deve comparar o preço informado pelo vendedor com o preço sugerido. |
| RF11 | O sistema deve exibir uma mensagem indicando se o preço informado está abaixo, próximo ou acima do valor sugerido. |
| RF12 | O sistema deve mostrar o lucro estimado em reais e em porcentagem com base no preço de venda informado. |
| RF13 | O sistema deve impedir o cadastro de produtos com valores de custo ou de venda negativos. |
| RF14 | O sistema deve listar todos os produtos cadastrados pelo vendedor. |
| RF15 | O sistema deve permitir a consulta dos detalhes de um produto. |
| RF16 | O sistema deve recalcular a sugestão de preço quando o custo ou o percentual de lucro for alterado. |
| RF17 | O sistema deve permitir a exclusão de um produto mediante confirmação do vendedor. |
| RF18 | O sistema deve permitir que o vendedor altere a situação do produto entre disponível e vendido. |

**Fonte:** Elaborado pelo grupo (2026).

## 5. Requisitos Não Funcionais

Os requisitos não funcionais estabelecem características de qualidade, segurança, compatibilidade e organização técnica da aplicação.

### Quadro 2 – Requisitos não funcionais

| Código | Descrição do requisito |
|---|---|
| RNF01 | O usuário não poderá fazer login com credenciais inválidas. |
| RNF02 | O sistema não permitirá valores de venda e lucro negativos. |
| RNF03 | As mensagens de erro não deverão apresentar códigos técnicos sem uma explicação compreensível ao vendedor. |
| RNF04 | O usuário não poderá consultar, cadastrar, editar ou excluir os dados de outro usuário. |
| RNF05 | Não haverá cadastro de produtos com campos vazios. |

**Fonte:** Elaborado pelo grupo (2026).

## 6. Regras de Negócio e Cálculos

### 6.1 Cálculo do Preço Sugerido

Para manter a primeira versão simples, o percentual será tratado como um acréscimo sobre o valor de custo do produto.

**Preço sugerido = Custo + (Custo × Percentual de lucro ÷ 100)**

**Exemplo:** para um produto com custo de R$ 800,00 e percentual desejado de 25%, o lucro estimado será de R$ 200,00 e o preço sugerido será de R$ 1.000,00.

### 6.2 Cálculo do Lucro do Preço Definido

Se o vendedor informar um preço diferente da sugestão, o sistema recalculará o lucro estimado e o percentual efetivo sobre o custo.

**Lucro estimado = Preço de venda − Valor de custo**

**Percentual efetivo = (Lucro estimado ÷ Valor de custo) × 100**

A primeira versão não considerará impostos, comissões, frete ou outras despesas adicionais.

### 6.3 Avaliação do Preço Informado

O sistema utilizará faixas objetivas para classificar a diferença entre o preço informado e o preço sugerido.

### Quadro 3 – Avaliação do preço informado

| Comparação com a sugestão | Mensagem apresentada |
|---|---|
| Mais de 20% abaixo | Atenção: o preço está muito abaixo da sugestão. |
| Entre 5% e 20% abaixo | O preço está abaixo da sugestão. |
| Diferença de até 5% | O preço está próximo do valor sugerido. |
| Entre 5% e 20% acima | O preço está acima da sugestão. |
| Mais de 20% acima | Atenção: o preço está muito acima da sugestão. |

**Fonte:** Elaborado pelo grupo (2026).

As mensagens terão caráter informativo. O vendedor continuará livre para definir o preço que considerar adequado.

## 7. Escopo da Primeira Versão

Considerando o prazo de 17 dias, a primeira versão deverá priorizar as funcionalidades que comprovam a proposta central do sistema:

- Cadastro e login do vendedor.
- Cadastro de produtos com foto.
- Cálculo da sugestão de preço.
- Definição manual do preço de venda.
- Mensagens sobre a diferença entre o preço informado e o sugerido.
- Cálculo do lucro estimado.
- Listagem, edição e exclusão dos produtos.
- Alteração da situação do produto entre disponível e vendido.

Funcionalidades como pagamento on-line, carrinho de compras, comunicação com clientes, relatórios avançados, emissão de nota fiscal e publicação automática em marketplaces ficarão fora da primeira versão.

## 8. Critérios de Validação da Proposta

A proposta será considerada validada para desenvolvimento quando o professor aprovar o objetivo, o escopo da primeira versão, a regra de cálculo do preço sugerido e o conjunto de requisitos apresentados neste documento.

Mudanças aprovadas posteriormente deverão ser registradas antes da implementação.
