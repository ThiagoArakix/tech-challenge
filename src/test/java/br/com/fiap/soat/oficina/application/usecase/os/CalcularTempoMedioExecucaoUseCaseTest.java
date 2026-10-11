package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.response.MetricasTempoMedioOutput;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - CalcularTempoMedioExecucaoUseCase")
class CalcularTempoMedioExecucaoUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @InjectMocks
    private CalcularTempoMedioExecucaoUseCase useCase;

    @Test
    @DisplayName("Deve calcular tempo médio corretamente com ordens concluídas")
    void deveCalcularTempoMedio() {
        Instant agora = Instant.now();

        // OS 1: 4 horas de execução
        OrdemServico os1 = new OrdemServico(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                null, "Problema 1", null, new ArrayList<>(), new ArrayList<>(),
                agora.minus(10, ChronoUnit.HOURS),
                agora.minus(6, ChronoUnit.HOURS),
                agora.minus(2, ChronoUnit.HOURS),
                null, agora.minus(10, ChronoUnit.HOURS), null);

        // OS 2: 2 horas de execução
        OrdemServico os2 = new OrdemServico(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                null, "Problema 2", null, new ArrayList<>(), new ArrayList<>(),
                agora.minus(5, ChronoUnit.HOURS),
                agora.minus(3, ChronoUnit.HOURS),
                agora.minus(1, ChronoUnit.HOURS),
                null, agora.minus(5, ChronoUnit.HOURS), null);

        when(ordemServicoRepository.listarTodas()).thenReturn(List.of(os1, os2));

        MetricasTempoMedioOutput metricas = useCase.executar();

        assertThat(metricas.totalOrdensFinalizadas()).isEqualTo(2);
        // Média de 4h e 2h = 3h
        assertThat(metricas.tempoMedioEmHoras()).isEqualTo(3.0);
        assertThat(metricas.tempoMedioFormatado()).isEqualTo("3h 0m");
    }

    @Test
    @DisplayName("Deve retornar métrica vazia quando não existirem ordens concluídas")
    void deveRetornarVazioQuandoSemOrdens() {
        when(ordemServicoRepository.listarTodas()).thenReturn(List.of());

        MetricasTempoMedioOutput metricas = useCase.executar();

        assertThat(metricas.totalOrdensFinalizadas()).isZero();
        assertThat(metricas.tempoMedioEmHoras()).isZero();
        assertThat(metricas.tempoMedioFormatado()).isEqualTo("0h 0m");
    }
}

