package com.essi.Dependency;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.essi.Dependency.Service.DependencyService;
import com.essi.Dependency.Service.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(DependencyService storageService) {
	return (args) -> {
	    storageService.deleteAll();
	};
    }

}
