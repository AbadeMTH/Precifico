package br.com.precifico.command.action;

import br.com.precifico.command.ICommand;
import br.com.precifico.model.Precificacao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SimularProdutoAction implements ICommand {

    @Override
    public Map<String, Object> executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();

        try {
            String strCusto = request.getParameter("custo");
            String strMargem = request.getParameter("margem");
            String strVenda = request.getParameter("valorVenda");

            double custo = (strCusto != null && !strCusto.trim().isEmpty())
                    ? Double.parseDouble(strCusto.trim().replace(",", ".")) : 0.0;
            double margem = (strMargem != null && !strMargem.trim().isEmpty())
                    ? Double.parseDouble(strMargem.trim().replace(",", ".")) : 0.0;
            double venda = (strVenda != null && !strVenda.trim().isEmpty())
                    ? Double.parseDouble(strVenda.trim().replace(",", ".")) : 0.0;

            Precificacao prec = new Precificacao(venda, custo);
            double sugerido = (custo > 0 && margem >= 0) ? prec.calcularPrecoSugerido(margem) : 0.0;
            double lucro = (custo > 0 && venda > 0) ? prec.calcularLucro() : 0.0;
            double margemEfetiva = (custo > 0 && venda > 0) ? prec.getMargemEfetiva() : 0.0;
            String diagnostico = (custo > 0 && venda > 0) ? prec.classificarPreco(margem) : "";

            String estiloAviso = "aviso";
            if (custo > 0 && venda > 0) {
                if (venda < custo) {
                    estiloAviso = "perigo";
                } else if (diagnostico.contains("próximo")) {
                    estiloAviso = "sucesso";
                } else if (diagnostico.contains("Atenção") || diagnostico.contains("abaixo") || diagnostico.contains("acima")) {
                    estiloAviso = "atencao";
                }
            }

            resultado.put("valido", true);
            resultado.put("precoSugerido", sugerido);
            resultado.put("lucro", lucro);
            resultado.put("margemEfetiva", margemEfetiva);
            resultado.put("diagnostico", diagnostico);
            resultado.put("estiloAviso", estiloAviso);
        } catch (Exception ex) {
            resultado.put("valido", false);
            resultado.put("erro", "Valores numéricos inválidos.");
        }
        return resultado;
    }
}
