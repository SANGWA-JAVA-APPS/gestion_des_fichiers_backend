package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.config.CurrentUser;
import com.bar.gestiondesfichier.document.dto.AccordConcessionRequest;
import com.bar.gestiondesfichier.document.model.AccordConcession;
import com.bar.gestiondesfichier.document.model.Document;
import com.bar.gestiondesfichier.document.projection.AccordConcessionProjection;

import com.bar.gestiondesfichier.document.repository.AccordConcessionRepository;
import com.bar.gestiondesfichier.document.repository.DocStatusRepository;
import com.bar.gestiondesfichier.document.repository.DocumentRepository;
import com.bar.gestiondesfichier.document.repository.SectionCategoryRepository;
import com.bar.gestiondesfichier.document.service.DocumentUploadService;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.repository.AccountRepository;
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
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST controller for Concession Agreement management with 20-record default
 * pagination
 */
@RestController
@RequestMapping("/api/document/accord-concession")
@DocumentControllerCors
@Tag(name = "Document", description = "Concession Agreement CRUD operations with pagination")
public class AccordConcessionController {

    private static final Logger log = LoggerFactory.getLogger(AccordConcessionController.class);

    private final AccordConcessionRepository accordConcessionRepository;
    private final DocumentRepository documentRepository;
    private final AccountRepository accountRepository;
    private final DocumentUploadService documentUploadService;
    private  final DocStatusRepository docStatusRepository;
    private final SectionCategoryRepository sectionCategoryRepository;
private final CurrentUser  currentUser;
    private static final String DOCUMENT_FOLDER = "accord_concession";
    public AccordConcessionController(
            AccordConcessionRepository accordConcessionRepository,
            DocumentRepository documentRepository,
            AccountRepository accountRepository,
            DocumentUploadService documentUploadService, DocStatusRepository docStatusRepository, SectionCategoryRepository sectionCategoryRepository, CurrentUser currentUser) {
        this.accordConcessionRepository = accordConcessionRepository;
        this.documentRepository = documentRepository;
        this.accountRepository = accountRepository;
        this.documentUploadService = documentUploadService;
        this.docStatusRepository = docStatusRepository;
        this.sectionCategoryRepository = sectionCategoryRepository;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "Get all concession agreements", description = "Retrieve paginated list of concession agreements with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Concession agreements retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")})
    public ResponseEntity<Page<AccordConcessionProjection>> getAllAccordConcession(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "numeroAccord") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by document ID") @RequestParam(required = false) Long documentId,
            @Parameter(description = "Filter by section category ID") @RequestParam(required = false) Long sectionCategoryId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving concession agreements!!!!!!!!!!+++++++++++++ - page: {}, size: {}, sort: {} {}, statusId: {}, search: {}",
                    page, size, sort, direction, statusId, search);
