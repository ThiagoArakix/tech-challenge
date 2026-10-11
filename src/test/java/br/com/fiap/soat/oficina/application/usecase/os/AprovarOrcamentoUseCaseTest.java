package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.domain.entity.ItemPecaOrdem;
import br.com.fiap.soat.oficina.domain.entity.ItemServicoOrdem;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - AprovarOrcamentoUseCase")
class AprovarOrcamentoUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private PecaRepositoryPort pecaRepository;

    @InjectMocks
    private AprovarOrcamentoUseCase useCase;

    private OrdemServico os;
    private Peca peca;

    @BeforeEach
    void setUp() {
        os = OrdemServico.abrirNova(UUID.randomUUID(), UUID.randomUUID(), "Problema no radiador");
        peca = Peca.criarNova("RAD-01", "Válvula Termostática", "Válvula", Dinheiro.de(120.00), 10, 2);

        os.iniciarDiagnostico();
        os.registrarDiagnostico("Válvula travada fechada");
        os.adicionarItemServico(ItemServicoOrdem.criarNovo("Troca de válvula", 1, Dinheiro.de(80.00)));
        os.adicionarItemPeca(ItemPecaOrdem.criarNovo(peca.getId(), 2, peca.getValorUnitario()));
        os.enviarParaAprovacao();
    }

    @Test
    @DisplayName("Deve debitar estoque de cada peça física no catálogo e avançar status para EM_EXECUCAO")
    void deveAprovarOrcamentoEDebitarEstoque() {
        when(ordemServicoRepository.buscarPorId(os.getId())).thenReturn(Optional.of(os));
        when(pecaRepository.buscarPorId(peca.getId())).thenReturn(Optional.of(peca));
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServicoOutput output = useCase.executar(os.getId());

        assertThat(output.status()).isEqualTo(StatusOS.EM_EXECUCAO);
        assertThat(output.dataAprovacao()).isNotNull();

        // Verifica que o estoque da peça foi debitado de 10 para 8
        assertThat(peca.getSaldoEstoque()).isEqualTo(8);

        verify(pecaRepository).salvar(peca);
        verify(ordemServicoRepository).salvar(os);
    }
}

