package com.financeiro.application.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.financeiro.domain.entities.Categoria;

/**
 * Interface de repositório para a entidade Categoria.
 * Define operações de acesso a dados seguindo os princípios da Clean Architecture.
 */
public interface CategoriaRepository {

    /**
     * Busca uma categoria por ID
     */
    Optional<Categoria> findById(UUID id);

    /**
     * Busca todas as categorias ativas
     */
    List<Categoria> findAllAtivas();

    /**
     * Busca categorias por tipo (RECEITA ou DESPESA)
     */
    List<Categoria> findByTipo(Categoria.TipoCategoria tipo);

    /**
     * Busca categorias por nome (busca case-insensitive)
     */
    List<Categoria> findByNomeContaining(String nome);

    /**
     * Salva uma categoria
     */
    Categoria save(Categoria categoria);

    /**
     * Remove uma categoria por ID
     */
    void deleteById(UUID id);

    /**
     * Verifica se existe categoria com o nome especificado
     */
    boolean existsByNome(String nome);

    /**
     * Busca todas as categorias (ativas e inativas)
     */
    List<Categoria> findAll();
}