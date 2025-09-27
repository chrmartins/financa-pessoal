package com.financeiro.infrastructure.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private final String databaseUrlEnv;
    private final String databaseJdbcUrlEnv;
    private final String databaseUsernameEnv;
    private final String databasePasswordEnv;

    public RailwayDataSourceConfig(
            DataSourceProperties properties,
            Environment environment,
            @Value("${DATABASE_URL:}") String databaseUrlEnv,
            @Value("${DATABASE_JDBC_URL:}") String databaseJdbcUrlEnv,
            @Value("${DATABASE_USERNAME:}") String databaseUsernameEnv,
            @Value("${DATABASE_PASSWORD:}") String databasePasswordEnv) {
        this.properties = properties;
        this.environment = environment;
        this.databaseUrlEnv = databaseUrlEnv;
        this.databaseJdbcUrlEnv = databaseJdbcUrlEnv;
        this.databaseUsernameEnv = databaseUsernameEnv;
        this.databasePasswordEnv = databasePasswordEnv;
    }

    @PostConstruct
    void configureDataSourceProperties() {
        if (log.isInfoEnabled()) {
            log.info("Variáveis de ambiente detectadas: DATABASE_URL='{}', DATABASE_JDBC_URL='{}'", maskSensitive(databaseUrlEnv), maskSensitive(databaseJdbcUrlEnv));
        }
        String resolvedJdbcUrl = resolveJdbcUrl();
        if (StringUtils.hasText(resolvedJdbcUrl)) {
            properties.setUrl(resolvedJdbcUrl);
            if (log.isInfoEnabled()) {
                log.info("Datasource JDBC URL resolvido para deploy: {}", maskSensitive(resolvedJdbcUrl));
            }
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
        String jdbcEnv = normalize(databaseJdbcUrlEnv);
        if (StringUtils.hasText(jdbcEnv)) {
            return jdbcEnv;
        }

        String databaseUrl = resolveDatabaseUrlCandidate();
        if (!StringUtils.hasText(databaseUrl)) {
            String fallback = resolvePlaceholder(properties.getUrl());
            if (StringUtils.hasText(fallback)) {
                return fallback;
            }
            log.warn("Nenhuma URL de banco encontrada nas variáveis de ambiente ou propriedades. Valor atual: {}", properties.getUrl());
            return null;
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
            }
        }

        return databaseUrl;
    }

    private UsernamePassword resolveCredentials() {
        String username = normalize(databaseUsernameEnv);
        String password = normalize(databasePasswordEnv);

        if (StringUtils.hasText(username) && password != null) {
            return new UsernamePassword(username, password);
        }

        try {
            String databaseUrl = resolveDatabaseUrlCandidate();
            if (!StringUtils.hasText(databaseUrl)) {
                return new UsernamePassword(resolvePlaceholder(properties.getUsername()), resolvePlaceholder(properties.getPassword()));
            }
            String credentialUrl = databaseUrl;
            if (credentialUrl.startsWith("jdbc:postgresql://")) {
                credentialUrl = credentialUrl.replaceFirst("jdbc:postgresql://", "postgresql://");
            }
            if (credentialUrl.startsWith("postgres://")) {
                credentialUrl = credentialUrl.replaceFirst("postgres://", "postgresql://");
            }
            if (!credentialUrl.startsWith("postgresql://")) {
                return new UsernamePassword(resolvePlaceholder(properties.getUsername()), resolvePlaceholder(properties.getPassword()));
            }

            URI uri = new URI(credentialUrl);
            String userInfo = uri.getUserInfo();
            if (!StringUtils.hasText(userInfo)) {
                return new UsernamePassword(resolvePlaceholder(properties.getUsername()), resolvePlaceholder(properties.getPassword()));
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
            return new UsernamePassword(resolvePlaceholder(properties.getUsername()), resolvePlaceholder(properties.getPassword()));
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

    private String resolveDatabaseUrlCandidate() {
        String fromEnv = normalize(databaseUrlEnv);
        if (StringUtils.hasText(fromEnv)) {
            if (log.isInfoEnabled()) {
                log.info("Utilizando DATABASE_URL da origem ENV: {}", maskSensitive(fromEnv));
            }
            return fromEnv;
        }

        String fromProperties = normalize(resolvePlaceholder(properties.getUrl()));
        if (StringUtils.hasText(fromProperties)) {
            if (log.isInfoEnabled()) {
                log.info("Utilizando URL das propriedades após placeholder: {}", maskSensitive(fromProperties));
            }
            return fromProperties;
        }

        return null;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || trimmed.contains("${")) {
            return null;
        }
        return trimmed;
    }

    private String resolvePlaceholder(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String resolved = environment.resolvePlaceholders(value);
        if (!StringUtils.hasText(resolved) || resolved.contains("${")) {
            return null;
        }
        return resolved;
    }

    private String maskSensitive(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        return value.replaceAll("://([^:@/]+):([^@/]+)@", "://****:****@");
    }

    private record UsernamePassword(String username, String password) {}
}
