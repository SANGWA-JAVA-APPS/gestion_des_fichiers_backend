package com.bar.gestiondesfichier.document.service;

import com.bar.gestiondesfichier.document.model.Document;
import com.bar.gestiondesfichier.document.repository.DocumentRepository;
import com.bar.gestiondesfichier.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for handling document upload operations including version management
 */
@Service
@Slf4j
public class DocumentUploadService {

    private final DocumentRepository documentRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadBaseDir;

    public DocumentUploadService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Upload a file to the specified folder within the application context
     * root. Creates the folder if it doesn't exist.
     *
     * @param file The multipart file to upload
     * @param folderName The folder name within the upload directory
     * @return The relative file path (e.g.,
     * "uploads/accord_concession/filename.pdf")
     * @throws IOException If file upload fails
     */
    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        if (folderName == null || folderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Folder name cannot be null or empty");
        }

        // Get original filename and extract extension
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        String fileExtension = extractFileExtension(originalFilename, contentType);

        // Generate unique filename
        String uniqueFileName = generateUniqueFileName(folderName, fileExtension);

        // Create the full upload path: uploadBaseDir/folderName
        Path uploadPath = Paths.get(uploadBaseDir, folderName);

        // Create directory if it doesn't exist
        if (!Files.exists(uploadPath)) {
            log.info("Creating upload directory: {}", uploadPath.toAbsolutePath());
            Files.createDirectories(uploadPath);
        }

        // Full file path
        Path filePath = uploadPath.resolve(uniqueFileName);

        log.info("Uploading file '{}' to: {}", originalFilename, filePath.toAbsolutePath());

        // Copy file to the target location (Replacing existing file with the same name)
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path for database storage
        String relativePath = uploadBaseDir + File.separator + folderName + File.separator + uniqueFileName;
        log.info("File uploaded successfully. Relative path: {}", relativePath);

