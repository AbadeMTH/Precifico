package command.action;

import command.ICommand;
import model.Precificacao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Locale;

public class SimularProdutoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

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

            String json = String.format(Locale.US,
                    "{\"valido\": true, \"precoSugerido\": %.2f, \"lucro\": %.2f, \"margemEfetiva\": %.2f, \"diagnostico\": \"%s\", \"estiloAviso\": \"%s\"}",
                    sugerido, lucro, margemEfetiva, diagnostico.replace("\"", "\\\""), estiloAviso
            );
            out.print(json);
        } catch (Exception ex) {
            out.print("{\"valido\": false, \"erro\": \"Valores numéricos inválidos.\"}");
        }
        out.flush();
        return null;
    }
}
