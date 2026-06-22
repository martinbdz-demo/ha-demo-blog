package com.example.blog;

import com.example.blog.config.SecurityConfig;
import com.example.blog.controller.AdminController;
import com.example.blog.model.BlogPost;
import com.example.blog.service.BlogPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BlogPostService service;

    // --- Security ---

    @Test
    void loginPage_isAccessibleWithoutAuth() throws Exception {
        mvc.perform(get("/admin/login"))
                .andExpect(status().isOk());
    }

    @Test
    void dashboard_redirectsToLogin_whenNotAuthenticated() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void dashboard_isAccessible_withAdminRole() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void dashboard_isForbidden_withoutAdminRole() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    // --- Admin features ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void newPostForm_returns200_withEmptyPostInModel() throws Exception {
        mvc.perform(get("/admin/posts/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("post"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPost_redirectsToDashboard_whenValid() throws Exception {
        when(service.save(any())).thenReturn(new BlogPost());

        mvc.perform(post("/admin/posts").with(csrf())
                        .param("title", "New Post")
                        .param("excerpt", "Short excerpt for this post")
                        .param("content", "Full content of the post"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(service).save(any(BlogPost.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPost_returnsForm_whenTitleIsBlank() throws Exception {
        mvc.perform(post("/admin/posts").with(csrf())
                        .param("title", "")
                        .param("excerpt", "Some excerpt")
                        .param("content", "Some content"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/new-post"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePost_redirectsToDashboard() throws Exception {
        mvc.perform(post("/admin/posts/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(service).deleteById(1L);
    }
}
