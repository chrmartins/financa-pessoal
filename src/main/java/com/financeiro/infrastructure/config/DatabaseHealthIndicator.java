package com.financeiro.infrastructure.config;

/**
 * Health check customizado para verificar conectividade com banco de dados
 * Temporariamente desabilitado para resolver problemas de dependência
 */
// @Component("database")
// @RequiredArgsConstructor
public class DatabaseHealthIndicator { // implements HealthIndicator {

    // private final DataSource dataSource;

    // @Override
    // public Health health() {
    //     try (Connection connection = dataSource.getConnection()) {
    //         if (connection.isValid(1)) {
    //             return Health.up()
    //                 .withDetail("database", "PostgreSQL")
    //                 .withDetail("status", "Connected")
    //                 .build();
    //         }
    //     } catch (Exception ex) {
    //         return Health.down()
    //             .withDetail("database", "PostgreSQL")
    //             .withDetail("error", ex.getMessage())
    //             .build();
    //     }
    //     
    //     return Health.down()
    //         .withDetail("database", "PostgreSQL")
    //         .withDetail("error", "Connection invalid")
    //         .build();
    // }
}