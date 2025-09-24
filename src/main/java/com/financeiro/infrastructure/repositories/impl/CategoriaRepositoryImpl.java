package com.financeiro.infrastructure.repositories.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financeiro.application.repositories.CategoriaRepository;
import com.financeiro.domain.entities.Categoria;
import com.financeiro.infrastructure.repositories.JpaCategoriaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementação do repositório de Categoria.
 * Adapta as operações JPA para as interfaces da camada de aplicação.
 */
@Component
@RequiredArgsConstructor
public class CategoriaRepositoryImpl implements CategoriaRepository {

    private final JpaCategoriaRepository jpaCategoriaRepository;

    @Override
    public Optional<Categoria> findById(UUID id) {
        return jpaCategoriaRepository.findById(id);
    }

    @Override
    public List<Categoria> findAllAtivas() {
        return jpaCategoriaRepository.findByAtivaTrue();
    }

    @Override
    public List<Categoria> findByTipo(Categoria.TipoCategoria tipo) {
        return jpaCategoriaRepository.findByTipoAndAtivaTrue(tipo);
    }

    @Override
    public List<Categoria> findByNomeContaining(String nome) {
        return jpaCategoriaRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Override
    public Categoria save(Categoria categoria) {
        return jpaCategoriaRepository.save(categoria);
    }

    @Override
    public void deleteById(UUID id) {
        jpaCategoriaRepository.deleteById(id);
    }

    @Override
    public boolean existsByNome(String nome) {
        return jpaCategoriaRepository.existsByNome(nome);
    }

    @Override
    public List<Categoria> findAll() {
        return jpaCategoriaRepository.findAll();
    }
}