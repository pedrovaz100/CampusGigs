package com.campusgigs.api.repository;

import com.campusgigs.api.entity.Contratacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratacaoRepository extends JpaRepository<Contratacao, Long> {

    List<Contratacao> findByContratanteEmail(String email);
}
