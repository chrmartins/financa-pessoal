package com.financeiro.domain.enums;

import java.time.LocalDate;

/**
 * Frequências disponíveis para transações fixas recorrentes
 */
public enum Frequencia {
    DIARIO("Diário", 1, "DAYS"),
    SEMANAL("Semanal", 7, "DAYS"),
    QUINZENAL("Quinzenal", 15, "DAYS"),
    MENSAL("Mensal", 1, "MONTHS"),
    SEMESTRAL("Semestral", 6, "MONTHS"),
    ANUAL("Anual", 1, "YEARS");

    private final String descricao;
    private final int valor;
    private final String unidade;

    Frequencia(String descricao, int valor, String unidade) {
        this.descricao = descricao;
        this.valor = valor;
        this.unidade = unidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getValor() {
        return valor;
    }

    public String getUnidade() {
        return unidade;
    }

    /**
     * Calcula a próxima data baseada na frequência
     * 
     * @param dataAtual Data de referência
     * @return Próxima data calculada
     */
    public LocalDate calcularProximaData(LocalDate dataAtual) {
        return switch (this.unidade) {
            case "DAYS" -> dataAtual.plusDays(this.valor);
            case "MONTHS" -> dataAtual.plusMonths(this.valor);
            case "YEARS" -> dataAtual.plusYears(this.valor);
            default -> throw new IllegalStateException("Unidade inválida: " + this.unidade);
        };
    }
}
