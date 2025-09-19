package com.magerwa.gestiondesfichiers.controller;

import com.magerwa.gestiondesfichiers.entity.Document;
import com.magerwa.gestiondesfichiers.entity.Section;
import com.magerwa.gestiondesfichiers.entity.User;
import com.magerwa.gestiondesfichiers.service.DocumentService;
import com.magerwa.gestiondesfichiers.service.OrganizationService;
import com.magerwa.gestiondesfichiers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/documents")
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    public String listDocuments(Model model, Authentication authentication) {
        List<Document> documents = documentService.findActiveDocuments();
        model.addAttribute("documents", documents);
        return "documents/list";
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        model.addAttribute("sections", organizationService.findAllSections());
        return "documents/upload";
    }

    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file,
                               @RequestParam("title") String title,
                               @RequestParam("description") String description,
                               @RequestParam("sectionId") Long sectionId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findByUsername(authentication.getName());
            Optional<Section> sectionOpt = organizationService.findSectionById(sectionId);
            
            if (userOpt.isPresent() && sectionOpt.isPresent()) {
                Document document = documentService.uploadDocument(file, title, description, userOpt.get(), sectionOpt.get());
                redirectAttributes.addFlashAttribute("successMessage", "Document uploaded successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "User or Section not found!");
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading file: " + e.getMessage());
        }
        
        return "redirect:/documents";
    }

    @GetMapping("/{id}")
    public String viewDocument(@PathVariable Long id, Model model) {
        Optional<Document> documentOpt = documentService.findById(id);
        if (documentOpt.isPresent()) {
            model.addAttribute("document", documentOpt.get());
            return "documents/view";
        }
        return "redirect:/documents";
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Optional<Document> documentOpt = documentService.findById(id);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();
            Path filePath = Paths.get(document.getFilePath());
            Resource resource = new FileSystemResource(filePath.toFile());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/archive")
    public String archiveDocument(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        documentService.archiveDocument(id);
        redirectAttributes.addFlashAttribute("successMessage", "Document archived successfully!");
        return "redirect:/documents";
    }

    @PostMapping("/{id}/delete")
    public String deleteDocument(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        documentService.deleteDocument(id);
        redirectAttributes.addFlashAttribute("successMessage", "Document deleted successfully!");
        return "redirect:/documents";
    }

    @GetMapping("/section/{sectionId}")
    public String listDocumentsBySection(@PathVariable Long sectionId, Model model) {
        List<Document> documents = documentService.findBySectionId(sectionId);
        Optional<Section> sectionOpt = organizationService.findSectionById(sectionId);
        model.addAttribute("documents", documents);
        sectionOpt.ifPresent(section -> model.addAttribute("section", section));
        return "documents/list";
    }
}