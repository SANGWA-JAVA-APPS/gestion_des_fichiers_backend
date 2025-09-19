package com.magerwa.gestiondesfichiers.controller;

import com.magerwa.gestiondesfichiers.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @Autowired
    private DocumentService documentService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recentDocuments", documentService.findActiveDocuments());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}