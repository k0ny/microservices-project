package com.xantrix.webapp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api()
    {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xantrix.webapp.controller")) //introduce il pkg dove ci sono gli endpoint
                .paths(regex("/api/articoli.*")) //gli indico il basepath
                .build()
                .apiInfo(apiInfo()); //inserisce informazioni aggiuntive
    }

    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder()
                .title("ARTICOLI WEB SERVICE API")
                .description("Spring Boot REST API per la gestione articoli AlphaShop")
                .version("1.0.0")
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .contact(new Contact("Vincenzo Giordano", "https://basicinfourl.com/info", "prova@xantrix.it"))
                .build();
    }
}
