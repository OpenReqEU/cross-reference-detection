package com.essi.Dependency;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.essi.Dependency.Service.DependencyService;
import com.essi.Dependency.Service.StorageProperties;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
// @PropertySource({"classpath:application.properties"})
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
    public static void main(String[] args) throws IOException {
	SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(DependencyService storageService) {
	return (args) -> {
	    storageService.deleteAll();
	    //storageService.init();
	};
    }

}
