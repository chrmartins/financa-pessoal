# 🔐 Implementação do Spring Security - Explicação Didática

Este documento explica passo a passo como o Spring Security foi implementado no projeto Sistema Financeiro Pessoal.

---

## 📚 Passo 1: Entender o Fluxo de Autenticação

```
Cliente → Login (email/senha) → Backend valida → Gera JWT Token → Cliente usa token em todas requisições
```

---

## 🏗️ Passo 2: Estrutura de Arquivos Criados

```
infrastructure/security/
├── JwtService.java                    # Gera e valida tokens JWT
├── JwtAuthenticationFilter.java       # Intercepta requisições e valida token
├── CustomUserDetailsService.java      # Busca usuário no banco
└── SecurityConfig.java                # Configuração geral do Spring Security

domain/entities/
└── Usuario.java implements UserDetails  # Entidade que representa o usuário autenticado

presentation/controllers/
└── AuthenticationController.java      # Endpoints de login e refresh token
```

---

## 🔄 Passo 3: Fluxo Completo de Autenticação

### **3.1. Usuário Faz Login**

```java
// AuthenticationController.java
POST /api/auth/login
{
  "email": "usuario@example.com",
  "senha": "senha123"
}
```

### **3.2. Spring Security Valida as Credenciais**

```java
// AuthenticationController.java - linha 56
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(email, senha)
);
```

**O que acontece aqui:**

1. Spring Security chama `CustomUserDetailsService.loadUserByUsername(email)`
2. Busca o usuário no banco: `usuarioRepository.findByEmail(email)`
3. Compara a senha usando BCrypt
4. Se correto → continua; se errado → lança exceção

### **3.3. Gera o Token JWT**

```java
// AuthenticationController.java - linha 62
String token = jwtService.generateToken(usuario);
String refreshToken = jwtService.generateRefreshToken(usuario);
```

**O que acontece:**

- `JwtService` cria um token assinado contendo:
  - Email do usuário (subject)
  - Data de criação (iat)
  - Data de expiração (exp - 24h)
  - Assinatura para garantir integridade

### **3.4. Retorna o Token ao Cliente**

```json
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
  "refreshToken": "eyJhbGciOiJIUzM4NCJ9...",
  "tipo": "Bearer"
}
```

---

## 🛡️ Passo 4: Cliente Usa o Token em Requisições

### **4.1. Cliente Envia Token no Header**

```bash
GET /api/categorias
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

### **4.2. Filter Intercepta a Requisição**

```java
// JwtAuthenticationFilter.java - doFilterInternal()

// 1. Extrai o token do header Authorization
String token = header.substring(7); // Remove "Bearer "

// 2. Extrai o email do token
String userEmail = jwtService.extractUsername(token);

// 3. Busca o usuário no banco
UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

// 4. Valida se o token é válido
if (jwtService.isTokenValid(token, userDetails)) {
    // 5. Cria o contexto de autenticação
    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities() // ROLE_ADMIN ou ROLE_USER
        );

    // 6. Salva no contexto do Spring Security
    SecurityContextHolder.getContext().setAuthentication(authToken);
}

// 7. Passa para o próximo filtro/controller
filterChain.doFilter(request, response);
```

### **4.3. Controller Usa o Usuário Autenticado**

```java
// CategoriaController.java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String email = auth.getName(); // Pega o email do usuário logado
Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

// Agora sabe qual usuário está fazendo a requisição!
```

---

## 🔑 Passo 5: Componentes Principais Explicados

### **5.1. Usuario implements UserDetails**

**Arquivo:** `src/main/java/com/financeiro/domain/entities/Usuario.java`

```java
public class Usuario implements UserDetails {

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + papel.name()));
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return ativo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
```

**Por quê?**

- Spring Security precisa saber como obter username, senha, roles e status da conta
- Permite que a entidade Usuario seja usada diretamente pelo Spring Security
- Evita criar classes separadas para autenticação

---

### **5.2. CustomUserDetailsService**

**Arquivo:** `src/main/java/com/financeiro/infrastructure/security/CustomUserDetailsService.java`

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuário não encontrado com email: " + email
            ));
    }
}
```

**Responsabilidade:**

- Buscar o usuário no banco quando o Spring Security precisa autenticar
- Chamado automaticamente pelo AuthenticationManager durante o login
- Retorna um UserDetails (que é o nosso Usuario)

