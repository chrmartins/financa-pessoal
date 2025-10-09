package com.financeiro.presentation.dto.categoria;

import com.financeiro.domain.entities.Categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de nova categoria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoriaRequest {

    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    private String nome;

    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    private String descricao;

    @NotNull(message = "Tipo da categoria é obrigatório")
    private Categoria.TipoCategoria tipo;

    @Size(max = 7, message = "Cor deve ter no máximo 7 caracteres (formato: #RRGGBB)")
    private String cor;
}