package com.example.blog;

import com.example.blog.config.SecurityConfig;
import com.example.blog.controller.AdminApiController;
import com.example.blog.model.BlogPost;
import com.example.blog.service.BlogPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminApiController.class)
@Import(SecurityConfig.class)
class AdminApiControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BlogPostService service;

    // --- Security ---

    @Test
    void createPost_returns401_whenNotAuthenticated() throws Exception {
        mvc.perform(post("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPostJson()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deletePost_returns401_whenNotAuthenticated() throws Exception {
        mvc.perform(delete("/api/admin/posts/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deletePost_returns403_withoutAdminRole() throws Exception {
        mvc.perform(delete("/api/admin/posts/1"))
                .andExpect(status().isForbidden());
    }

    // --- Admin features ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPost_returns201_withValidBody() throws Exception {
        BlogPost saved = blogPost(1L, "New Post", "Excerpt text here", "Full content here");
        when(service.save(any())).thenReturn(saved);

        mvc.perform(post("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPostJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Post"));

        verify(service).save(any(BlogPost.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPost_returns400_whenTitleIsBlank() throws Exception {
        String body = objectMapper.writeValueAsString(
                blogPost(null, "", "Some excerpt", "Some content"));

        mvc.perform(post("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePost_returns204() throws Exception {
        mvc.perform(delete("/api/admin/posts/1"))
                .andExpect(status().isNoContent());

        verify(service).deleteById(1L);
    }

    private String validPostJson() throws Exception {
        return objectMapper.writeValueAsString(
                blogPost(null, "New Post", "Excerpt text here", "Full content here"));
    }

    private BlogPost blogPost(Long id, String title, String excerpt, String content) {
        BlogPost p = new BlogPost();
        p.setId(id);
        p.setTitle(title);
        p.setExcerpt(excerpt);
        p.setContent(content);
        p.setCreatedAt(LocalDateTime.now());
        return p;
    }
}