---

### **5.3. JwtService**

**Arquivo:** `src/main/java/com/financeiro/infrastructure/security/JwtService.java`

```java
@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    // Gera token JWT com email, expiração e assinatura
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS384)
            .compact();
    }

    // Extrai o email do token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Valida se token é válido e não expirou
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
```

**Responsabilidade:**

- Gerenciar toda a lógica de JWT (criar, validar, extrair dados)
- Assinar tokens com chave secreta
- Validar expiração e integridade

**Configurações:**

- `security.jwt.secret-key`: Chave secreta para assinar tokens (64 chars)
- `security.jwt.expiration-time`: Tempo de vida do access token (24h = 86400000 ms)
- `security.jwt.refresh-expiration-time`: Tempo de vida do refresh token (7d = 604800000 ms)

---

### **5.4. JwtAuthenticationFilter**

**Arquivo:** `src/main/java/com/financeiro/infrastructure/security/JwtAuthenticationFilter.java`

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extrai o header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Verifica se é Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extrai o token (remove "Bearer ")
        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);

        // 4. Se tem email e não está autenticado ainda
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Busca o usuário no banco
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // 6. Valida o token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 7. Cria o objeto de autenticação
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                // 8. Adiciona detalhes da requisição
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9. Salva no contexto do Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Passa para o próximo filtro
        filterChain.doFilter(request, response);
    }
}
```

**Responsabilidade:**

- Interceptar TODAS as requisições HTTP
- Validar o token JWT presente no header
- Configurar o contexto de segurança do Spring

**Por que OncePerRequestFilter?**

- Garante que o filtro execute apenas uma vez por requisição
- Evita processamento duplicado

---

### **5.5. SecurityConfig**

**Arquivo:** `src/main/java/com/financeiro/infrastructure/security/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // Desabilita CSRF (não precisa em API REST stateless)
            .csrf(csrf -> csrf.disable())

            // Define política de sessão como STATELESS (sem sessão no servidor)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configura quais endpoints precisam autenticação
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Login público
                .requestMatchers("/actuator/**").permitAll() // Health check
                .anyRequest().authenticated() // Resto precisa autenticação
            )

            // Adiciona nosso filtro JWT ANTES do filtro padrão de autenticação
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // Configura CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

**Responsabilidade:**

- Configurar as regras de segurança da aplicação
- Definir quais endpoints são públicos e quais precisam autenticação
- Adicionar o filtro JWT na cadeia de filtros
- Configurar CORS para permitir frontend acessar a API

**Principais Configurações:**

1. **CSRF Desabilitado**: APIs REST stateless não precisam de proteção CSRF
2. **Sessão STATELESS**: Não mantém sessão no servidor (usa apenas JWT)
3. **Endpoints Públicos**: `/api/auth/**` (login e refresh token)
4. **Filtro JWT**: Executado antes do filtro padrão de autenticação
5. **CORS**: Permite frontend em localhost:3000 acessar a API

---

### **5.6. AuthenticationController**

**Arquivo:** `src/main/java/com/financeiro/presentation/controllers/AuthenticationController.java`

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest request) {
        // 1. Autentica usando Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getSenha()
            )
        );

        // 2. Busca o usuário no banco
        Usuario usuario = usuarioRepository
            .findByEmail(request.getEmail())
            .orElseThrow();

        // 3. Gera tokens
        String accessToken = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        // 4. Retorna resposta
        return ResponseEntity.ok(LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tipo("Bearer")
            .build());
    }

    // POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        // 1. Extrai email do refresh token
        String userEmail = jwtService.extractUsername(request.getRefreshToken());

        // 2. Busca usuário
        Usuario usuario = usuarioRepository
            .findByEmail(userEmail)
            .orElseThrow();

        // 3. Valida refresh token
        if (jwtService.isTokenValid(request.getRefreshToken(), usuario)) {
            // 4. Gera novos tokens
            String accessToken = jwtService.generateToken(usuario);
            String refreshToken = jwtService.generateRefreshToken(usuario);

            return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .build());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
