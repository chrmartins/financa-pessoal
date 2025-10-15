package com.financeiro.domain.enums;

/**
 * Tipos de recorrência para transações
 */
public enum TipoRecorrencia {
    /**
     * Transação única, não recorrente
     */
    NAO_RECORRENTE("Não Recorrente"),
    
    /**
     * Transação parcelada (quantidade fixa de parcelas)
     * Todas as parcelas são criadas imediatamente
     */
    PARCELADA("Parcelada"),
    
    /**
     * Transação fixa recorrente (sem fim definido)
     * Novas transações são criadas automaticamente pelo JOB
     */
    FIXA("Fixa");

    private final String descricao;

    TipoRecorrencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
