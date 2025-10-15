package com.bar.gestiondesfichier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for sending email notifications
 * Supports both simple text and HTML email messages
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.admin:m.fall@aglgroup.com}")
    private String adminEmail;

    /**
     * Send a simple text email
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param text    email body (plain text)
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    /**
     * Send an HTML email
     *
     * @param to       recipient email address
     * @param subject  email subject
     * @param htmlBody email body (HTML format)
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
        }
    }

    /**
     * Send notification to admin
     *
     * @param subject email subject
     * @param message email body
     */
    @Async
    public void sendAdminAlert(String subject, String message) {
        log.info("Sending admin alert to: {}", adminEmail);
        sendSimpleEmail(adminEmail, subject, message);
    }

    /**
     * Send HTML notification to admin
     *
     * @param subject  email subject
     * @param htmlBody email body (HTML format)
     */
    @Async
    public void sendAdminHtmlAlert(String subject, String htmlBody) {
        log.info("Sending admin HTML alert to: {}", adminEmail);
        sendHtmlEmail(adminEmail, subject, htmlBody);
    }

    /**
     * Send email to multiple recipients
     *
     * @param recipients array of email addresses
     * @param subject    email subject
     * @param text       email body
     */
    @Async
    public void sendEmailToMultiple(String[] recipients, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipients);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email sent successfully to {} recipients", recipients.length);
        } catch (Exception e) {
            log.error("Failed to send email to multiple recipients", e);
        }
    }

    /**
     * Send document expiry alert to admin with formatted HTML
     *
     * @param documentName    name of the document
     * @param documentId      ID of the document
     * @param originalName    original file name
     * @param owner           owner's name
     * @param daysUntilExpiry days until expiration
     * @param expiryDate      expiration date
     */
    @Async
    public void sendDocumentExpiryAlert(String documentName, Long documentId, String originalName,
                                        String owner, long daysUntilExpiry, String expiryDate) {
        String subject = String.format("🔔 Document Expiring Soon: %s", originalName);
        
        String htmlBody = String.format(
            "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "    <style>" +
            "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
            "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
            "        .header { background-color: #f44336; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }" +
            "        .content { background-color: #f9f9f9; padding: 20px; border: 1px solid #ddd; }" +
            "        .info-row { padding: 10px; margin: 5px 0; background-color: white; border-left: 3px solid #f44336; }" +
            "        .label { font-weight: bold; color: #555; }" +
            "        .value { color: #000; }" +
            "        .warning { background-color: #fff3cd; border: 1px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
            "        .footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }" +
            "    </style>" +
            "</head>" +
            "<body>" +
            "    <div class='container'>" +
            "        <div class='header'>" +
            "            <h2>⚠️ Document Expiry Alert</h2>" +
            "        </div>" +
            "        <div class='content'>" +
            "            <div class='warning'>" +
            "                <strong>⏰ Urgent:</strong> A document in your system is expiring in <strong>%d days</strong>." +
            "            </div>" +
            "            <div class='info-row'>" +
            "                <span class='label'>Document ID:</span> <span class='value'>%d</span>" +
            "            </div>" +
            "            <div class='info-row'>" +
            "                <span class='label'>File Name:</span> <span class='value'>%s</span>" +
            "            </div>" +
            "            <div class='info-row'>" +
            "                <span class='label'>Original Name:</span> <span class='value'>%s</span>" +
            "            </div>" +
            "            <div class='info-row'>" +
            "                <span class='label'>Owner:</span> <span class='value'>%s</span>" +
            "            </div>" +
            "            <div class='info-row'>" +
            "                <span class='label'>Days Until Expiry:</span> <span class='value' style='color: #f44336; font-weight: bold;'>%d days</span>" +
            "            </div>" +
            "            <div class='info-row'>" +
            "                <span class='label'>Expiry Date:</span> <span class='value'>%s</span>" +
            "            </div>" +
            "            <div style='margin-top: 20px; padding: 15px; background-color: #e3f2fd; border-left: 3px solid #2196F3;'>" +
            "                <strong>Action Required:</strong> Please review and take necessary action before the document expires." +
            "            </div>" +
            "        </div>" +
            "        <div class='footer'>" +
            "            <p>This is an automated notification from the Document Management System.</p>" +
            "            <p>© 2025 Gestion des Fichiers</p>" +
            "        </div>" +
            "    </div>" +
            "</body>" +
            "</html>",
            daysUntilExpiry,
            documentId,
            documentName,
            originalName,
            owner,
            daysUntilExpiry,
            expiryDate
        );

        sendAdminHtmlAlert(subject, htmlBody);
        log.info("Document expiry alert sent to admin for document ID: {}", documentId);
    }
}