Long ownerId=currentUser.isUser()?currentUser.getAccountId():null;
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<AccordConcessionProjection> accordConcessions;

            if (search != null && !search.trim().isEmpty()) {
                accordConcessions = accordConcessionRepository.findByActiveTrueAndNumeroAccordOrObjetConcessionContainingWithDetails(search, pageable);
            } else if (statusId != null) {
                accordConcessions = accordConcessionRepository.findByActiveTrueAndStatusIdWithDetails(statusId, pageable);
            } else if (sectionCategoryId != null) {
                accordConcessions = accordConcessionRepository.findByActiveTrueAndSectionCategoryIdWithDetails(sectionCategoryId, pageable);
            } else if (documentId != null) {
                accordConcessions = accordConcessionRepository.findByActiveTrueAndDocumentIdWithDetails(documentId, pageable);
            } else {
                accordConcessions = accordConcessionRepository.findAllActiveWithDetails(ownerId,pageable);
            }

            return ResponseEntity.ok(accordConcessions);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for concession agreement retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving concession agreements", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get concession agreement by ID", description = "Retrieve a specific concession agreement record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Concession agreement retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Concession agreement not found or invalid ID")})
    public ResponseEntity<Map<String, Object>> getAccordConcessionById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid concession agreement ID");
            }

            log.info("Retrieving concession agreement by ID: {}", id);
            Optional<AccordConcession> accordConcession = accordConcessionRepository.findByIdAndActiveTrue(id);

            if (accordConcession.isPresent()) {
                return ResponseUtil.success(accordConcession.get(), "Concession agreement retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Concession agreement not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving concession agreement with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve concession agreement: " + e.getMessage());
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @Transactional
    public ResponseEntity<Map<String, Object>> createAccordConcession(
            @RequestPart("accordConcession") AccordConcessionRequest request,
            @RequestPart("file") MultipartFile file) {

        try {
            // --- Validate required fields ---
            if (request.getNumeroAccord() == null || request.getNumeroAccord().trim().isEmpty()) {
                return ResponseUtil.badRequest("Numero accord is required");
            }
            if (file == null || file.isEmpty()) {
                return ResponseUtil.badRequest("Document file is required");
            }

            // --- Resolve account ---
            Long ownerId = request.getDoneById() != null ? request.getDoneById() : currentUser.getAccountId();
            Account owner = accountRepository.findById(ownerId)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + ownerId));

            // --- Handle file upload ---
            String filePath = documentUploadService.uploadFile(file, DOCUMENT_FOLDER);
            String contentType = file.getContentType();
            long fileSize = file.getSize();
            String extension = documentUploadService.extractFileExtension(file.getOriginalFilename(), contentType);
            String uniqueFileName = Paths.get(filePath).getFileName().toString();
            String originalFileName = documentUploadService.generateOriginalFileName(file.getOriginalFilename(), request.getNumeroAccord(), extension);

            Document document = documentUploadService.initializeDocument(uniqueFileName, originalFileName, contentType, fileSize, filePath, owner);
            Document savedDocument = documentRepository.save(document);

            // --- Build AccordConcession entity ---
            AccordConcession accord = new AccordConcession();
            accord.setNumeroAccord(trim(request.getNumeroAccord()));
            accord.setContratConcession(trim(request.getContratConcession()));
            accord.setObjetConcession(trim(request.getObjetConcession()));
            accord.setConcessionnaire(trim(request.getConcessionnaire()));
            accord.setConditionsFinancieres(trim(request.getConditionsFinancieres()));
            accord.setEmplacement(trim(request.getEmplacement()));
            accord.setCoordonneesGps(trim(request.getCoordonneesGps()));
            accord.setRapportTransfertGestion(trim(request.getRapportTransfertGestion()));
            accord.setDureeAnnees(request.getDureeAnnees());
            accord.setDateDebutConcession(request.getDateDebutConcession());
            accord.setDateFinConcession(request.getDateFinConcession());
            accord.setDocument(savedDocument);
            accord.setActive(true);
            accord.setDoneBy(owner);

            // --- Resolve relationships ---
            if (request.getSectionCategoryId() != null) {
                accord.setSectionCategory(
                        sectionCategoryRepository.findById(request.getSectionCategoryId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid SectionCategory ID"))
                );
            }
            if (request.getStatusId() != null) {
                accord.setStatus(
                        docStatusRepository.findById(request.getStatusId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Status ID"))
                );
            }

            AccordConcession savedAccord = accordConcessionRepository.save(accord);

            return ResponseUtil.success(savedAccord, "Concession agreement created successfully");

        } catch (Exception e) {
            log.error("Error creating concession agreement", e);
            return ResponseUtil.badRequest("Failed to create concession agreement: " + e.getMessage());
        }
    }


    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @Transactional
    public ResponseEntity<Map<String, Object>> updateAccordConcessionWithFile(
            @PathVariable Long id,
            @RequestPart("accordConcession") AccordConcessionRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            AccordConcession existing = accordConcessionRepository.findByIdAndActiveTrue(id)
                    .orElseThrow(() -> new IllegalArgumentException("Concession agreement not found with ID: " + id));

            // --- Update fields ---
            if (request.getNumeroAccord() != null) existing.setNumeroAccord(trim(request.getNumeroAccord()));
            if (request.getContratConcession() != null) existing.setContratConcession(trim(request.getContratConcession()));
            if (request.getObjetConcession() != null) existing.setObjetConcession(trim(request.getObjetConcession()));
            if (request.getConcessionnaire() != null) existing.setConcessionnaire(trim(request.getConcessionnaire()));
            if (request.getConditionsFinancieres() != null) existing.setConditionsFinancieres(trim(request.getConditionsFinancieres()));
            if (request.getEmplacement() != null) existing.setEmplacement(trim(request.getEmplacement()));
            if (request.getCoordonneesGps() != null) existing.setCoordonneesGps(trim(request.getCoordonneesGps()));
            if (request.getRapportTransfertGestion() != null) existing.setRapportTransfertGestion(trim(request.getRapportTransfertGestion()));
            if (request.getDureeAnnees() != null) existing.setDureeAnnees(request.getDureeAnnees());
            if (request.getDateDebutConcession() != null) existing.setDateDebutConcession(request.getDateDebutConcession());
            if (request.getDateFinConcession() != null) existing.setDateFinConcession(request.getDateFinConcession());

            // --- Update relationships ---
            if (request.getSectionCategoryId() != null) {
                existing.setSectionCategory(
                        sectionCategoryRepository.findById(request.getSectionCategoryId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid SectionCategory ID"))
                );
            }
            if (request.getStatusId() != null) {
                existing.setStatus(
                        docStatusRepository.findById(request.getStatusId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Status ID"))
                );
            }

            // --- Handle optional file upload ---
            String message = "Concession agreement updated successfully";
            if (file != null && !file.isEmpty()) {
                String extension = documentUploadService.extractFileExtension(file.getOriginalFilename(), file.getContentType());
                String originalFileName = documentUploadService.generateOriginalFileName(file.getOriginalFilename(), existing.getNumeroAccord(), extension);

                Document updatedDocument = documentUploadService
                        .handleFileUpdate(existing.getDocument(), file, DOCUMENT_FOLDER, originalFileName, existing.getDoneBy())
                        .map(documentRepository::save)
                        .orElse(null);

                if (updatedDocument != null) {
                    existing.setDocument(updatedDocument);
                    message = "Concession agreement updated successfully. Document version upgraded to " + updatedDocument.getVersion();
                }
            }

            AccordConcession saved = accordConcessionRepository.save(existing);
            return ResponseUtil.success(saved, message);

        } catch (Exception e) {
            log.error("Error updating concession agreement with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update concession agreement: " + e.getMessage());
        }
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Delete concession agreement", description = "Soft delete a concession agreement record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Concession agreement deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Concession agreement not found")
    })
    public ResponseEntity<Map<String, Object>> deleteAccordConcession(@PathVariable Long id) {
        try {
            log.info("Deleting concession agreement with ID: {}", id);

            Optional<AccordConcession> accordConcessionOpt = accordConcessionRepository.findByIdAndActiveTrue(id);
            if (accordConcessionOpt.isEmpty()) {
                return ResponseUtil.badRequest("Concession agreement not found with ID: " + id);
            }

            AccordConcession accordConcession = accordConcessionOpt.get();
            accordConcession.setActive(false);
            accordConcessionRepository.save(accordConcession);

            return ResponseUtil.success(null, "Concession agreement deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting concession agreement with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete concession agreement: " + e.getMessage());
        }
    }
    private String trim(String v) {
        return v == null ? null : v.trim();
    }

    private void applyAccordConcessionUpdates(AccordConcession existing, AccordConcession updates) {
        if (updates == null) {
            return;
        }

        // ---------- STRING FIELDS (normalized) ----------
        if (updates.getContratConcession() != null) {
            existing.setContratConcession(trim(updates.getContratConcession()));
        }
        if (updates.getNumeroAccord() != null) {
            existing.setNumeroAccord(trim(updates.getNumeroAccord()));
        }
        if (updates.getObjetConcession() != null) {
            existing.setObjetConcession(trim(updates.getObjetConcession()));
        }
        if (updates.getConcessionnaire() != null) {
            existing.setConcessionnaire(trim(updates.getConcessionnaire()));
        }
        if (updates.getConditionsFinancieres() != null) {
            existing.setConditionsFinancieres(trim(updates.getConditionsFinancieres()));
        }
        if (updates.getEmplacement() != null) {
            existing.setEmplacement(trim(updates.getEmplacement()));
        }
        if (updates.getCoordonneesGps() != null) {
            existing.setCoordonneesGps(trim(updates.getCoordonneesGps()));
        }
        if (updates.getRapportTransfertGestion() != null) {
            existing.setRapportTransfertGestion(trim(updates.getRapportTransfertGestion()));
        }

        // ---------- NUMBERS ----------
        if (updates.getDureeAnnees() != null) {
            existing.setDureeAnnees(updates.getDureeAnnees());
        }

        // ---------- DATES ----------
        if (updates.getDateDebutConcession() != null) {
            existing.setDateDebutConcession(updates.getDateDebutConcession());
        }
        if (updates.getDateFinConcession() != null) {
            existing.setDateFinConcession(updates.getDateFinConcession());
        }

        // ---------- RELATIONS ----------
        if (updates.getSectionCategory() != null && updates.getSectionCategory().getId() != null) {
            existing.setSectionCategory(updates.getSectionCategory());
        }

        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }
    }

}
