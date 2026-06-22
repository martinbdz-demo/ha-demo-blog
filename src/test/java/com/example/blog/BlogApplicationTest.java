package com.example.blog;

import com.example.blog.repository.BlogPostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BlogApplicationTest {

    @Autowired
    BlogPostRepository repository;

    @Test
    void contextLoads() {
    }

    @Test
    void dataInitializer_seedsThreePosts() {
        assertThat(repository.count()).isEqualTo(3);
    }
}
