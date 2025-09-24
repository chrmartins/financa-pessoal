package com.financeiro.presentation.dto.transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de transação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransacaoRequest {

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 2, max = 100, message = "Descrição deve ter entre 2 e 100 caracteres")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Data da transação é obrigatória")
    private LocalDate dataTransacao;

    @NotNull(message = "Categoria é obrigatória")
    private UUID categoriaId;

    @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    private String observacoes;
}