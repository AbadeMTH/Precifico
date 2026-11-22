package br.com.precifico.service;

import br.com.precifico.dao.IVendedorDAO;
import br.com.precifico.dto.CadastroVendedorRequisicao;
import br.com.precifico.dto.LoginRequisicao;
import br.com.precifico.dto.RespostaApi;
import br.com.precifico.model.Vendedor;
import org.springframework.stereotype.Service;

@Service
public class VendedorService implements IVendedorService {

    private final IVendedorDAO vendedorDAO;

    public VendedorService(IVendedorDAO vendedorDAO) {
        this.vendedorDAO = vendedorDAO;
    }

    @Override
    public RespostaApi cadastrar(CadastroVendedorRequisicao requisicao) throws Exception {
        RespostaApi erroValidacao = validarCadastro(requisicao);
        if (erroValidacao != null) {
            return erroValidacao;
        }

        String emailNormalizado = requisicao.getEmail().trim().toLowerCase();
        Vendedor filtroEmail = criarFiltroEmail(emailNormalizado);

        if (vendedorDAO.consultarPorEmail(filtroEmail) != null) {
            return RespostaApi.erro(409, "Já existe um vendedor cadastrado com este e-mail.");
        }

        Vendedor vendedor = construirVendedor(requisicao, emailNormalizado);
        vendedorDAO.cadastrar(vendedor);

        Vendedor vendedorSalvo = vendedorDAO.consultarPorEmail(filtroEmail);
        Vendedor vendedorFinal = vendedorSalvo != null ? vendedorSalvo : vendedor;

        String mensagem = "Bem-vindo, " + vendedorFinal.getNome() + "! Cadastro realizado com sucesso.";
        return RespostaApi.sucesso(201, mensagem, vendedorFinal);
    }

    @Override
    public RespostaApi entrar(LoginRequisicao requisicao) throws Exception {
        if (requisicao == null || estaVazio(requisicao.getEmail()) || estaVazio(requisicao.getSenha())) {
            return RespostaApi.erro(400, "Informe e-mail e senha para acessar.");
        }

        Vendedor credenciais = new Vendedor();
        credenciais.setEmail(requisicao.getEmail().trim().toLowerCase());
        credenciais.setSenha(requisicao.getSenha());

        Vendedor vendedor = vendedorDAO.autenticar(credenciais);
        if (vendedor == null) {
            return RespostaApi.erro(401, "E-mail ou senha inválidos. Verifique suas credenciais.");
        }

        String mensagem = "Login efetuado com sucesso! Olá, " + vendedor.getNome() + ".";
        return RespostaApi.sucesso(200, mensagem, vendedor);
    }

    @Override
    public RespostaApi sair() {
        return RespostaApi.sucesso(200, "Você saiu da sua conta com segurança.", null);
    }

    private RespostaApi validarCadastro(CadastroVendedorRequisicao requisicao) {
        if (requisicao == null || estaVazio(requisicao.getNome())
                || estaVazio(requisicao.getEmail()) || estaVazio(requisicao.getSenha())) {
            return RespostaApi.erro(400, "Nome, e-mail e senha são campos obrigatórios.");
        }

        if (requisicao.getSenha().length() < 6) {
            return RespostaApi.erro(400, "A senha deve conter no mínimo 6 caracteres.");
        }

        if (requisicao.getConfirmaSenha() != null && !requisicao.getSenha().equals(requisicao.getConfirmaSenha())) {
            return RespostaApi.erro(400, "As senhas não conferem. Digite a mesma senha nos dois campos.");
        }

        return null;
    }

    private Vendedor criarFiltroEmail(String emailNormalizado) {
        Vendedor filtro = new Vendedor();
        filtro.setEmail(emailNormalizado);
        return filtro;
    }

    private Vendedor construirVendedor(CadastroVendedorRequisicao requisicao, String emailNormalizado) {
        int idade = requisicao.getIdade() != null ? requisicao.getIdade() : 0;
        String endereco = requisicao.getEndereco() != null ? requisicao.getEndereco().trim() : "";
        String cpf = requisicao.getCpf() != null ? requisicao.getCpf().trim() : "";

        return new Vendedor(
                requisicao.getNome().trim(),
                emailNormalizado,
                requisicao.getSenha(),
                idade,
                endereco,
                cpf
        );
    }

    private boolean estaVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
