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
        Usuario admin = ensureDefaultAdminUser();
        List<Categoria> categorias = ensureDefaultCategorias();
        ensureDefaultTransacoes(admin, categorias);
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

    private List<Categoria> ensureDefaultCategorias() {
        if (categoriaRepository.count() > 0) {
            return categoriaRepository.findAll();
        }

        List<Categoria> categorias = List.of(
                Categoria.builder().nome("Salário").descricao("Receita de salário mensal").tipo(TipoCategoria.RECEITA).build(),
                Categoria.builder().nome("Freelance").descricao("Trabalhos extras e freelances").tipo(TipoCategoria.RECEITA).build(),
                Categoria.builder().nome("Investimentos").descricao("Rendimentos de investimentos").tipo(TipoCategoria.RECEITA).build(),
                Categoria.builder().nome("Vendas").descricao("Vendas de produtos ou serviços").tipo(TipoCategoria.RECEITA).build(),
                Categoria.builder().nome("Aluguel Recebido").descricao("Receita de aluguel de imóveis").tipo(TipoCategoria.RECEITA).build(),
                Categoria.builder().nome("Alimentação").descricao("Gastos com alimentação e supermercado").tipo(TipoCategoria.DESPESA).build(),
                Categoria.builder().nome("Transporte").descricao("Combustível, uber, transporte público").tipo(TipoCategoria.DESPESA).build(),
                Categoria.builder().nome("Moradia").descricao("Aluguel, condomínio, IPTU").tipo(TipoCategoria.DESPESA).build(),
                Categoria.builder().nome("Saúde").descricao("Plano de saúde, medicamentos, consultas").tipo(TipoCategoria.DESPESA).build(),
                Categoria.builder().nome("Lazer").descricao("Cinema, restaurantes, viagens").tipo(TipoCategoria.DESPESA).build(),
                Categoria.builder().nome("Educação").descricao("Cursos e capacitações").tipo(TipoCategoria.DESPESA).build(),
                Categoria.builder().nome("Utilities").descricao("Energia, água, internet, telefone").tipo(TipoCategoria.DESPESA).build());

        List<Categoria> salvas = categoriaRepository.saveAll(categorias);
        LOGGER.info("{} categorias padrão criadas", salvas.size());
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