```

**Responsabilidade:**

- Fornecer endpoints de autenticação
- Login: validar credenciais e gerar tokens
- Refresh: renovar access token usando refresh token válido

---

## 🎯 Resumo Visual do Fluxo

```
1️⃣ LOGIN
   Cliente → POST /api/auth/login → AuthenticationController
                                           ↓
                                    AuthenticationManager valida
                                           ↓
                                    CustomUserDetailsService busca no banco
                                           ↓
                                    BCrypt compara senhas
                                           ↓
                                    JwtService gera tokens
                                           ↓
                                    Retorna tokens ao cliente

2️⃣ REQUISIÇÕES PROTEGIDAS
   Cliente → GET /api/categorias (com token) → JwtAuthenticationFilter
                                                       ↓
                                                 Extrai token do header
                                                       ↓
                                                 JwtService valida token
                                                       ↓
                                                 CustomUserDetailsService busca usuário
                                                       ↓
                                                 SecurityContext salva autenticação
                                                       ↓
                                                 Controller acessa usuário logado
                                                       ↓
                                                 Retorna dados apenas do usuário

3️⃣ CONTROLE DE ACESSO POR ROLES
   @PreAuthorize("hasRole('ADMIN')") → Spring Security valida roles
                                                ↓
                                          Usuario.getAuthorities() retorna roles
                                                ↓
                                          ROLE_ADMIN? ✅ OK, executa método
                                          ROLE_USER? ❌ 403 Forbidden
```

---

## 📊 Diagrama de Sequência

```mermaid
sequenceDiagram
    participant C as Cliente
    participant AC as AuthController
    participant AM as AuthManager
    participant UDS as UserDetailsService
    participant DB as Database
    participant JS as JwtService

    C->>AC: POST /api/auth/login {email, senha}
    AC->>AM: authenticate(email, senha)
    AM->>UDS: loadUserByUsername(email)
    UDS->>DB: findByEmail(email)
    DB-->>UDS: Usuario
    UDS-->>AM: UserDetails (Usuario)
    AM->>AM: BCrypt.matches(senha)
    AM-->>AC: Autenticado ✅
    AC->>JS: generateToken(usuario)
    JS-->>AC: accessToken
    AC->>JS: generateRefreshToken(usuario)
    JS-->>AC: refreshToken
    AC-->>C: {accessToken, refreshToken, tipo}

    Note over C,JS: Cliente agora tem tokens

    C->>JwtFilter: GET /api/categorias + Bearer token
    JwtFilter->>JS: extractUsername(token)
    JS-->>JwtFilter: email
    JwtFilter->>UDS: loadUserByUsername(email)
    UDS->>DB: findByEmail(email)
    DB-->>UDS: Usuario
    UDS-->>JwtFilter: UserDetails
    JwtFilter->>JS: isTokenValid(token, userDetails)
    JS-->>JwtFilter: true ✅
    JwtFilter->>SecurityContext: setAuthentication()
    JwtFilter->>Controller: doFilter()
    Controller->>SecurityContext: getAuthentication()
    SecurityContext-->>Controller: Usuario autenticado
    Controller-->>C: Dados da categoria
```

---

## ✅ Vantagens Dessa Implementação

### 1. **Stateless (Sem Estado)**

- ❌ Sem sessões no servidor (não usa memória)
- ✅ Escalável horizontalmente (múltiplos servidores)
- ✅ Ideal para microserviços

### 2. **Segurança**

- ✅ Tokens assinados com HMAC-SHA384
- ✅ Senhas criptografadas com BCrypt
- ✅ Tokens com expiração (24h access, 7d refresh)
- ✅ Refresh token rotation (previne roubo de tokens)

### 3. **Isolamento de Dados**

- ✅ Cada usuário só vê seus próprios dados
- ✅ SecurityContext identifica usuário automaticamente
- ✅ Queries filtradas por usuarioId

### 4. **Controle de Acesso (RBAC)**

- ✅ Roles (ADMIN/USER) validadas automaticamente
- ✅ `@PreAuthorize("hasRole('ADMIN')")` protege endpoints
- ✅ Granularidade de permissões

### 5. **Padrão de Mercado**

- ✅ JWT é amplamente adotado (OAuth 2.0, OpenID Connect)
- ✅ Compatível com SPAs, mobile apps
- ✅ Fácil integração com frontend

---

## 🔐 Configurações de Segurança

### application.yml

```yaml
security:
  jwt:
    secret-key: ${JWT_SECRET_KEY:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
    expiration-time: 86400000 # 24 horas (ms)
    refresh-expiration-time: 604800000 # 7 dias (ms)
```

### Variáveis de Ambiente (Produção)

```bash
JWT_SECRET_KEY=sua-chave-secreta-super-segura-com-64-caracteres-minimo
```

**⚠️ IMPORTANTE:**

- Nunca comite a secret-key no Git
- Use variáveis de ambiente em produção
- A chave deve ter no mínimo 64 caracteres
- Troque a chave periodicamente

---

## 🧪 Exemplos de Uso

### 1. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@financeiro.com",
    "senha": "admin123"
  }'
```

**Resposta:**

```json
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBmaW5hbmNlaXJvLmNvbSIsImlhdCI6MTY5ODc2NTQzMiwiZXhwIjoxNjk4ODUxODMyfQ.abc123...",
  "refreshToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBmaW5hbmNlaXJvLmNvbSIsImlhdCI6MTY5ODc2NTQzMiwiZXhwIjoxNjk5MzcwMjMyfQ.def456...",
  "tipo": "Bearer"
}
```

### 2. Acessar Endpoint Protegido

```bash
curl -X GET http://localhost:8080/api/categorias \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..."
```

### 3. Refresh Token

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzM4NCJ9..."
  }'
