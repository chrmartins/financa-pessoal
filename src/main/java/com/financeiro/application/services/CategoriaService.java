package com.financeiro.application.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Categoria.TipoCategoria;
import com.financeiro.domain.entities.Usuario;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    private Usuario obterUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado"));
    }

    @Transactional
    public Categoria criarCategoria(String nome, String descricao, TipoCategoria tipo, String emailUsuario) {
        Usuario usuario = obterUsuario(emailUsuario);

        if (categoriaRepository.existsByNomeIgnoreCaseAndUsuarioId(nome, usuario.getId())) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        Categoria categoria = Categoria.builder()
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .usuario(usuario)
                .build();

        return categoriaRepository.save(categoria);
    }

    public Optional<Categoria> buscarPorId(UUID id, String emailUsuario) {
        Usuario usuario = obterUsuario(emailUsuario);
        return categoriaRepository.findByIdAndUsuarioId(id, usuario.getId());
    }

    public List<Categoria> listarAtivas(String emailUsuario) {
        Usuario usuario = obterUsuario(emailUsuario);
        return categoriaRepository.findAtivasByUsuario(usuario.getId());
    }

    public List<Categoria> listarPorTipo(TipoCategoria tipo, String emailUsuario) {
        Usuario usuario = obterUsuario(emailUsuario);
        return categoriaRepository.findAtivasByUsuarioAndTipo(usuario.getId(), tipo);
    }

    @Transactional
    public Categoria atualizarCategoria(UUID id, String nome, String descricao, String emailUsuario) {
        Usuario usuario = obterUsuario(emailUsuario);

        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        if (!categoria.getNome().equalsIgnoreCase(nome) &&
            categoriaRepository.existsByNomeIgnoreCaseAndUsuarioIdAndIdNot(nome, usuario.getId(), id)) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        categoria.setNome(nome);
        categoria.setDescricao(descricao);
        // Tipo não é alterado na atualização

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void ativarCategoria(UUID id, String emailUsuario) {
        Usuario usuario = obterUsuario(emailUsuario);
        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        
        categoria.ativar();
        categoriaRepository.save(categoria);
    }

    @Transactional
    public void desativarCategoria(UUID id, String emailUsuario) {
        Usuario usuario = obterUsuario(emailUsuario);
        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        
        categoria.desativar();
        categoriaRepository.save(categoria);
    }

    public List<Categoria> listarTodas(String emailUsuario) {
        return listarAtivas(emailUsuario);
    }
}