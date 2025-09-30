package com.financeiro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Categoria.TipoCategoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    
    List<Categoria> findByAtivaTrue();
    
    List<Categoria> findByAtivaTrueAndTipo(TipoCategoria tipo);
    
    @Query("SELECT c FROM Categoria c WHERE c.ativa = true AND c.tipo = :tipo ORDER BY c.nome")
    List<Categoria> findByTipoAndAtivaOrderByNome(@Param("tipo") TipoCategoria tipo);
    
    boolean existsByNomeIgnoreCase(String nome);
    
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, UUID id);
}