package com.financeiro.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Categoria.TipoCategoria;
import com.financeiro.domain.entities.Transacao;
import com.financeiro.domain.entities.Transacao.TipoTransacao;
import com.financeiro.domain.entities.Usuario;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.TransacaoRepository;
import com.financeiro.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BootstrapData implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapData.class);

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            Usuario admin = ensureDefaultAdminUser();
            List<Categoria> categorias = ensureDefaultCategorias(admin);
            ensureDefaultTransacoes(admin, categorias);
            LOGGER.info("✅ BootstrapData executado com sucesso!");
        } catch (Exception e) {
            LOGGER.error("❌ Erro ao executar BootstrapData: {}", e.getMessage(), e);
        }
    }

    private Usuario ensureDefaultAdminUser() {
        return usuarioRepository.findByEmail("admin@financeiro.com")
                .orElseGet(() -> {
                    Usuario novoAdmin = Usuario.builder()
                            .nome("Admin Sistema")
                            .email("admin@financeiro.com")
                            .senha(passwordEncoder.encode("admin123"))
                            .papel(Usuario.Papel.ADMIN)
                            .ativo(true)
                            .build();

                    Usuario salvo = usuarioRepository.save(novoAdmin);
                    LOGGER.info("Usuário admin padrão criado com email admin@financeiro.com");
                    return salvo;
                });
    }

    private List<Categoria> ensureDefaultCategorias(Usuario admin) {
        // Verificar se o admin já tem categorias
        List<Categoria> existentes = categoriaRepository.findAtivasByUsuario(admin.getId());
        if (!existentes.isEmpty()) {
            LOGGER.info("Admin já possui {} categorias", existentes.size());
            return existentes;
        }

        LOGGER.info("Criando categorias padrão para o admin...");
        List<Categoria> categorias = List.of(
                Categoria.builder().nome("Salário").descricao("Receita de salário mensal").tipo(TipoCategoria.RECEITA).cor("#4CAF50").usuario(admin).build(),
                Categoria.builder().nome("Freelance").descricao("Trabalhos extras e freelances").tipo(TipoCategoria.RECEITA).cor("#8BC34A").usuario(admin).build(),
                Categoria.builder().nome("Investimentos").descricao("Rendimentos de investimentos").tipo(TipoCategoria.RECEITA).cor("#009688").usuario(admin).build(),
                Categoria.builder().nome("Vendas").descricao("Vendas de produtos ou serviços").tipo(TipoCategoria.RECEITA).cor("#00BCD4").usuario(admin).build(),
                Categoria.builder().nome("Aluguel Recebido").descricao("Receita de aluguel de imóveis").tipo(TipoCategoria.RECEITA).cor("#66BB6A").usuario(admin).build(),
                Categoria.builder().nome("Alimentação").descricao("Gastos com alimentação e supermercado").tipo(TipoCategoria.DESPESA).cor("#F44336").usuario(admin).build(),
                Categoria.builder().nome("Transporte").descricao("Combustível, uber, transporte público").tipo(TipoCategoria.DESPESA).cor("#FF5722").usuario(admin).build(),
                Categoria.builder().nome("Moradia").descricao("Aluguel, condomínio, IPTU").tipo(TipoCategoria.DESPESA).cor("#E91E63").usuario(admin).build(),
                Categoria.builder().nome("Saúde").descricao("Plano de saúde, medicamentos, consultas").tipo(TipoCategoria.DESPESA).cor("#9C27B0").usuario(admin).build(),
                Categoria.builder().nome("Lazer").descricao("Cinema, restaurantes, viagens").tipo(TipoCategoria.DESPESA).cor("#FF9800").usuario(admin).build(),
                Categoria.builder().nome("Educação").descricao("Cursos e capacitações").tipo(TipoCategoria.DESPESA).cor("#3F51B5").usuario(admin).build(),
                Categoria.builder().nome("Utilities").descricao("Energia, água, internet, telefone").tipo(TipoCategoria.DESPESA).cor("#795548").usuario(admin).build());

        List<Categoria> salvas = categoriaRepository.saveAll(categorias);
        LOGGER.info("{} categorias padrão criadas para o admin", salvas.size());
        return salvas;
    }

    private void ensureDefaultTransacoes(Usuario usuario, List<Categoria> categorias) {
        if (transacaoRepository.count() > 0) {
            return;
        }

        Categoria salario = buscarCategoria(categorias, "Salário");
        Categoria alimentacao = buscarCategoria(categorias, "Alimentação");

        List<Transacao> transacoes = List.of(
                Transacao.builder()
                        .descricao("Salário Agosto")
                        .valor(BigDecimal.valueOf(5500.00))
                        .tipo(TipoTransacao.RECEITA)
                        .dataTransacao(LocalDate.of(2025, 8, 1))
                        .categoria(salario)
                        .usuario(usuario)
                        .build(),
                Transacao.builder()
                        .descricao("Supermercado Agosto")
                        .valor(BigDecimal.valueOf(650.00))
                        .tipo(TipoTransacao.DESPESA)
                        .dataTransacao(LocalDate.of(2025, 8, 3))
                        .categoria(alimentacao)
                        .usuario(usuario)
                        .build());

        transacaoRepository.saveAll(transacoes);
        LOGGER.info("{} transações de exemplo criadas", transacoes.size());
    }

    private Categoria buscarCategoria(List<Categoria> categorias, String nome) {
        return categorias.stream()
                .filter(cat -> cat.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Categoria padrão não encontrada: " + nome));
    }
}
