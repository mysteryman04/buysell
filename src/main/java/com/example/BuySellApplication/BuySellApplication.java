package com.example.BuySellApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@EnableJpaAuditing
@SpringBootApplication(exclude = SpringApplicationAdminJmxAutoConfiguration.class)
public class BuySellApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuySellApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer(ResourceLoader resourceLoader) {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/data/**").addResourceLocations("classpath:/data/");
            }
        };
    }
}
