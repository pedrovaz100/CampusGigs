package com.campusgigs.api.service;

import com.campusgigs.api.dto.AtualizarServicoRequest;
import com.campusgigs.api.dto.CriarServicoRequest;
import com.campusgigs.api.dto.ServicoResponse;
import com.campusgigs.api.entity.Servico;
import com.campusgigs.api.entity.Usuario;
import com.campusgigs.api.entity.enums.SituacaoServico;
import com.campusgigs.api.exception.AcessoNegadoException;
import com.campusgigs.api.exception.RecursoNaoEncontradoException;
import com.campusgigs.api.exception.RegraNegocioException;
import com.campusgigs.api.repository.ServicoRepository;
import com.campusgigs.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ServicoResponse publicar(CriarServicoRequest request, String emailPrestador) {
        Usuario prestador = buscarUsuario(emailPrestador);

        Servico servico = Servico.builder()
                .prestador(prestador)
                .titulo(request.titulo())
                .descricao(request.descricao())
                .categoria(request.categoria())
                .preco(request.preco())
                .situacao(SituacaoServico.ATIVO)
                .build();

        return ServicoResponse.de(servicoRepository.save(servico));
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> listar() {
        return servicoRepository.findAll().stream()
                .map(ServicoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServicoResponse buscarPorId(Long id) {
        return ServicoResponse.de(buscarServico(id));
    }

    @Transactional
    public ServicoResponse editar(Long id, AtualizarServicoRequest request, String emailAutenticado) {
        Servico servico = buscarServico(id);

        if (!servico.getPrestador().getEmail().equalsIgnoreCase(emailAutenticado)) {
            throw new AcessoNegadoException("Apenas o prestador responsavel pode editar este servico");
        }

        if (request.situacao() == SituacaoServico.ENCERRADO) {
            throw new RegraNegocioException("Use o endpoint de encerramento para encerrar um servico");
        }

        servico.setTitulo(request.titulo());
        servico.setDescricao(request.descricao());
        servico.setCategoria(request.categoria());
        servico.setPreco(request.preco());
        servico.setSituacao(request.situacao());

        return ServicoResponse.de(servico);
    }

    @Transactional
    public ServicoResponse encerrar(Long id, String emailAutenticado, boolean admin) {
        Servico servico = buscarServico(id);

        boolean donoDoServico = servico.getPrestador().getEmail().equalsIgnoreCase(emailAutenticado);
        if (!donoDoServico && !admin) {
            throw new AcessoNegadoException("Apenas o prestador responsavel ou um administrador podem encerrar este servico");
        }

        servico.setSituacao(SituacaoServico.ENCERRADO);
        return ServicoResponse.de(servico);
    }

    private Servico buscarServico(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Servico nao encontrado"));
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }
}
