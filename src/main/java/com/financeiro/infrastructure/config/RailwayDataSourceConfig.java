package com.financeiro.infrastructure.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Ajusta a configuração do DataSource quando a aplicação estiver sendo executada
 * em ambientes que expõem a variável {@code DATABASE_URL}, como o Railway.
 *
 * Essa classe converte automaticamente a URL no formato
 * {@code postgresql://usuario:senha@host:porta/banco?parametros}
 * para as propriedades aceitas pelo Spring ({@code spring.datasource.url},
 * {@code spring.datasource.username} e {@code spring.datasource.password}).
 */
@Configuration
public class RailwayDataSourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RailwayDataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties, Environment environment) {
        applyRailwayDatabaseConfiguration(properties, environment);
        return properties.initializeDataSourceBuilder().build();
    }

    private void applyRailwayDatabaseConfiguration(DataSourceProperties properties, Environment environment) {
        if (StringUtils.hasText(properties.getUrl())) {
            return;
        }

        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (!StringUtils.hasText(databaseUrl)) {
            return;
        }

        try {
            URI uri = new URI(databaseUrl);

            if (uri.getUserInfo() != null && uri.getUserInfo().contains(":")) {
                String[] credentials = uri.getUserInfo().split(":", 2);

                if (!StringUtils.hasText(properties.getUsername())) {
                    properties.setUsername(URLDecoder.decode(credentials[0], StandardCharsets.UTF_8));
                }
                if (!StringUtils.hasText(properties.getPassword())) {
                    String decodedPassword = credentials.length > 1
                            ? URLDecoder.decode(credentials[1], StandardCharsets.UTF_8)
                            : "";
                    properties.setPassword(decodedPassword);
                }
            }

            StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://");
            jdbcUrl.append(uri.getHost());

            int port = uri.getPort();
            if (port > 0) {
                jdbcUrl.append(":").append(port);
            }

            jdbcUrl.append(uri.getPath());

            if (StringUtils.hasText(uri.getQuery())) {
                jdbcUrl.append("?").append(uri.getQuery());
            }

            properties.setUrl(jdbcUrl.toString());
            LOGGER.info("DATABASE_URL detectado. Utilizando host {}:{}", uri.getHost(), port > 0 ? port : 5432);
            if (!StringUtils.hasText(properties.getDriverClassName())) {
                properties.setDriverClassName("org.postgresql.Driver");
            }
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Formato inválido para DATABASE_URL", ex);
        }
    }
}
