package com.financeiro.application.repositories;

import java.util.Optional;
import java.util.UUID;

import com.financeiro.domain.entities.Usuario;

/**
 * Interface de repositório para a entidade Usuario.
 * Define operações de acesso a dados seguindo os princípios da Clean Architecture.
 */
public interface UsuarioRepository {

    /**
     * Busca um usuário por ID
     */
    Optional<Usuario> findById(UUID id);

    /**
     * Busca um usuário por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Salva um usuário
     */
    Usuario save(Usuario usuario);

    /**
     * Remove um usuário por ID
     */
    void deleteById(UUID id);

    /**
     * Verifica se existe usuário com o email especificado
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários ativos
     */
    Optional<Usuario> findByEmailAndAtivoTrue(String email);
}