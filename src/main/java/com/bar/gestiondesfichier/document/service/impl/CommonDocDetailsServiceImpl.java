package com.bar.gestiondesfichier.document.service.impl;

import com.bar.gestiondesfichier.document.dto.CommonDocDetailsRequestDTO;
import com.bar.gestiondesfichier.document.model.CommonDocDetails;
import com.bar.gestiondesfichier.document.model.Document;
import com.bar.gestiondesfichier.document.model.DocStatus;
import com.bar.gestiondesfichier.document.model.SectionCategory;
import com.bar.gestiondesfichier.document.projection.CommonDocDetailsProjection;
import com.bar.gestiondesfichier.document.repository.CommonDocDetailsRepository;
import com.bar.gestiondesfichier.document.repository.DocumentRepository;
import com.bar.gestiondesfichier.document.repository.DocStatusRepository;
import com.bar.gestiondesfichier.document.repository.SectionCategoryRepository;
import com.bar.gestiondesfichier.document.service.CommonDocDetailsService;
import com.bar.gestiondesfichier.document.service.DocumentUploadService;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.repository.AccountRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;

@Service
@Slf4j
public class CommonDocDetailsServiceImpl implements CommonDocDetailsService {

    private final CommonDocDetailsRepository repo;
    private final SectionCategoryRepository sectionCategoryRepo;
    private final DocumentUploadService documentUploadService;
    private final DocumentRepository documentRepository;
    private final AccountRepository accountRepository;
    private final DocStatusRepository docStatusRepository;
    private static final String DOCUMENT_FOLDER = "common_doc_details";

    public CommonDocDetailsServiceImpl(
            CommonDocDetailsRepository repo,
            SectionCategoryRepository sectionCategoryRepo,
            DocumentUploadService documentUploadService,
            DocumentRepository documentRepository,
            AccountRepository accountRepository,
            DocStatusRepository docStatusRepository
    ) {
        this.repo = repo;
        this.sectionCategoryRepo = sectionCategoryRepo;
        this.documentUploadService = documentUploadService;
        this.documentRepository = documentRepository;
        this.accountRepository = accountRepository;
        this.docStatusRepository = docStatusRepository;
    }

    @Override
    @Transactional
    public CommonDocDetailsProjection createCommonDocDetails(
            CommonDocDetailsRequestDTO request,
            MultipartFile file
    ) {
        validateRequest(request);
        
        if (repo.existsByReference(request.getReference())) {
            throw new IllegalArgumentException("Document with this reference already exists");
        }

        CommonDocDetails doc = new CommonDocDetails();
        applyRequest(doc, request);
        
        if (file != null && !file.isEmpty()) {
            handleFileUpload(doc, file, request.getReference());
        }
        
        CommonDocDetails saved = repo.save(doc);
        return getProjectionById(saved.getId());
    }

    @Override
    public CommonDocDetailsProjection getCommonDocDetailsById(Long id) {
        return getProjectionById(id);
    }

    @Override
    @Transactional
    public CommonDocDetailsProjection updateCommonDocDetails(
            Long id,
            CommonDocDetailsRequestDTO request,
            MultipartFile file
    ) {
        CommonDocDetails doc = findByIdOrThrow(id);
        applyRequest(doc, request);
        
        if (file != null && !file.isEmpty()) {
            handleFileUpdate(doc, file, request.getReference());
        }
        
        CommonDocDetails saved = repo.save(doc);
        return getProjectionById(saved.getId());
    }

    @Override
    @Transactional
    public void deleteCommonDocDetails(Long id) {
        CommonDocDetails doc = findByIdOrThrow(id);
        doc.setActive(false);
        repo.save(doc);
    }

    @Override
    public Page<CommonDocDetailsProjection> getCommonDocDetails(
            String reference,
            String status,
            Long sectionCategoryId,
            String sectionCategoryCode,
            Long ownerId,
            String search,
            Pageable pageable
    ) {
        if (search != null && !search.trim().isEmpty()) {
            return repo.search(search.trim(), ownerId, pageable);
        } else if (status != null) {
            Long statusId = Long.parseLong(status);
            return repo.findByStatus(statusId, ownerId, pageable);
        } else if (sectionCategoryId != null) {
            return repo.findBySection(sectionCategoryId, ownerId, pageable);
        } else {
            return repo.findAllActive(ownerId, pageable);
        }
    }

    // ------------------------
    // Internal helpers
    // ------------------------

    private CommonDocDetails findByIdOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
    }

    private CommonDocDetailsProjection getProjectionById(Long id) {
        return repo.findByIdProjection(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
    }

    private void validateRequest(CommonDocDetailsRequestDTO request) {
        if (request.getReference() == null || request.getReference().isBlank()) {
            throw new IllegalArgumentException("Reference is required");
        }
        if (request.getDoneById() == null) {
            throw new IllegalArgumentException("DoneBy (Account) is required");
        }
        if (request.getSectionCategoryId() == null) {
            throw new IllegalArgumentException("Section category is required");
        }
    }

    private void applyRequest(
            CommonDocDetails doc,
            CommonDocDetailsRequestDTO request
    ) {
        doc.setReference(request.getReference());
        doc.setDescription(request.getDescription());
        doc.setDateTime(request.getDateTime());

        doc.setExpirationDate(request.getExpirationDate());
        doc.setSectionCategory(resolveSectionCategory(request.getSectionCategoryId()));
        doc.setDoneBy(resolveAccount(request.getDoneById()));
        
        if (request.getStatusId() != null) {
            doc.setStatus(resolveDocStatus(request.getStatusId()));
        }
    }

    private void handleFileUpload(CommonDocDetails doc, MultipartFile file, String reference) {
        try {
            String filePath = documentUploadService.uploadFile(file, DOCUMENT_FOLDER);
            String contentType = file.getContentType();
            long fileSize = file.getSize();
            String fileExtension = documentUploadService.extractFileExtension(
                    file.getOriginalFilename(), contentType);
            String uniqueFileName = Paths.get(filePath).getFileName().toString();
            String originalFileName = documentUploadService.generateOriginalFileName(
                    file.getOriginalFilename(), reference, fileExtension);

            Document document = documentUploadService.initializeDocument(
                    uniqueFileName, originalFileName, contentType, fileSize, filePath, doc.getDoneBy());
            
            Document savedDocument = documentRepository.save(document);
            doc.setDocument(savedDocument);
            
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    private void handleFileUpdate(CommonDocDetails doc, MultipartFile file, String reference) {
        try {
            String extension = documentUploadService.extractFileExtension(
                    file.getOriginalFilename(), file.getContentType());
            String originalFileName = documentUploadService.generateOriginalFileName(
                    file.getOriginalFilename(), reference, extension);

            Document updatedDocument = documentUploadService
                    .handleFileUpdate(doc.getDocument(), file, DOCUMENT_FOLDER, originalFileName, doc.getDoneBy())
                    .map(documentRepository::save)
                    .orElse(null);

            if (updatedDocument != null) {
                doc.setDocument(updatedDocument);
            }
        } catch (IOException e) {
            log.error("File update failed", e);
            throw new RuntimeException("Failed to update file: " + e.getMessage());
        }
    }

    private SectionCategory resolveSectionCategory(Long sectionCategoryId) {
        return sectionCategoryRepo.findById(sectionCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Section category not found"));
    }

    private Account resolveAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private DocStatus resolveDocStatus(Long statusId) {
        return docStatusRepository.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Status not found"));
    }
}