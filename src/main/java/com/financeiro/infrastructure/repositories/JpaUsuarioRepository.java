package com.financeiro.infrastructure.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeiro.domain.entities.Usuario;

/**
 * Repositório JPA para a entidade Usuario.
 * Extends JpaRepository para operações básicas de CRUD.
 */
@Repository
public interface JpaUsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Busca um usuário por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se existe usuário com o email especificado
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuário ativo por email
     */
    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    /**
     * Busca todos os usuários ativos
     */
    List<Usuario> findByAtivoTrue();

    /**
     * Busca usuários por papel
     */
    List<Usuario> findByPapel(Usuario.Papel papel);

    /**
     * Busca usuários ativos por papel
     */
    List<Usuario> findByPapelAndAtivoTrue(Usuario.Papel papel);

    /**
     * Conta usuários ativos
     */
    long countByAtivoTrue();
}