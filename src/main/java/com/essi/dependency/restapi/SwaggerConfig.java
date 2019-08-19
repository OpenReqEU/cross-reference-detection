package com.essi.dependency.restapi;

import com.essi.dependency.controller.Controller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@PropertySource("classpath:swagger.properties")
@ComponentScan(basePackageClasses = Controller.class)
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurerAdapter {

    private static final String	SWAGGER_API_VERSION = "1.0";
    private static final String	LICENSE_TEXT	    = "EPL-v1.0";
    private static final String	LICENSE_URL	    = "https://www.eclipse.org/legal/epl-v10.html";
    private static final String	TITLE		    = "Cross-reference detection RESTful API";
    private static final String	DESCRIPTION	    = "A REST API used to identify cross-references from requirements. "
	    + "Requirements can be in a database, or in an html document-html. Cross-references are dependencies explicitly "
	    + "stated, either internal (between requirements of the same project), or external (between a requirement and an external source).";

    /**
     * API Documentation Generation.
     * @return
     */
    @Bean
    public Docket api() {
	return new Docket(DocumentationType.SWAGGER_2).host("217.172.12.199:9401")
		.apiInfo(apiInfo()).pathMapping("/").select()
		.apis(RequestHandlerSelectors.basePackage("com.essi.Dependency.Controller")).paths(PathSelectors.any())
		.build().tags(new Tag("Cross-reference detection Service", "API related to cross-reference detection"));
    }

    /**
     * Informtion that appear in the API Documentation Head.
     * 
     * @return
     */
    private ApiInfo apiInfo() {
	return new ApiInfoBuilder().title(TITLE).description(DESCRIPTION).license(LICENSE_TEXT).licenseUrl(LICENSE_URL)
		.version(SWAGGER_API_VERSION).contact(new Contact("UPC-GESSI (OPENReq)", "http://openreq.eu/", ""))
		.build();
    }
}
