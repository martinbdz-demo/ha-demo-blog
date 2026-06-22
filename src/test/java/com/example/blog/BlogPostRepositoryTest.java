package com.example.blog;

import com.example.blog.model.BlogPost;
import com.example.blog.repository.BlogPostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BlogPostRepositoryTest {

    @Autowired
    BlogPostRepository repository;

    @Test
    void prePersist_setsCreatedAtAutomatically() {
        BlogPost post = post("Title", "Excerpt", "Content");

        BlogPost saved = repository.save(post);

        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findAllByOrderByCreatedAtDesc_returnsNewerPostFirst() {
        BlogPost older = post("Older", "e", "c");
        older.setCreatedAt(LocalDateTime.now().minusDays(1));
        repository.save(older);

        BlogPost newer = post("Newer", "e", "c");
        newer.setCreatedAt(LocalDateTime.now());
        repository.save(newer);

        List<BlogPost> results = repository.findAllByOrderByCreatedAtDesc();

        assertThat(results).extracting(BlogPost::getTitle)
                .containsExactly("Newer", "Older");
    }

    private BlogPost post(String title, String excerpt, String content) {
        BlogPost p = new BlogPost();
        p.setTitle(title);
        p.setExcerpt(excerpt);
        p.setContent(content);
        return p;
    }
}
