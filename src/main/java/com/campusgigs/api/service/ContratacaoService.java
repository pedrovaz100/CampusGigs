package com.campusgigs.api.service;

import com.campusgigs.api.dto.ContratacaoResponse;
import com.campusgigs.api.dto.ContratarServicoRequest;
import com.campusgigs.api.entity.Contratacao;
import com.campusgigs.api.entity.Servico;
import com.campusgigs.api.entity.Usuario;
import com.campusgigs.api.entity.enums.SituacaoContratacao;
import com.campusgigs.api.entity.enums.SituacaoServico;
import com.campusgigs.api.exception.RecursoNaoEncontradoException;
import com.campusgigs.api.exception.RegraNegocioException;
import com.campusgigs.api.repository.ContratacaoRepository;
import com.campusgigs.api.repository.ServicoRepository;
import com.campusgigs.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratacaoService {

    private final ContratacaoRepository contratacaoRepository;
    private final ServicoRepository servicoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ContratacaoResponse contratar(ContratarServicoRequest request, String emailContratante) {
        Servico servico = servicoRepository.findById(request.servicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Servico nao encontrado"));

        Usuario contratante = usuarioRepository.findByEmail(emailContratante)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        if (servico.getPrestador().getId().equals(contratante.getId())) {
            throw new RegraNegocioException("Nao e possivel contratar o proprio servico");
        }

        if (servico.getSituacao() != SituacaoServico.ATIVO) {
            throw new RegraNegocioException("Apenas servicos ativos podem ser contratados");
        }

        Contratacao contratacao = Contratacao.builder()
                .servico(servico)
                .contratante(contratante)
                .situacao(SituacaoContratacao.SOLICITADA)
                .build();

        return ContratacaoResponse.de(contratacaoRepository.save(contratacao));
    }

    @Transactional(readOnly = true)
    public List<ContratacaoResponse> listarMinhas(String emailContratante) {
        return contratacaoRepository.findByContratanteEmail(emailContratante).stream()
                .map(ContratacaoResponse::de)
                .toList();
    }
}