```

### 4. Endpoint Protegido por Role

```bash
# Admin pode criar usuário
curl -X POST http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer <token-admin>" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Novo Usuário",
    "email": "user@example.com",
    "senha": "senha123",
    "papel": "USER"
  }'

# User NÃO pode criar usuário (403 Forbidden)
curl -X POST http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer <token-user>" \
  -H "Content-Type: application/json" \
  -d '{ ... }'
```

---

## 🚀 Próximos Passos Sugeridos

### 1. **Testes Automatizados**

- Testes de integração para endpoints de autenticação
- Testes unitários para JwtService
- Testes de segurança com Spring Security Test

### 2. **Refresh Token Rotation**

- Invalidar refresh tokens antigos após uso
- Armazenar refresh tokens no banco (controle de sessões)
- Revogar todos os tokens de um usuário

### 3. **Rate Limiting**

- Limitar tentativas de login (prevenir brute force)
- Usar Redis para controle de tentativas
- Bloqueio temporário após N tentativas falhas

### 4. **Auditoria e Logs**

- Log de tentativas de login (sucesso/falha)
- Rastreamento de IPs
- Notificação de login em novo dispositivo

### 5. **Autenticação Multifator (2FA)**

- TOTP (Google Authenticator, Authy)
- SMS ou Email com código
- Biometria

### 6. **OAuth 2.0 / Social Login**

- Login com Google
- Login com GitHub
- Login com Microsoft

### 7. **Token Blacklist**

- Invalidar tokens antes da expiração (logout)
- Usar Redis para armazenar tokens revogados
- Cleanup automático de tokens expirados

---

## 📚 Referências

### Documentação Oficial

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [JWT.io](https://jwt.io/) - Debug e documentação JWT
- [JJWT (Java JWT)](https://github.com/jwtk/jjwt) - Biblioteca usada no projeto

### Artigos e Tutoriais

- [Spring Security Architecture](https://spring.io/guides/topicals/spring-security-architecture)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

### Vídeos Recomendados

- [Spring Security + JWT - Amigoscode](https://www.youtube.com/watch?v=KxqlJblhzfI)
- [Spring Boot 3 Security - Dan Vega](https://www.youtube.com/watch?v=UaB-0e76LdQ)

---

## 🤝 Contribuindo

Se você encontrar algum erro nesta documentação ou tiver sugestões de melhorias, por favor:

1. Abra uma issue no repositório
2. Envie um pull request com as correções
3. Entre em contato com a equipe de desenvolvimento

---

## 📝 Changelog

### Versão 1.0.0 (2025-10-09)

- ✅ Implementação inicial do Spring Security com JWT
- ✅ Autenticação stateless
- ✅ Refresh token
- ✅ Controle de acesso por roles (ADMIN/USER)
- ✅ CORS configurado
- ✅ Isolamento de dados por usuário

---

**Última atualização:** 10 de outubro de 2025  
**Autor:** Christian Martins  
**Projeto:** Sistema Financeiro Pessoal
