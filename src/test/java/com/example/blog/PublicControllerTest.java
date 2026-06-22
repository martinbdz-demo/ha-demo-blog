package com.example.blog;

import com.example.blog.config.SecurityConfig;
import com.example.blog.controller.PublicController;
import com.example.blog.model.BlogPost;
import com.example.blog.service.BlogPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicController.class)
@Import(SecurityConfig.class)
class PublicControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BlogPostService service;

    @Test
    void frontPage_returns200_withPostsInModel() throws Exception {
        when(service.findAll()).thenReturn(List.of(new BlogPost()));

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("posts"));
    }

    @Test
    void postDetail_returns200_whenPostExists() throws Exception {
        BlogPost post = new BlogPost();
        when(service.findById(1L)).thenReturn(Optional.of(post));

        mvc.perform(get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void postDetail_redirectsToFrontPage_whenPostNotFound() throws Exception {
        when(service.findById(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/post/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
