package com.financeiro.presentation.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financeiro.application.services.CategoriaService;
import com.financeiro.domain.entities.Categoria;
import com.financeiro.presentation.dto.categoria.CategoriaResponse;
import com.financeiro.presentation.dto.categoria.CreateCategoriaRequest;
import com.financeiro.presentation.dto.categoria.UpdateCategoriaRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para gerenciamento de categorias financeiras.
 * Expõe endpoints para CRUD de categorias.
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    /**
     * Criar nova categoria
     */
    @PostMapping
    public ResponseEntity<CategoriaResponse> criarCategoria(@Valid @RequestBody CreateCategoriaRequest request) {
        Categoria categoria = categoriaService.criarCategoria(
                request.getNome(),
                request.getDescricao(),
                request.getTipo()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoriaResponse.fromEntity(categoria));
    }

    /**
     * Buscar categoria por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarCategoria(@PathVariable UUID id) {
        return categoriaService.buscarPorId(id)
                .map(categoria -> ResponseEntity.ok(CategoriaResponse.fromEntity(categoria)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Listar todas as categorias ativas
     */
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        List<Categoria> categorias = categoriaService.listarCategoriasAtivas();
        List<CategoriaResponse> response = categorias.stream()
                .map(CategoriaResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Listar categorias por tipo
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<CategoriaResponse>> listarPorTipo(@PathVariable Categoria.TipoCategoria tipo) {
        List<Categoria> categorias = categoriaService.listarPorTipo(tipo);
        List<CategoriaResponse> response = categorias.stream()
                .map(CategoriaResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar categorias por nome
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<CategoriaResponse>> buscarPorNome(@RequestParam String nome) {
        List<Categoria> categorias = categoriaService.buscarPorNome(nome);
        List<CategoriaResponse> response = categorias.stream()
                .map(CategoriaResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Atualizar categoria
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizarCategoria(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoriaRequest request) {
        
        try {
            Categoria categoria = categoriaService.atualizarCategoria(
                    id,
                    request.getNome(),
                    request.getDescricao()
            );
            return ResponseEntity.ok(CategoriaResponse.fromEntity(categoria));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Desativar categoria
     */
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarCategoria(@PathVariable UUID id) {
        try {
            categoriaService.desativarCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Ativar categoria
     */
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativarCategoria(@PathVariable UUID id) {
        try {
            categoriaService.ativarCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}