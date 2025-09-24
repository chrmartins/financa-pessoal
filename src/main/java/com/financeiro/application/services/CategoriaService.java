package com.financeiro.application.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.application.repositories.CategoriaRepository;
import com.financeiro.domain.entities.Categoria;

import lombok.RequiredArgsConstructor;

/**
 * Serviço de aplicação para gerenciamento de categorias.
 * Implementa os casos de uso relacionados às categorias financeiras.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    /**
     * Caso de uso: Criar nova categoria
     */
    public Categoria criarCategoria(String nome, String descricao, Categoria.TipoCategoria tipo) {
        if (categoriaRepository.existsByNome(nome)) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        Categoria categoria = Categoria.builder()
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .build();

        return categoriaRepository.save(categoria);
    }

    /**
     * Caso de uso: Buscar categoria por ID
     */
    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(UUID id) {
        return categoriaRepository.findById(id);
    }

    /**
     * Caso de uso: Listar todas as categorias ativas
     */
    @Transactional(readOnly = true)
    public List<Categoria> listarCategoriasAtivas() {
        return categoriaRepository.findAllAtivas();
    }

    /**
     * Caso de uso: Listar categorias por tipo
     */
    @Transactional(readOnly = true)
    public List<Categoria> listarPorTipo(Categoria.TipoCategoria tipo) {
        return categoriaRepository.findByTipo(tipo);
    }

    /**
     * Caso de uso: Atualizar categoria
     */
    public Categoria atualizarCategoria(UUID id, String nome, String descricao) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        // Verificar se o nome já existe (exceto para a própria categoria)
        if (!categoria.getNome().equals(nome) && categoriaRepository.existsByNome(nome)) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        categoria.setNome(nome);
        categoria.setDescricao(descricao);

        return categoriaRepository.save(categoria);
    }

    /**
     * Caso de uso: Desativar categoria
     */
    public void desativarCategoria(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        categoria.desativar();
        categoriaRepository.save(categoria);
    }

    /**
     * Caso de uso: Ativar categoria
     */
    public void ativarCategoria(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        categoria.ativar();
        categoriaRepository.save(categoria);
    }

    /**
     * Caso de uso: Buscar categorias por nome
     */
    @Transactional(readOnly = true)
    public List<Categoria> buscarPorNome(String nome) {
        return categoriaRepository.findByNomeContaining(nome);
    }
}