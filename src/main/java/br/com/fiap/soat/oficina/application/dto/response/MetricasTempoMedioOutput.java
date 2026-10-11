package br.com.fiap.soat.oficina.application.dto.response;

public record MetricasTempoMedioOutput(
        long totalOrdensFinalizadas,
        double tempoMedioEmHoras,
        double tempoMedioEmDias,
        String tempoMedioFormatado
) {
    public static MetricasTempoMedioOutput vazio() {
        return new MetricasTempoMedioOutput(0, 0.0, 0.0, "0h 0m");
    }
}

