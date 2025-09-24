# Copilot Instructions - Sistema Financeiro Pessoal

Este projeto é um sistema de gestão financeira pessoal desenvolvido com Spring Boot seguindo princípios SOLID e Clean Code.

## Arquitetura

- **Clean Architecture**: Separação clara de responsabilidades em camadas
- **SOLID Principles**: Aplicação dos 5 princípios para código maintível
- **Domain-Driven Design**: Modelagem focada no domínio do negócio

## Estrutura do Projeto

```
src/main/java/com/financeiro/
├── domain/           # Entidades de domínio
├── application/      # Casos de uso e serviços
├── infrastructure/   # Repositórios e configurações
└── presentation/     # Controllers e DTOs
```

## Funcionalidades Principais

1. Gestão de categorias financeiras
2. Lançamento de receitas e despesas
3. Relatórios mensais
4. Cálculo de saldos
5. Listagem de movimentações

## Tecnologias

- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Docker Compose
- Spring Security
- Bean Validation

## Padrões de Código

- Use injeção de dependência via construtor
- Implemente interfaces para abstrações
- Mantenha métodos pequenos e focados
- Use nomes descritivos para classes e métodos
- Aplique validações nos DTOs
- Trate exceções de forma adequada
