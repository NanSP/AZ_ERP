package com.example.backend.rh.dependentes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DependentesRepository extends JpaRepository<Dependentes, Integer> {
    boolean existsByColaboradorId(Integer colaboradorId);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Integer id);
    boolean existsByColaboradorIdAndNomeIgnoreCaseAndDataNascimento(Integer colaboradorId, String nome, LocalDate dataNascimento);
    boolean existsByColaboradorIdAndNomeIgnoreCaseAndDataNascimentoAndIdNot(Integer colaboradorId, String nome, LocalDate dataNascimento, Integer id);
}
