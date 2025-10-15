package com.financeiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.financeiro.infrastructure.config.AppProperties;
import com.financeiro.infrastructure.config.SecurityProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({AppProperties.class, SecurityProperties.class})
public class FinancasPessoalApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancasPessoalApplication.class, args);
    }

}