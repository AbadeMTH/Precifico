package br.com.precifico.service;

import br.com.precifico.dto.CadastroVendedorRequisicao;
import br.com.precifico.dto.LoginRequisicao;
import br.com.precifico.dto.RespostaApi;

public interface IVendedorService {
    RespostaApi cadastrar(CadastroVendedorRequisicao requisicao) throws Exception;

    RespostaApi entrar(LoginRequisicao requisicao) throws Exception;

    RespostaApi sair();
}
