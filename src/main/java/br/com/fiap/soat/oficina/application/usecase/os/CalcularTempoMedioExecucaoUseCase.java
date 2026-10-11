package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.response.MetricasTempoMedioOutput;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class CalcularTempoMedioExecucaoUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepository;

    public CalcularTempoMedioExecucaoUseCase(OrdemServicoRepositoryPort ordemServicoRepository) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository);
    }

    public MetricasTempoMedioOutput executar() {
        List<OrdemServico> ordensConcluidas = ordemServicoRepository.listarTodas().stream()
                .filter(os -> os.getDataAprovacao() != null && os.getDataFinalizacao() != null)
                .toList();

        if (ordensConcluidas.isEmpty()) {
            return MetricasTempoMedioOutput.vazio();
        }

        long somaSegundos = ordensConcluidas.stream()
                .mapToLong(os -> Duration.between(os.getDataAprovacao(), os.getDataFinalizacao()).toSeconds())
                .sum();

        double mediaSegundos = (double) somaSegundos / ordensConcluidas.size();
        double mediaHoras = mediaSegundos / 3600.0;
        double mediaDias = mediaHoras / 24.0;

        long horas = (long) (mediaSegundos / 3600);
        long minutos = (long) ((mediaSegundos % 3600) / 60);
        String tempoFormatado = String.format("%dh %dm", horas, minutos);

        return new MetricasTempoMedioOutput(
                ordensConcluidas.size(),
                Math.round(mediaHoras * 100.0) / 100.0,
                Math.round(mediaDias * 100.0) / 100.0,
                tempoFormatado
        );
    }
}