        return relativePath;
    }

    /**
     * Get the next version number for a document based on its original file
     * name. If no existing document is found, returns "1.0". If existing
     * document is found, increments the minor version (e.g., "1.5" -> "1.6").
     *
     * @param originalFileName The original file name to check for existing
     * versions
     * @return The next version number as a string (e.g., "1.0", "1.1", "2.5")
     */
    public String getNextVersionForDocument(String originalFileName) {
        log.debug("Checking version for document with original file name: {}", originalFileName);

        Optional<Document> existingDocOpt = documentRepository
                .findTopByOriginalFileNameOrderByIdDesc(originalFileName);

        if (existingDocOpt.isEmpty()) {
            log.info("No existing document found with name '{}'. Setting version to 1.0",
                    originalFileName);
            return "1.0";
        }

        Document existingDoc = existingDocOpt.get();
        String currentVersion = existingDoc.getVersion();

        return incrementVersion(currentVersion, originalFileName);
    }

    /**
     * Increment the version number. Parses the current version and increments
     * the minor version. Format: major.minor (e.g., "1.5" -> "1.6")
     *
     * @param currentVersion The current version string to increment
     * @param originalFileName The original file name (used for logging)
     * @return The incremented version string, or "1.0" if parsing fails
     */
    public String incrementVersion(String currentVersion, String originalFileName) {
        if (currentVersion == null || currentVersion.trim().isEmpty()) {
            log.info("Existing document '{}' has no version. Setting new version to 1.0",
                    originalFileName);
            return "1.0";
        }

        try {
            String[] versionParts = currentVersion.split("\\.");

            if (versionParts.length == 0) {
                log.warn("Version '{}' has no parts. Using default '1.0'", currentVersion);
                return "1.0";
            }

            int majorVersion = Integer.parseInt(versionParts[0]);
            int minorVersion = versionParts.length > 1 ? Integer.parseInt(versionParts[1]) : 0;

            // Increment minor version
            minorVersion++;
            String newVersion = majorVersion + "." + minorVersion;

            log.info("Found existing document '{}' with version {}. New version will be: {}",
                    originalFileName, currentVersion, newVersion);

            return newVersion;

        } catch (NumberFormatException e) {
            log.warn("Could not parse version '{}' for document '{}'. Using default '1.0'. Error: {}",
                    currentVersion, originalFileName, e.getMessage());
            return "1.0";
        }
    }

    /**
     * Check if a document with the given original file name already exists.
     *
     * @param originalFileName The original file name to check
     * @return true if a document exists, false otherwise
     */
    public boolean documentExists(String originalFileName) {
        Optional<Document> existingDoc = documentRepository
                .findTopByOriginalFileNameOrderByIdDesc(originalFileName);

        boolean exists = existingDoc.isPresent();
        log.debug("Document with name '{}' exists: {}", originalFileName, exists);

        return exists;
    }

    /**
     * Get the latest document by original file name.
     *
     * @param originalFileName The original file name to search for
     * @return Optional containing the latest document if found, empty otherwise
     */
    public Optional<Document> getLatestDocumentByOriginalFileName(String originalFileName) {
        log.debug("Fetching latest document with original file name: {}", originalFileName);

        return documentRepository.findTopByOriginalFileNameOrderByIdDesc(originalFileName);
    }

    /**
     * Get the current version of the latest document with the given original
     * file name. Returns "1.0" if no document is found.
     *
     * @param originalFileName The original file name to check
     * @return The current version string, or "1.0" if no document exists
     */
    public String getCurrentVersion(String originalFileName) {
        Optional<Document> existingDoc = getLatestDocumentByOriginalFileName(originalFileName);

        if (existingDoc.isEmpty()) {
            log.debug("No document found with name '{}'. Returning default version 1.0",
                    originalFileName);
            return "1.0";
        }

        String version = existingDoc.get().getVersion();
        if (version == null || version.trim().isEmpty()) {
            log.warn("Document '{}' has null or empty version. Returning default 1.0",
                    originalFileName);
            return "1.0";
        }

        log.debug("Current version for document '{}' is: {}", originalFileName, version);
        return version;
    }

    /**
     * Parse version string into major and minor components. Returns an array
     * [major, minor]. If parsing fails, returns [1, 0].
     *
     * @param version The version string to parse (e.g., "2.5")
     * @return Array of [majorVersion, minorVersion]
     */
    public int[] parseVersion(String version) {
        if (version == null || version.trim().isEmpty()) {
            log.warn("Cannot parse null or empty version. Returning [1, 0]");
            return new int[]{1, 0};
        }

        try {
            String[] parts = version.split("\\.");

            if (parts.length == 0) {
                log.warn("Version '{}' has no parts. Returning [1, 0]", version);
                return new int[]{1, 0};
            }

            int major = Integer.parseInt(parts[0]);
            int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            log.debug("Parsed version '{}' to major={}, minor={}", version, major, minor);
            return new int[]{major, minor};

        } catch (NumberFormatException e) {
            log.warn("Failed to parse version '{}'. Returning [1, 0]. Error: {}",
                    version, e.getMessage());
            return new int[]{1, 0};
        }
    }

    /**
     * Build a version string from major and minor version numbers.
     *
     * @param major The major version number
     * @param minor The minor version number
     * @return The formatted version string (e.g., "2.5")
     */
    public String buildVersion(int major, int minor) {
        String version = major + "." + minor;
        log.debug("Built version string: {}", version);
        return version;
    }

    /**
     * Extract file extension from filename or content type. Returns the
     * extension with dot (e.g., ".pdf", ".docx"). Falls back to ".pdf" if
     * extension cannot be determined.
     *
     * @param filename The filename to extract extension from (can be null)
     * @param contentType The content type (can be null)
     * @return The file extension with dot (e.g., ".pdf")
     */
    public String extractFileExtension(String filename, String contentType) {
        // Try to extract from filename first
        if (filename != null && !filename.trim().isEmpty()) {
            int lastDotIndex = filename.lastIndexOf('.');
            if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
                String extension = filename.substring(lastDotIndex);
                log.debug("Extracted extension '{}' from filename '{}'", extension, filename);
                return extension.toLowerCase();
            }
        }

        // Fallback to content type
        if (contentType != null && !contentType.trim().isEmpty()) {
            String extension = getExtensionFromContentType(contentType);
            if (!extension.equals(".pdf")) { // If we found a specific extension
                log.debug("Extracted extension '{}' from content type '{}'", extension, contentType);
                return extension;
            }
        }

        // Default fallback
        log.debug("Could not extract extension from filename '{}' or content type '{}'. Using default '.pdf'",
                filename, contentType);
        return ".pdf";
    }

    /**
     * Get file extension from content type.
     *
     * @param contentType The MIME content type
     * @return The file extension with dot
     */
    private String getExtensionFromContentType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            return ".pdf";
        }

        String type = contentType.toLowerCase().trim();

        // Remove charset if present (e.g., "application/pdf; charset=UTF-8")
        if (type.contains(";")) {
            type = type.substring(0, type.indexOf(";")).trim();
        }

        // Map common content types to extensions
        switch (type) {
            case "application/pdf":
                return ".pdf";
            case "application/msword":
                return ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return ".docx";
            case "application/vnd.ms-excel":
                return ".xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return ".xlsx";
            case "application/vnd.ms-powerpoint":
                return ".ppt";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                return ".pptx";
            case "image/jpeg":
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "text/plain":
                return ".txt";
            case "text/csv":
                return ".csv";
            case "application/zip":
                return ".zip";
            case "application/x-rar-compressed":
                return ".rar";
            default:
                log.debug("Unknown content type '{}', using default .pdf", type);
                return ".pdf";
        }
    }

    /**
     * Generate a unique filename with proper extension.
     *
     * @param prefix The prefix for the filename (e.g., "accord_concession")
     * @param extension The file extension with dot (e.g., ".pdf")
     * @return The unique filename (e.g., "accord_concession_uuid.pdf")
     */
    public String generateUniqueFileName(String prefix, String extension) {
        String uuid = java.util.UUID.randomUUID().toString();
        String filename = prefix + "_" + uuid + extension;
        log.debug("Generated unique filename: {}", filename);
        return filename;
    }

    /**
     * Generate an original filename with proper extension.
     *
     * @param prefix The prefix for the filename (e.g., "Accord_Concession")
     * @param identifier The identifier (e.g., record number)
     * @param extension The file extension with dot (e.g., ".pdf")
     * @return The original filename (e.g.,
     * "Accord_Concession_ACC-2025-001.pdf")
     */
    public String generateOriginalFileName(String prefix, String identifier, String extension) {
        String filename = prefix;
        //                + "_" + identifier + extension;
        log.debug("Generated original filename: {}", filename);
        return filename;
    }

    /**
     * Initialize a Document object with all required properties. Sets file
     * metadata, owner, expiration date (5 years from now), version, and active
     * status.
     *
     * @param uniqueFileName The unique filename stored on disk
     * @param originalFileName The original filename for version tracking
     * @param contentType The MIME content type of the file
     * @param fileSize The file size in bytes
     * @param filePath The relative file path
     * @param owner The Account who owns/created the document
     * @return A fully initialized Document object ready to be saved
     */
    public Document initializeDocument(String uniqueFileName, String originalFileName, String contentType, long fileSize, String filePath, Account owner) {
        log.debug("Initializing document: uniqueFileName={}, originalFileName={}, contentType={}, fileSize={}, filePath={}",
                uniqueFileName, originalFileName, contentType, fileSize, filePath);

        Document document = new Document();

        // Set file metadata
        document.setFileName(uniqueFileName);
        document.setOriginalFileName(originalFileName);
        document.setContentType(contentType);
        document.setFileSize(fileSize);
        document.setFilePath(filePath);

        // Set owner
        document.setOwner(owner);

        // Set expiration date (default: 5 years from now)
        document.setExpirationDate(LocalDateTime.now().plusYears(5));

        // Get and set the next version for this document
        String newVersion = getNextVersionForDocument(originalFileName);
        document.setVersion(newVersion);

        // Set as active
        document.setActive(true);

        log.info("Document initialized with version: {} for file: {}", newVersion, originalFileName);

        return document;
    }

    /**
     * Update an existing document or create a new one from an uploaded file.
     * Reuses core upload/versioning logic and increments version based on original file name.
     *
     * @param existingDocument The existing document to update (can be null)
     * @param file The uploaded file
     * @param folderName The upload folder name
     * @param originalFileName The original file name used for version tracking
     * @param owner The document owner (required when creating a new document)
     * @return Optional containing the updated/created document when file is provided
     * @throws IOException If file upload fails
     */
    public Optional<Document> handleFileUpdate(Document existingDocument, MultipartFile file, String folderName,
                                               String originalFileName, Account owner) throws IOException {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        }

        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Original file name is required for versioning");
        }

        if (existingDocument == null && owner == null) {
            throw new IllegalArgumentException("Document owner is required");
        }

        String filePath = uploadFile(file, folderName);
        String contentType = file.getContentType();
        long fileSize = file.getSize();
        String uniqueFileName = Paths.get(filePath).getFileName().toString();

        Document document;
        if (existingDocument == null) {
            document = initializeDocument(uniqueFileName, originalFileName, contentType, fileSize, filePath, owner);
        } else {
            document = existingDocument;
            document.setFileName(uniqueFileName);
            document.setOriginalFileName(originalFileName);
            document.setContentType(contentType);
            document.setFileSize(fileSize);
            document.setFilePath(filePath);
            document.setVersion(getNextVersionForDocument(originalFileName));
            if (document.getOwner() == null) {
                document.setOwner(owner);
            }
            document.setActive(true);
        }

        return Optional.of(document);
    }
}
