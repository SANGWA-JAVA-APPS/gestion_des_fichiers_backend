# Email Notification Implementation - Quick Start

## ✅ What Was Done

### 1. Created EmailService
**Location:** `src/main/java/com/bar/gestiondesfichier/service/EmailService.java`
- Sends email notifications
- Async processing (non-blocking)
- HTML email support
- Dedicated method for document expiry alerts

### 2. Updated application.properties
**Added:**
```properties
app.email.admin=m.fall@aglgroup.com
```

### 3. Updated DocumentExpiryService
**Changes:**
- Injected EmailService
- **30-minute cron job** now sends email to m.fall@aglgroup.com
- Sends summary of ALL expiring documents (within 30 days)
- Individual HTML alerts for documents expiring within 2 weeks

### 4. Enabled Async Support
**Updated:** `GestiondesfichierApplication.java`
- Added `@EnableAsync` annotation

---

## 📧 Email Notifications

### Every 30 Minutes (If documents are expiring)
**To:** m.fall@aglgroup.com  
**Subject:** ⚠️ X Document(s) Expiring Soon  
**Content:** List of all documents expiring within 30 days

### Daily at 9:00 AM
**To:** m.fall@aglgroup.com  
**Subject:** 🔔 Document Expiring Soon: [Document Name]  
**Content:** HTML formatted individual alerts for documents expiring within 2 weeks

---

## 🚀 How to Test

### Method 1: Run Application and Wait
1. Start the Spring Boot application
2. Wait for the next 30-minute mark (10:00, 10:30, 11:00, etc.)
3. Check console logs for:
   ```
   EXPIRING DOCUMENTS CHECK - 2025-10-14T10:30:00
   Expiring documents notification sent to admin
   Email sent successfully to: m.fall@aglgroup.com
   ```

### Method 2: Create Test Endpoint
Add this to a controller:
```java
@Autowired
private EmailService emailService;

@GetMapping("/test-email")
public String testEmail() {
    emailService.sendAdminAlert(
        "Test Email", 
        "This is a test from Document Management System"
    );
    return "Email sent!";
}
```

Then access: `http://localhost:8104/api/test-email`

### Method 3: Add Test Documents
Insert documents with near expiry dates:
```sql
INSERT INTO files (file_name, original_file_name, content_type, file_size, 
                   file_path, owner_id, expiration_date, version, active, 
                   created_at, updated_at, status, expiry_alert_sent, expiry_date)
VALUES ('test_doc.pdf', 'Test_Document.pdf', 'application/pdf', 102400,
        '/uploads/test/test.pdf', 1, DATE_ADD(NOW(), INTERVAL 5 DAY), 
        '1.0', TRUE, NOW(), NOW(), 'ACTIVE', FALSE, DATE_ADD(CURDATE(), INTERVAL 5 DAY));
```

---

## 📋 Configuration

### Change Admin Email
Edit `application.properties`:
```properties
app.email.admin=newemail@example.com
```

### Add Multiple Recipients
Modify `EmailService.sendAdminAlert()` to use:
```java
sendEmailToMultiple(
    new String[]{"m.fall@aglgroup.com", "admin2@aglgroup.com"}, 
    subject, 
    message
);
```

---

## 🐛 Troubleshooting

### No Emails Sent?
Check logs:
```bash
tail -f logs/application.log | grep -i "email\|mail"
```

Look for:
- "Email sent successfully to: m.fall@aglgroup.com" ✅
- "Failed to send email" ❌

### Email Goes to Spam?
- Check spam folder first
- Verify SPF/DKIM records for codeguru-pro.com
- Ensure proper From address (info@codeguru-pro.com)

### Scheduled Job Not Running?
Check for:
```
EXPIRING DOCUMENTS CHECK - [timestamp]
```
in console output every 30 minutes

---

## ✅ Status: READY

All code is implemented and ready to use. Just restart the application and the email notifications will start automatically!

**Email will be sent to:** m.fall@aglgroup.com  
**Frequency:** Every 30 minutes (if documents are expiring)

---

## 📚 Full Documentation

See `EMAIL_NOTIFICATION_SYSTEM.md` for complete details including:
- Email templates
- HTML formatting
- All scheduled jobs
- Advanced configuration
- Future enhancements
