package com.example.blog;

import com.example.blog.model.BlogPost;
import com.example.blog.repository.BlogPostRepository;
import com.example.blog.service.BlogPostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {

    @Mock
    BlogPostRepository repository;

    @InjectMocks
    BlogPostService service;

    @Test
    void findAll_delegatesToOrderedRepositoryMethod() {
        BlogPost post = new BlogPost();
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post));

        List<BlogPost> result = service.findAll();

        assertThat(result).containsExactly(post);
        verify(repository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void findById_returnsPost_whenPresent() {
        BlogPost post = new BlogPost();
        when(repository.findById(1L)).thenReturn(Optional.of(post));

        assertThat(service.findById(1L)).contains(post);
    }

    @Test
    void findById_returnsEmpty_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.findById(99L)).isEmpty();
    }

    @Test
    void save_returnsPersistedPost() {
        BlogPost post = new BlogPost();
        when(repository.save(post)).thenReturn(post);

        assertThat(service.save(post)).isSameAs(post);
        verify(repository).save(post);
    }

    @Test
    void deleteById_delegatesToRepository() {
        service.deleteById(1L);

        verify(repository).deleteById(1L);
    }
}
