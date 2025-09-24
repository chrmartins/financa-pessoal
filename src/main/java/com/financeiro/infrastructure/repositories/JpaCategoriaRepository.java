package com.financeiro.infrastructure.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.financeiro.domain.entities.Categoria;

/**
 * Repositório JPA para a entidade Categoria.
 * Extends JpaRepository para operações básicas de CRUD.
 */
@Repository
public interface JpaCategoriaRepository extends JpaRepository<Categoria, UUID> {

    /**
     * Busca todas as categorias ativas
     */
    List<Categoria> findByAtivaTrue();

    /**
     * Busca categorias por tipo (RECEITA ou DESPESA)
     */
    List<Categoria> findByTipo(Categoria.TipoCategoria tipo);

    /**
     * Busca categorias por nome (busca case-insensitive)
     */
    List<Categoria> findByNomeContainingIgnoreCase(String nome);

    /**
     * Verifica se existe categoria com o nome especificado
     */
    boolean existsByNome(String nome);

    /**
     * Busca categorias ativas por tipo
     */
    List<Categoria> findByTipoAndAtivaTrue(Categoria.TipoCategoria tipo);

    /**
     * Busca categorias por nome exato
     */
    @Query("SELECT c FROM Categoria c WHERE LOWER(c.nome) = LOWER(:nome)")
    List<Categoria> findByNomeIgnoreCase(@Param("nome") String nome);

    /**
     * Conta o número de categorias ativas por tipo
     */
    long countByTipoAndAtivaTrue(Categoria.TipoCategoria tipo);
}