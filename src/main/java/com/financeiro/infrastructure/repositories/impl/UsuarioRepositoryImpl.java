package com.financeiro.infrastructure.repositories.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financeiro.application.repositories.UsuarioRepository;
import com.financeiro.domain.entities.Usuario;
import com.financeiro.infrastructure.repositories.JpaUsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementação do repositório de Usuario.
 * Adapta as operações JPA para as interfaces da camada de aplicação.
 */
@Component
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final JpaUsuarioRepository jpaUsuarioRepository;

    @Override
    public Optional<Usuario> findById(UUID id) {
        return jpaUsuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaUsuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return jpaUsuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(UUID id) {
        jpaUsuarioRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUsuarioRepository.existsByEmail(email);
    }

    @Override
    public Optional<Usuario> findByEmailAndAtivoTrue(String email) {
        return jpaUsuarioRepository.findByEmailAndAtivoTrue(email);
    }
}