package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;

import java.util.Objects;
import java.util.UUID;

public class AtualizarStatusOSUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepository;

    public AtualizarStatusOSUseCase(OrdemServicoRepositoryPort ordemServicoRepository) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository);
    }

    public OrdemServicoOutput iniciarDiagnostico(UUID osId) {
        OrdemServico os = buscarOrdem(osId);
        os.iniciarDiagnostico();
        return salvarERetornar(os);
    }

    public OrdemServicoOutput registrarDiagnostico(UUID osId, String laudoDiagnostico) {
        OrdemServico os = buscarOrdem(osId);
        os.registrarDiagnostico(laudoDiagnostico);
        return salvarERetornar(os);
    }

    public OrdemServicoOutput submeterParaAprovacao(UUID osId) {
        OrdemServico os = buscarOrdem(osId);
        os.enviarParaAprovacao();
        return salvarERetornar(os);
    }

    public OrdemServicoOutput finalizarExecucao(UUID osId) {
        OrdemServico os = buscarOrdem(osId);
        os.finalizar();
        return salvarERetornar(os);
    }

    public OrdemServicoOutput registrarEntrega(UUID osId) {
        OrdemServico os = buscarOrdem(osId);
        os.entregar();
        return salvarERetornar(os);
    }

    public OrdemServicoOutput cancelar(UUID osId) {
        OrdemServico os = buscarOrdem(osId);
        os.cancelar();
        return salvarERetornar(os);
    }

    private OrdemServico buscarOrdem(UUID osId) {
        return ordemServicoRepository.buscarPorId(osId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Ordem de Serviço", osId));
    }

    private OrdemServicoOutput salvarERetornar(OrdemServico os) {
        OrdemServico salva = ordemServicoRepository.salvar(os);
        return OrdemServicoOutput.deDominio(salva);
    }
}

