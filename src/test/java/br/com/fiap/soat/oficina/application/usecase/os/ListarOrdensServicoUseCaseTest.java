package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - ListarOrdensServicoUseCase")
class ListarOrdensServicoUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort repository;

    private ListarOrdensServicoUseCase useCase;
    private OrdemServico os;

    @BeforeEach
    void setUp() {
        useCase = new ListarOrdensServicoUseCase(repository);
        os = OrdemServico.abrirNova(UUID.randomUUID(), UUID.randomUUID(), "Barulho na suspensão");
    }

    @Test
    @DisplayName("Deve listar todas as OS quando nenhum status for informado")
    void deveListarTodas() {
        when(repository.listarTodas()).thenReturn(List.of(os));

        List<OrdemServicoOutput> resultado = useCase.listar(null);

        assertThat(resultado).hasSize(1);
        verify(repository).listarTodas();
    }

    @Test
    @DisplayName("Deve filtrar OS por status quando informado")
    void deveFiltrarPorStatus() {
        when(repository.buscarPorStatus(StatusOS.RECEBIDA)).thenReturn(List.of(os));

        List<OrdemServicoOutput> resultado = useCase.listar(StatusOS.RECEBIDA);

        assertThat(resultado).hasSize(1);
        verify(repository).buscarPorStatus(StatusOS.RECEBIDA);
    }

    @Test
    @DisplayName("Deve buscar OS por ID")
    void deveBuscarPorId() {
        when(repository.buscarPorId(os.getId())).thenReturn(Optional.of(os));

        assertThat(useCase.buscarPorId(os.getId()).id()).isEqualTo(os.getId());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException para ID inexistente")
    void deveFalharIdInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.buscarPorId(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}

