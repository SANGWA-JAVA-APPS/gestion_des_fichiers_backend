package com.magerwa.gestiondesfichiers.service;

import com.magerwa.gestiondesfichiers.entity.Document;
import com.magerwa.gestiondesfichiers.entity.Document.DocumentStatus;
import com.magerwa.gestiondesfichiers.entity.Section;
import com.magerwa.gestiondesfichiers.entity.User;
import com.magerwa.gestiondesfichiers.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DocumentService {
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public Document uploadDocument(MultipartFile file, String title, String description, 
                                 User uploadedBy, Section section) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // Create document entity
        Document document = new Document(
                title,
                description,
                originalFilename,
                filePath.toString(),
                file.getContentType(),
                file.getSize(),
                uploadedBy,
                section
        );
        
        return documentRepository.save(document);
    }
    
    public List<Document> findAll() {
        return documentRepository.findAll();
    }
    
    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }
    
    public List<Document> findBySection(Section section) {
        return documentRepository.findBySection(section);
    }
    
    public List<Document> findBySectionId(Long sectionId) {
        return documentRepository.findBySectionId(sectionId);
    }
    
    public List<Document> findByUploadedBy(User user) {
        return documentRepository.findByUploadedBy(user);
    }
    
    public List<Document> findByStatus(DocumentStatus status) {
        return documentRepository.findByStatus(status);
    }
    
    public List<Document> findActiveDocuments() {
        return documentRepository.findByStatusOrderByUploadDateDesc(DocumentStatus.ACTIVE);
    }
    
    public Document updateDocument(Document document) {
        return documentRepository.save(document);
    }
    
    public void deleteDocument(Long id) {
        Optional<Document> documentOpt = documentRepository.findById(id);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();
            document.setStatus(DocumentStatus.DELETED);
            documentRepository.save(document);
        }
    }
    
    public void archiveDocument(Long id) {
        Optional<Document> documentOpt = documentRepository.findById(id);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();
            document.setStatus(DocumentStatus.ARCHIVED);
            documentRepository.save(document);
        }
    }
    
    public List<Document> searchByTitle(String title) {
        return documentRepository.findByTitleContainingAndStatus(title, DocumentStatus.ACTIVE);
    }
    
    public List<Document> findDocumentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return documentRepository.findByUploadDateBetween(startDate, endDate);
    }
}