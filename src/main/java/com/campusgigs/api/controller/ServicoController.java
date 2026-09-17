package com.campusgigs.api.controller;

import com.campusgigs.api.dto.AtualizarServicoRequest;
import com.campusgigs.api.dto.CriarServicoRequest;
import com.campusgigs.api.dto.ServicoResponse;
import com.campusgigs.api.service.ServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @PostMapping
    public ResponseEntity<ServicoResponse> publicar(@Valid @RequestBody CriarServicoRequest request,
                                                      Authentication authentication) {
        ServicoResponse response = servicoService.publicar(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ServicoResponse>> listar() {
        return ResponseEntity.ok(servicoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponse> editar(@PathVariable Long id,
                                                    @Valid @RequestBody AtualizarServicoRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(servicoService.editar(id, request, authentication.getName()));
    }

    @PostMapping("/{id}/encerrar")
    public ResponseEntity<ServicoResponse> encerrar(@PathVariable Long id, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(servicoService.encerrar(id, authentication.getName(), admin));
    }
}
