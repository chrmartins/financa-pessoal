package com.financeiro.infrastructure.config;

// import java.sql.Connection;
// import javax.sql.DataSource;
// import org.springframework.boot.actuator.health.Health;
// import org.springframework.boot.actuator.health.HealthIndicator;
// import org.springframework.stereotype.Component;
// import lombok.RequiredArgsConstructor;

/**
 * Health check customizado para verificar conectividade com banco de dados
 * Temporariamente desabilitado devido a problema com dependências do Spring Boot Actuator
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