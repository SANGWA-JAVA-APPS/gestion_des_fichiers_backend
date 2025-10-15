package com.bar.gestiondesfichier.document.service;

import com.bar.gestiondesfichier.document.model.Document;
import com.bar.gestiondesfichier.document.model.DocumentStatus;
import com.bar.gestiondesfichier.document.repository.DocumentRepository;
import com.bar.gestiondesfichier.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service for managing document expiry and status lifecycle Handles automated
 * checks for expiring and expired documents
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentExpiryService {

    private final DocumentRepository documentRepository;
    private final EmailService emailService;

    /**
     * Check for documents expiring within 2 weeks and send alerts to admin Runs
     * daily at 9:00 AM Cron expression: second minute hour day month weekday
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkExpiringDocuments() {
        log.info("Starting scheduled check for expiring documents.");

        try {
            LocalDate today = LocalDate.now();
            LocalDate twoWeeksFromNow = today.plusWeeks(2);

            // Find active documents expiring within 2 weeks that haven't been alerted
            List<Document> expiringDocuments = documentRepository
                    .findByStatusAndExpiryDateBetweenAndExpiryAlertSentFalse(
                            DocumentStatus.ACTIVE,
                            today,
                            twoWeeksFromNow
                    );

            log.info("Found {} documents expiring within 2 weeks", expiringDocuments.size());

            for (Document document : expiringDocuments) {
                try {
                    // Send alert to admin
                    sendExpiryAlert(document);

                    // Mark alert as sent
                    document.setExpiryAlertSent(true);
                    documentRepository.save(document);

                    log.info("Expiry alert sent for document: '{}' (ID: {}), expires on: {}",
                            document.getFileName(),
                            document.getId(),
                            document.getExpiryDate());
                } catch (Exception e) {
                    log.error("Failed to send expiry alert for document ID: {}",
                            document.getId(), e);
                }
            }

            log.info("Expiry check completed successfully. Alerts sent: {}", expiringDocuments.size());
        } catch (Exception e) {
            log.error("Error during expiry check process", e);
        }
    }

    /**
     * Check expiring documents and send notification every 30 minutes Runs
     * every 30 minutes Cron expression: 0 /30 * * * * (at minute 0 and 30 of
     * every hour)
     */
    @Scheduled(cron = "0 */30 * * * *")
    @Transactional(readOnly = true)
    public void checkAndPrintExpiringDocumentsCount() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate futureDate = today.plusMonths(1); // Check documents expiring within next month

            // Find all documents with expiry date set
            List<Document> expiringDocuments = documentRepository
                    .findByStatusAndExpiryDateBetweenAndExpiryAlertSentFalse(
                            DocumentStatus.ACTIVE,
                            today,
                            futureDate
                    );

            // Print to console
            System.out.println("===========================================");
            System.out.println("EXPIRING DOCUMENTS CHECK - " + java.time.LocalDateTime.now());
            System.out.println("Total expiring documents: " + expiringDocuments.size());
            System.out.println("===========================================");

            log.info("Scheduled check: {} documents expiring within 30 days", expiringDocuments.size());

            // Send email notification if there are expiring documents
            if (!expiringDocuments.isEmpty()) {
                StringBuilder emailBody = new StringBuilder();
                emailBody.append("EXPIRING DOCUMENTS REPORT\n\n");
                emailBody.append(String.format("Total documents expiring within 30 days: %d\n\n", expiringDocuments.size()));
                emailBody.append("Details:\n");
                emailBody.append("========================================\n\n");

                for (Document doc : expiringDocuments) {
                    long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), doc.getExpiryDate());
                    emailBody.append(String.format(
                            "- Document: %s\n"
                            + "  ID: %d\n"
                            + "  Owner: %s\n"
                            + "  Days until expiry: %d\n"
                            + "  Expiry date: %s\n\n",
                            doc.getOriginalFileName() != null ? doc.getOriginalFileName() : doc.getFileName(),
                            doc.getId(),
                            doc.getOwner() != null ? doc.getOwner().getFullName() : "Unknown",
                            daysUntilExpiry,
                            doc.getExpiryDate()
                    ));
                }

                emailBody.append("\nPlease review these documents and take necessary action.\n");

                emailService.sendAdminAlert(
                        String.format("⚠️ %d Document(s) Expiring Soon", expiringDocuments.size()),
                        emailBody.toString()
                );

                log.info("Expiring documents notification sent to admin");
            }
        } catch (Exception e) {
            log.error("Error during scheduled expiring documents count check", e);
            System.err.println("Error checking expiring documents: " + e.getMessage());
        }
    }

    /**
     * Check for expired documents and update their status to EXPIRED Runs daily
     * at 1:00 AM Cron expression: second minute hour day month weekday
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void updateExpiredDocuments() {
        log.info("Starting scheduled update of expired documents...");

        try {
            LocalDate today = LocalDate.now();

            // Find active documents that have expired
            List<Document> expiredDocuments = documentRepository
                    .findByStatusAndExpiryDateBefore(DocumentStatus.ACTIVE, today);

            log.info("Found {} documents that have expired", expiredDocuments.size());

            for (Document document : expiredDocuments) {
                try {
                    document.setStatus(DocumentStatus.EXPIRED);
                    documentRepository.save(document);

                    log.info("Document expired and status updated: '{}' (ID: {}), expired on: {}",
                            document.getFileName(),
                            document.getId(),
                            document.getExpiryDate());
                } catch (Exception e) {
                    log.error("Failed to update status for expired document ID: {}",
                            document.getId(), e);
                }
            }

            log.info("Expired documents update completed. Count: {}", expiredDocuments.size());
        } catch (Exception e) {
            log.error("Error during expired documents update process", e);
        }
    }

    /**
     * Manual method to check and expire documents immediately Can be called via
     * API endpoint if needed
     *
     * @return number of documents that were expired
     */
    @Transactional
    public int expireDocumentsNow() {
        log.info("Manual expiry check initiated");
        LocalDate today = LocalDate.now();

        List<Document> expiredDocuments = documentRepository
                .findByStatusAndExpiryDateBefore(DocumentStatus.ACTIVE, today);

        for (Document document : expiredDocuments) {
            document.setStatus(DocumentStatus.EXPIRED);
            documentRepository.save(document);
        }

        log.info("Manual expiry completed. {} documents expired", expiredDocuments.size());
        return expiredDocuments.size();
    }

    /**
     * Send expiry alert notification via email
     *
     * @param document the document that is expiring
     */
    private void sendExpiryAlert(Document document) {
        // Calculate days until expiry
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), document.getExpiryDate());

        String subject = String.format("Document Expiring Soon: %s", document.getFileName());
        String message = String.format(
                "DOCUMENT EXPIRY ALERT\n\n"
                + "Document Name: %s\n"
                + "Document ID: %s\n"
                + "Original File Name: %s\n"
                + "Owner: %s\n"
                + "Days Until Expiry: %d\n"
                + "Expiry Date: %s\n\n"
                + "Please take necessary action before the document expires.. ",
                document.getFileName(),
                document.getId(),
                document.getOriginalFileName(),
                document.getOwner() != null ? document.getOwner().getFullName() : "Unknown",
                daysUntilExpiry,
                document.getExpiryDate()
        );

        // Log the alert
        log.warn("EXPIRY ALERT: {}", subject);
        log.warn("Alert Details:\n{}", message);

        // Send email notification to admin
        emailService.sendDocumentExpiryAlert(
                document.getFileName(),
                document.getId(),
                document.getOriginalFileName(),
                document.getOwner() != null ? document.getOwner().getFullName() : "Unknown",
                daysUntilExpiry,
                document.getExpiryDate().toString()
        );
    }

    /**
     * Get statistics about document expiry status
     *
     * @return formatted string with expiry statistics
     */
    public String getExpiryStatistics() {
        long activeCount = documentRepository.countByStatus(DocumentStatus.ACTIVE);
        long archivedCount = documentRepository.countByStatus(DocumentStatus.ARCHIVED);
        long expiredCount = documentRepository.countByStatus(DocumentStatus.EXPIRED);

        LocalDate today = LocalDate.now();
        LocalDate twoWeeksFromNow = today.plusWeeks(2);

        List<Document> expiringDocuments = documentRepository
                .findByStatusAndExpiryDateBetweenAndExpiryAlertSentFalse(
                        DocumentStatus.ACTIVE, today, twoWeeksFromNow);

        return String.format(
                "Document Expiry Statistics:\n"
                + "- Active Documents: %d\n"
                + "- Archived Documents: %d\n"
                + "- Expired Documents: %d\n"
                + "- Documents Expiring Soon (within 2 weeks): %d",
                activeCount, archivedCount, expiredCount, expiringDocuments.size()
        );
    }
}
