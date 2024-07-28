package com.generation.personalblog.configuration;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;


@Configuration

public class SwaggerConfig {

	// retorna uma nova instancia com os dados da documentação
	@Bean 	// e indica que este objeto pode ser injetado em qualquer ponto da aplicação.
    OpenAPI springPersonalBlogOpenAPI() {
        return new OpenAPI()
            .info(new Info() //Insere as informações sobre a API
                .title("Projeto Personal Blog")
                .description("Projeto Personal Blog  - Generation Brasil")
                .version("v0.0.1")
                .license(new License()
                    .name("Luana Silva")
                    .url("https://github.com/Lu-nas/"))
                .contact(new Contact()
                    .name("Luana silva")
                    .url("https://github.com/Lu-nas/Personal_Blog")
                    .email("luanasilva.ss9497@gmail.com")))
            .externalDocs(new ExternalDocumentation()
                .description("Github")
                .url("https://github.com/Lu-nas/Personal_Blog"));
    }

	@Bean
	OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {

		//O Método acima, personaliza todas as mensagens HTTP Responses (Respostas das requisições) do Swagger
		// Cria um primeiro looping que fará a leitura de todos os recursos (Paths) através do Método getPaths()
		return openApi -> { 
			openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations()
					.forEach(operation -> {

				ApiResponses apiResponses = operation.getResponses();

				apiResponses.addApiResponse("200", createApiResponse("Sucesso!"));
				apiResponses.addApiResponse("201", createApiResponse("Objeto Persistido!"));
				apiResponses.addApiResponse("204", createApiResponse("Objeto Excluído!"));
				apiResponses.addApiResponse("400", createApiResponse("Erro na Requisição!"));
				apiResponses.addApiResponse("401", createApiResponse("Acesso Não Autorizado!"));
				apiResponses.addApiResponse("403", createApiResponse("Acesso Proibido!"));
				apiResponses.addApiResponse("404", createApiResponse("Objeto Não Encontrado!"));
				apiResponses.addApiResponse("500", createApiResponse("Erro na Aplicação!"));

			}));
		};
		
	}

	//O Método createApiResponse() adiciona uma descrição (Mensagem) em cada Resposta HTTP.
	private ApiResponse createApiResponse(String message) {

		return new ApiResponse().description(message);

	}
}