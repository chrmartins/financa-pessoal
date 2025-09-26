package com.financeiro.infrastructure.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class RailwayDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(RailwayDataSourceConfig.class);

    private final DataSourceProperties properties;
    private final Environment environment;

    public RailwayDataSourceConfig(DataSourceProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @PostConstruct
    void configureDataSourceProperties() {
        String resolvedJdbcUrl = resolveJdbcUrl();
        if (StringUtils.hasText(resolvedJdbcUrl)) {
            properties.setUrl(resolvedJdbcUrl);
        }

        UsernamePassword credentials = resolveCredentials();

        if (StringUtils.hasText(credentials.username())) {
            properties.setUsername(credentials.username());
        }

        if (credentials.password() != null) {
            properties.setPassword(credentials.password());
        }
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    private String resolveJdbcUrl() {
        String jdbcEnv = environment.getProperty("DATABASE_JDBC_URL");
        if (StringUtils.hasText(jdbcEnv)) {
            return jdbcEnv;
        }

        String databaseUrlRaw = environment.getProperty("DATABASE_URL");
        if (databaseUrlRaw == null) {
            return properties.getUrl();
        }

        String databaseUrl = databaseUrlRaw.trim();
        if (!StringUtils.hasText(databaseUrl)) {
            return properties.getUrl();
        }

        if (databaseUrl.startsWith("jdbc:")) {
            return databaseUrl;
        }

        if (databaseUrl.startsWith("postgres://")) {
            databaseUrl = databaseUrl.replaceFirst("postgres://", "postgresql://");
        }

        if (databaseUrl.startsWith("postgresql://")) {
            try {
                return toJdbcPostgresUrl(databaseUrl);
            } catch (URISyntaxException ex) {
                log.warn("Não foi possível converter DATABASE_URL para JDBC. Usando valor original: {}", ex.getMessage());
                return properties.getUrl();
            }
        }

        return properties.getUrl();
    }

    private UsernamePassword resolveCredentials() {
        String username = environment.getProperty("DATABASE_USERNAME");
        String password = environment.getProperty("DATABASE_PASSWORD");

        if (StringUtils.hasText(username) && password != null) {
            return new UsernamePassword(username, password);
        }

        String databaseUrlRaw = environment.getProperty("DATABASE_URL");
        if (databaseUrlRaw == null) {
            return new UsernamePassword(properties.getUsername(), properties.getPassword());
        }

        try {
            String databaseUrl = databaseUrlRaw.trim();
            if (!StringUtils.hasText(databaseUrl)) {
                return new UsernamePassword(properties.getUsername(), properties.getPassword());
            }
            URI uri = new URI(databaseUrl.replaceFirst("postgres://", "postgresql://"));
            String userInfo = uri.getUserInfo();
            if (!StringUtils.hasText(userInfo)) {
                return new UsernamePassword(properties.getUsername(), properties.getPassword());
            }

            String[] parts = userInfo.split(":", 2);
            String resolvedUser = parts.length > 0 ? parts[0] : null;
            String resolvedPassword = parts.length > 1 ? parts[1] : null;

            String finalUsername = Optional.ofNullable(username)
                .filter(StringUtils::hasText)
                .orElse(resolvedUser);

            String finalPassword = password != null ? password : resolvedPassword;

            return new UsernamePassword(finalUsername, finalPassword);
        } catch (URISyntaxException ex) {
            log.warn("Não foi possível extrair credenciais de DATABASE_URL: {}", ex.getMessage());
            return new UsernamePassword(properties.getUsername(), properties.getPassword());
        }
    }

    private String toJdbcPostgresUrl(String databaseUrl) throws URISyntaxException {
        URI uri = new URI(databaseUrl);
        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String path = uri.getPath();
        String query = uri.getQuery();

        StringBuilder builder = new StringBuilder("jdbc:postgresql://")
            .append(host)
            .append(":")
            .append(port)
            .append(path != null ? path : "");

        if (StringUtils.hasText(query)) {
            builder.append("?").append(query);
        }

        return builder.toString();
    }

    private record UsernamePassword(String username, String password) {}
}
