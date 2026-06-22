package com.example.blog.service;

import com.example.blog.model.BlogPost;
import com.example.blog.repository.BlogPostRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BlogPostService {

    private final BlogPostRepository repository;

    public BlogPostService(BlogPostRepository repository) {
        this.repository = repository;
    }

    public List<BlogPost> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<BlogPost> findById(Long id) {
        return repository.findById(id);
    }

    public BlogPost save(BlogPost post) {
        return repository.save(post);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
