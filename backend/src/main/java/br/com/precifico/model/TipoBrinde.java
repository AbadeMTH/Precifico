package br.com.precifico.model;

public enum TipoBrinde {
    BALA("Bala"),
    PIRULITO("Pirulito"),
    ADESIVO("Adesivo");

    private final String rotulo;

    TipoBrinde(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public static TipoBrinde deTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        String valorNormalizado = texto.trim();
        for (TipoBrinde tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valorNormalizado)
                    || tipo.rotulo.equalsIgnoreCase(valorNormalizado)) {
                return tipo;
            }
        }
        return null;
    }
}
