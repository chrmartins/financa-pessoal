package com.financeiro.application.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Categoria.TipoCategoria;
import com.financeiro.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional
    public Categoria criarCategoria(String nome, String descricao, TipoCategoria tipo) {
        if (categoriaRepository.existsByNomeIgnoreCase(nome)) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        Categoria categoria = Categoria.builder()
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .build();

        return categoriaRepository.save(categoria);
    }

    public Optional<Categoria> buscarPorId(UUID id) {
        return categoriaRepository.findById(id);
    }

    public List<Categoria> listarAtivas() {
        return categoriaRepository.findByAtivaTrue();
    }

    public List<Categoria> listarPorTipo(TipoCategoria tipo) {
        return categoriaRepository.findByTipoAndAtivaOrderByNome(tipo);
    }

    @Transactional
    public Categoria atualizarCategoria(UUID id, String nome, String descricao) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        if (!categoria.getNome().equalsIgnoreCase(nome) && 
            categoriaRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        categoria.setNome(nome);
        categoria.setDescricao(descricao);
        // Tipo não é alterado na atualização

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void ativarCategoria(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        
        categoria.ativar();
        categoriaRepository.save(categoria);
    }

    @Transactional
    public void desativarCategoria(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        
        categoria.desativar();
        categoriaRepository.save(categoria);
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }
}