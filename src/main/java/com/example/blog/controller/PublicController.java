package com.example.blog.controller;

import com.example.blog.service.BlogPostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PublicController {

    private final BlogPostService service;

    public PublicController(BlogPostService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", service.findAll());
        return "index";
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return service.findById(id).map(post -> {
            model.addAttribute("post", post);
            return "post";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Post not found.");
            return "redirect:/";
        });
    }
}
