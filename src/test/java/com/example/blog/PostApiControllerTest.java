package com.example.blog;

import com.example.blog.config.SecurityConfig;
import com.example.blog.controller.PostApiController;
import com.example.blog.model.BlogPost;
import com.example.blog.service.BlogPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostApiController.class)
@Import(SecurityConfig.class)
class PostApiControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BlogPostService service;

    @Test
    void listPosts_returns200_withJsonArray() throws Exception {
        BlogPost post = post(1L, "Title", "Excerpt", "Content");
        when(service.findAll()).thenReturn(List.of(post));

        mvc.perform(get("/api/posts").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Title"));
    }

    @Test
    void getPost_returns200_whenFound() throws Exception {
        BlogPost post = post(1L, "Title", "Excerpt", "Content");
        when(service.findById(1L)).thenReturn(Optional.of(post));

        mvc.perform(get("/api/posts/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.content").value("Content"));
    }

    @Test
    void getPost_returns404_whenNotFound() throws Exception {
        when(service.findById(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/posts/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private BlogPost post(Long id, String title, String excerpt, String content) {
        BlogPost p = new BlogPost();
        p.setId(id);
        p.setTitle(title);
        p.setExcerpt(excerpt);
        p.setContent(content);
        p.setCreatedAt(LocalDateTime.now());
        return p;
    }
}
