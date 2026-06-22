package com.example.blog.config;

import com.example.blog.model.BlogPost;
import com.example.blog.repository.BlogPostRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final BlogPostRepository repository;

    public DataInitializer(BlogPostRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) return;

        repository.saveAll(List.of(
            post(
                "Getting Started with Spring Boot",
                "Spring Boot makes it incredibly easy to build production-ready applications with minimal configuration.",
                """
                Spring Boot is an opinionated framework that builds on top of the Spring ecosystem. \
                It provides auto-configuration, embedded servers, and production-ready features out of the box.

                The key idea is convention over configuration: sensible defaults are applied automatically, \
                and you only override what you need. A working REST API or web application can be up and \
                running in minutes.

                Key features include embedded Tomcat (no WAR deployments needed), the Spring Boot starter \
                system for dependency management, Actuator for health checks and metrics, and seamless \
                integration with Spring Data, Security, and the broader Spring ecosystem.
                """
            ),
            post(
                "Understanding Thymeleaf Templates",
                "Thymeleaf is a modern server-side Java template engine that works seamlessly with Spring MVC.",
                """
                Thymeleaf processes HTML templates on the server and sends fully rendered pages to the browser — \
                no JavaScript framework required for basic web applications.

                Templates are valid HTML files, which means they can be opened directly in a browser during \
                development and still look correct. The Thymeleaf attributes (th:text, th:each, th:if, th:href) \
                are simply ignored by browsers that don't understand them.

                Common patterns include iterating over a list with th:each, conditional rendering with th:if, \
                binding form fields with th:field and th:object, and generating safe URLs with @{...} syntax. \
                Spring Security integration adds sec:authorize for showing or hiding content based on roles.
                """
            ),
            post(
                "Docker Multi-Stage Builds Explained",
                "Multi-stage builds dramatically reduce final image size by separating the build environment from the runtime environment.",
                """
                A naive Docker image for a Java application might start with a full JDK image, copy in source \
                code, run Maven, and end up with a 600MB+ image that contains Maven, the JDK, and build \
                artifacts you don't need at runtime.

                Multi-stage builds solve this by using multiple FROM statements in a single Dockerfile. \
                The first stage (the builder) uses a heavy image with all build tools. The second stage \
                starts fresh from a minimal JRE image and copies only the compiled artifact from the builder.

                An important optimization is to copy pom.xml and run dependency:go-offline before copying \
                source files. Docker caches each layer, so if only source files change, the dependency \
                download layer is reused — making incremental rebuilds much faster.
                """
            )
        ));
    }

    private BlogPost post(String title, String excerpt, String content) {
        BlogPost p = new BlogPost();
        p.setTitle(title);
        p.setExcerpt(excerpt);
        p.setContent(content);
        return p;
    }
}
