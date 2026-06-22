package com.example.blog.controller;

import com.example.blog.model.BlogPost;
import com.example.blog.service.BlogPostService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BlogPostService service;

    public AdminController(BlogPostService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("posts", service.findAll());
        return "admin/dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        model.addAttribute("post", new BlogPost());
        return "admin/new-post";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/posts")
    public String createPost(@Valid @ModelAttribute("post") BlogPost post,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/new-post";
        }
        service.save(post);
        redirectAttributes.addFlashAttribute("success", "Post published successfully.");
        return "redirect:/admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Post deleted.");
        return "redirect:/admin";
    }
}
