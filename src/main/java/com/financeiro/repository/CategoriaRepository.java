package com.financeiro.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Categoria.TipoCategoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    @Query("SELECT c FROM Categoria c WHERE c.usuario.id = :usuarioId AND c.ativa = true ORDER BY c.nome")
    List<Categoria> findAtivasByUsuario(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT c FROM Categoria c WHERE c.usuario.id = :usuarioId AND c.ativa = true AND c.tipo = :tipo ORDER BY c.nome")
    List<Categoria> findAtivasByUsuarioAndTipo(@Param("usuarioId") UUID usuarioId, @Param("tipo") TipoCategoria tipo);

    Optional<Categoria> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    boolean existsByNomeIgnoreCaseAndUsuarioId(String nome, UUID usuarioId);

    boolean existsByNomeIgnoreCaseAndUsuarioIdAndIdNot(String nome, UUID usuarioId, UUID id);
}