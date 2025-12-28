package com.bar.gestiondesfichier.location.controller;

import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.location.dto.SectionMapper;
import com.bar.gestiondesfichier.location.dto.SectionRequestDTO;
import com.bar.gestiondesfichier.location.dto.SectionResponseDTO;
import com.bar.gestiondesfichier.location.model.Module;
import com.bar.gestiondesfichier.location.model.Section;
import com.bar.gestiondesfichier.location.repository.ModuleRepository;
import com.bar.gestiondesfichier.location.repository.SectionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/api/location/sections")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8104"})
@Tag(name = "Section Management", description = "Section CRUD operations with pagination")
public class SectionController {

    private static final Logger log = LoggerFactory.getLogger(SectionController.class);
    private final SectionRepository sectionRepository;
    private final ModuleRepository moduleRepository;

    public SectionController(SectionRepository sectionRepository, ModuleRepository moduleRepository) {
        this.sectionRepository = sectionRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    @Operation(summary = "Get all sections", description = "Retrieve paginated list of active sections")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sections retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<Map<String, Object>> getAllSections(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        try {
            log.info("Retrieving sections - page: {}, size: {}, sort: {} {}", page, size, sort, direction);
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<Section> sections = sectionRepository.findByActiveTrue(pageable);

            Page<SectionResponseDTO> dtoPage = sections.map(SectionMapper::toResponseDTO);
            return ResponseUtil.successWithPagination(dtoPage);

        } catch (Exception e) {
            log.error("Error retrieving sections", e);
            return ResponseUtil.badRequest("Failed to retrieve sections: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get section by ID", description = "Retrieve a specific section by ID")
    public ResponseEntity<Map<String, Object>> getSectionById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) return ResponseUtil.badRequest("Invalid section ID");

            Optional<Section> section = sectionRepository.findByIdAndActiveTrue(id);
            return section
                    .map(s -> ResponseUtil.success(SectionMapper.toResponseDTO(s), "Section retrieved successfully"))
                    .orElseGet(() -> ResponseUtil.badRequest("Section not found with ID: " + id));

        } catch (Exception e) {
            log.error("Error retrieving section with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve section: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create section", description = "Create a new section")
    public ResponseEntity<Map<String, Object>> createSection(@RequestBody SectionRequestDTO dto) {
        try {
            log.info("Creating section: {}", dto.getName());

            if (dto.getName() == null || dto.getName().trim().isEmpty())
                return ResponseUtil.badRequest("Section name is required");

            if (dto.getModuleId() == null || dto.getModuleId() <= 0)
                return ResponseUtil.badRequest("Valid module ID is required");

            Optional<Module> moduleOpt = moduleRepository.findByIdAndActiveTrue(dto.getModuleId());
            if (moduleOpt.isEmpty())
                return ResponseUtil.badRequest("Module not found with ID: " + dto.getModuleId());

            Section section = SectionMapper.toEntity(dto, moduleOpt.get());
            section.setActive(true);

            Section savedSection = sectionRepository.save(section);
            return ResponseUtil.success(SectionMapper.toResponseDTO(savedSection), "Section created successfully");

        } catch (Exception e) {
            log.error("Error creating section", e);
            return ResponseUtil.badRequest("Failed to create section: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update section", description = "Update an existing section")
    public ResponseEntity<Map<String, Object>> updateSection(@PathVariable Long id, @RequestBody SectionRequestDTO dto) {
        try {
            if (id == null || id <= 0) return ResponseUtil.badRequest("Invalid section ID");
            if (dto.getName() == null || dto.getName().trim().isEmpty())
                return ResponseUtil.badRequest("Section name is required");

            Optional<Section> existingSection = sectionRepository.findByIdAndActiveTrue(id);
            if (existingSection.isEmpty())
                return ResponseUtil.badRequest("Section not found with ID: " + id);

            Section section = existingSection.get();
            Module module = section.getModule();

            if (dto.getModuleId() != null && dto.getModuleId() > 0) {
                Optional<Module> moduleOpt = moduleRepository.findByIdAndActiveTrue(dto.getModuleId());
                if (moduleOpt.isEmpty())
                    return ResponseUtil.badRequest("Module not found with ID: " + dto.getModuleId());
                module = moduleOpt.get();
            }

            // Update entity with DTO values
            SectionMapper.updateEntity(dto, section, module);

            Section savedSection = sectionRepository.save(section);
            return ResponseUtil.success(SectionMapper.toResponseDTO(savedSection), "Section updated successfully");

        } catch (Exception e) {
            log.error("Error updating section with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update section: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete section", description = "Soft delete a section")
    public ResponseEntity<Map<String, Object>> deleteSection(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) return ResponseUtil.badRequest("Invalid section ID");

            Optional<Section> section = sectionRepository.findByIdAndActiveTrue(id);
            if (section.isEmpty()) return ResponseUtil.badRequest("Section not found with ID: " + id);

            Section s = section.get();
            s.setActive(false);
            sectionRepository.save(s);

            return ResponseUtil.success(null, "Section deleted successfully");

        } catch (Exception e) {
            log.error("Error deleting section with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete section: " + e.getMessage());
        }
    }
}
