package br.com.fiap.soat.oficina.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestão de Oficina Mecânica")
                        .description("Tech Challenge SOAT Fase 1 - Sistema de gerenciamento de oficina mecânica desenvolvido com Clean Architecture, DDD, Java 21 e Spring Boot 3.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Thiago Costa Araki"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .tags(List.of(
                        new Tag().name("Autenticação").description("Endpoints de login e geração de token JWT"),
                        new Tag().name("Clientes").description("Endpoints para cadastro e gestão de clientes"),
                        new Tag().name("Veículos").description("Endpoints para cadastro e consulta de veículos"),
                        new Tag().name("Peças").description("Endpoints para catálogo e controle de estoque de peças"),
                        new Tag().name("Ordens de Serviço (Administrativo)").description("Fluxo operacional completo de atendimento e gestão das ordens de serviço"),
                        new Tag().name("Acompanhamento Público (Cliente)").description("Endpoints públicos para consulta de status e aprovação de orçamento pelo próprio cliente")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Insira o token JWT gerado no endpoint /api/v1/auth/login")));
    }

    @Bean
    public OpenApiCustomizer ordenarCaminhosOpenApi() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths == null) {
                return;
            }

            List<String> ordemPrioritaria = List.of(
                    // 1. Autenticação
                    "/api/v1/auth/login",

                    // 2. Clientes
                    "/api/v1/clientes",
                    "/api/v1/clientes/{id}",
                    "/api/v1/clientes/documento/{documento}",

                    // 3. Veículos
                    "/api/v1/veiculos",
                    "/api/v1/veiculos/{id}",
                    "/api/v1/veiculos/placa/{placa}",
                    "/api/v1/veiculos/cliente/{clienteId}",

                    // 4. Peças
                    "/api/v1/pecas",
                    "/api/v1/pecas/{id}",
                    "/api/v1/pecas/{id}/repor-estoque",
                    "/api/v1/pecas/{id}/debitar-estoque",
                    "/api/v1/pecas/abaixo-minimo",

                    // 5. Ordens de Serviço (Administrativo) - Ciclo de Vida Sequencial (1 ao 9)
                    "/api/v1/ordens-servico",
                    "/api/v1/ordens-servico/{id}/iniciar-diagnostico",
                    "/api/v1/ordens-servico/{id}/diagnostico",
                    "/api/v1/ordens-servico/{id}/itens/servicos",
                    "/api/v1/ordens-servico/{id}/itens/pecas",
                    "/api/v1/ordens-servico/{id}/submeter-aprovacao",
                    "/api/v1/ordens-servico/{id}/aprovar",
                    "/api/v1/ordens-servico/{id}/finalizar",
                    "/api/v1/ordens-servico/{id}/entregar",
                    "/api/v1/ordens-servico/{id}",
                    "/api/v1/ordens-servico/{id}/cancelar",
                    "/api/v1/ordens-servico/metricas/tempo-medio",

                    // 6. Acompanhamento Público (Cliente)
                    "/api/v1/public/ordens-servico/{id}/status",
                    "/api/v1/public/ordens-servico/{id}/aprovar"
            );

            Paths ordenados = new Paths();
            for (String caminho : ordemPrioritaria) {
                if (paths.containsKey(caminho)) {
                    ordenados.addPathItem(caminho, paths.get(caminho));
                }
            }
            paths.forEach((k, v) -> {
                if (!ordenados.containsKey(k)) {
                    ordenados.addPathItem(k, v);
                }
            });

            openApi.setPaths(ordenados);

            openApi.setTags(List.of(
                    new Tag().name("Autenticação").description("Endpoints de login e geração de token JWT"),
                    new Tag().name("Clientes").description("Endpoints para cadastro e gestão de clientes"),
                    new Tag().name("Veículos").description("Endpoints para cadastro e consulta de veículos"),
                    new Tag().name("Peças").description("Endpoints para catálogo e controle de estoque de peças"),
                    new Tag().name("Ordens de Serviço (Administrativo)").description("Fluxo operacional completo de atendimento e gestão das ordens de serviço"),
                    new Tag().name("Acompanhamento Público (Cliente)").description("Endpoints públicos para consulta de status e aprovação de orçamento pelo próprio cliente")
            ));
        };
    }
}

