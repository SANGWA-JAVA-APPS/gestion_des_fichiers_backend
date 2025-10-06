package com.magerwa.gestiondesfichiers.controller;

import com.magerwa.gestiondesfichiers.entity.*;
import com.magerwa.gestiondesfichiers.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("countries", organizationService.findAllCountries());
        model.addAttribute("departments", organizationService.findAllDepartments());
        model.addAttribute("entities", organizationService.findAllEntities());
        model.addAttribute("modules", organizationService.findAllModules());
        model.addAttribute("sections", organizationService.findAllSections());
        return "admin/dashboard";
    }

    // Country management
    @GetMapping("/countries")
    public String listCountries(Model model) {
        model.addAttribute("countries", organizationService.findAllCountries());
        model.addAttribute("country", new Country());
        return "admin/countries";
    }

    @PostMapping("/countries")
    public String createCountry(@Valid @ModelAttribute Country country, BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix the validation errors");
            return "redirect:/admin/countries";
        }
        try {
            organizationService.createCountry(country);
            redirectAttributes.addFlashAttribute("successMessage", "Country created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/countries";
    }

    // Department management
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", organizationService.findAllDepartments());
        model.addAttribute("countries", organizationService.findAllCountries());
        model.addAttribute("department", new Department());
        return "admin/departments";
    }

    @PostMapping("/departments")
    public String createDepartment(@Valid @ModelAttribute Department department, BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix the validation errors");
            return "redirect:/admin/departments";
        }
        try {
            organizationService.createDepartment(department);
            redirectAttributes.addFlashAttribute("successMessage", "Department created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/departments";
    }

    // Entity management
    @GetMapping("/entities")
    public String listEntities(Model model) {
        model.addAttribute("entities", organizationService.findAllEntities());
        model.addAttribute("departments", organizationService.findAllDepartments());
        model.addAttribute("entity", new MagerwaEntity());
        return "admin/entities";
    }

    @PostMapping("/entities")
    public String createEntity(@Valid @ModelAttribute MagerwaEntity entity, BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix the validation errors");
            return "redirect:/admin/entities";
        }
        try {
            organizationService.createEntity(entity);
            redirectAttributes.addFlashAttribute("successMessage", "Entity created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/entities";
    }

    // Module management
    @GetMapping("/modules")
    public String listModules(Model model) {
        model.addAttribute("modules", organizationService.findAllModules());
        model.addAttribute("entities", organizationService.findAllEntities());
        model.addAttribute("module", new com.magerwa.gestiondesfichiers.entity.Module());
        return "admin/modules";
    }

    @PostMapping("/modules")
    public String createModule(@Valid @ModelAttribute com.magerwa.gestiondesfichiers.entity.Module module, BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix the validation errors");
            return "redirect:/admin/modules";
        }
        try {
            organizationService.createModule(module);
            redirectAttributes.addFlashAttribute("successMessage", "Module created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/modules";
    }

    // Section management
    @GetMapping("/sections")
    public String listSections(Model model) {
        model.addAttribute("sections", organizationService.findAllSections());
        model.addAttribute("modules", organizationService.findAllModules());
        model.addAttribute("section", new Section());
        return "admin/sections";
    }

    @PostMapping("/sections")
    public String createSection(@Valid @ModelAttribute Section section, BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fix the validation errors");
            return "redirect:/admin/sections";
        }
        try {
            organizationService.createSection(section);
            redirectAttributes.addFlashAttribute("successMessage", "Section created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/sections";
    }
}