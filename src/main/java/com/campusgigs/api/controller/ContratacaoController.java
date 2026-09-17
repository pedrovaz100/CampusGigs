package com.campusgigs.api.controller;

import com.campusgigs.api.dto.ContratacaoResponse;
import com.campusgigs.api.dto.ContratarServicoRequest;
import com.campusgigs.api.service.ContratacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contratacoes")
@RequiredArgsConstructor
public class ContratacaoController {

    private final ContratacaoService contratacaoService;

    @PostMapping
    public ResponseEntity<ContratacaoResponse> contratar(@Valid @RequestBody ContratarServicoRequest request,
                                                            Authentication authentication) {
        ContratacaoResponse response = contratacaoService.contratar(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<ContratacaoResponse>> listarMinhas(Authentication authentication) {
        return ResponseEntity.ok(contratacaoService.listarMinhas(authentication.getName()));
    }
}
