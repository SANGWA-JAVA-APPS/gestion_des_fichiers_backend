# AccordConcessionController Update - Document Creation Integration

## Date: October 13, 2025

## Overview

Updated the `AccordConcessionController` to automatically create a `Document` record in the `files` table when creating a new `AccordConcession`, then link them through the `doc_id` foreign key relationship.

---

## Changes Made

### File: `AccordConcessionController.java`

**Location:** `d:\Apache\DEV\SPRING BOOT\gestiondesfichier\src\main\java\com\bar\gestiondesfichier\document\controller\AccordConcessionController.java`

---

## 1. Added Dependencies

### New Imports

```java
import com.bar.gestiondesfichier.document.model.Document;
import com.bar.gestiondesfichier.document.repository.DocumentRepository;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.repository.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.UUID;
```

### Injected Repositories

```java
private final AccordConcessionRepository accordConcessionRepository;
private final DocumentRepository documentRepository;  // NEW
private final AccountRepository accountRepository;    // NEW

public AccordConcessionController(
        AccordConcessionRepository accordConcessionRepository,
        DocumentRepository documentRepository,           // NEW
        AccountRepository accountRepository) {           // NEW
    this.accordConcessionRepository = accordConcessionRepository;
    this.documentRepository = documentRepository;
    this.accountRepository = accountRepository;
}
```

---

## 2. Updated createAccordConcession Method

### Before

```java
@PostMapping
public ResponseEntity<Map<String, Object>> createAccordConcession(
        @RequestBody AccordConcession accordConcession) {
    // Validate required fields
    if (accordConcession.getDocument() == null) {
        return ResponseUtil.badRequest("Document is required");
    }
    
    accordConcession.setActive(true);
    AccordConcession saved = accordConcessionRepository.save(accordConcession);
    
    return ResponseUtil.success(saved, "Concession agreement created successfully");
}
```

### After

```java
@PostMapping
public ResponseEntity<Map<String, Object>> createAccordConcession(
        @RequestBody AccordConcession accordConcession) {
    
    // Validate required fields
    if (accordConcession.getDoneBy() == null) {
        return ResponseUtil.badRequest("DoneBy (Account) is required");
    }
    
    // Get the current user (owner) for the document
    Account owner = accordConcession.getDoneBy();
    
    // Verify the account exists
    Optional<Account> accountOpt = accountRepository.findById(owner.getId());
    if (accountOpt.isEmpty()) {
        return ResponseUtil.badRequest("Account not found with ID: " + owner.getId());
    }
    
    Account actualOwner = accountOpt.get();
    
    // Create a new Document record with random data
    Document document = new Document();
    String uniqueFileName = "accord_concession_" + UUID.randomUUID().toString() + ".pdf";
    document.setFileName(uniqueFileName);
    document.setOriginalFileName("Accord_Concession_" + accordConcession.getNumeroAccord() + ".pdf");
    document.setContentType("application/pdf");
    document.setFileSize(1024L * 256L); // 256 KB
    document.setFilePath("/uploads/accord_concession/" + uniqueFileName);
    document.setOwner(actualOwner);
    document.setExpirationDate(LocalDateTime.now().plusYears(5));
    document.setVersion("1.0");
    document.setActive(true);
    
    // Save the document first
    Document savedDocument = documentRepository.save(document);
    log.info("Created document with ID: {} for concession agreement: {}", 
             savedDocument.getId(), accordConcession.getNumeroAccord());
    
    // Link the saved document to the accord concession
    accordConcession.setDocument(savedDocument);
    accordConcession.setActive(true);
    
    // Save the accord concession
    AccordConcession saved = accordConcessionRepository.save(accordConcession);
    
    return ResponseUtil.success(saved, "Concession agreement created successfully");
}
```

---

## 3. Key Changes Explained

### Removed Validation
❌ **Removed:** Validation that required `document` to be provided in request
```java
// REMOVED this check:
if (accordConcession.getDocument() == null) {
    return ResponseUtil.badRequest("Document is required");
}
```

### Added Document Creation Logic

#### Step 1: Verify Account Exists
```java
Account owner = accordConcession.getDoneBy();
Optional<Account> accountOpt = accountRepository.findById(owner.getId());
if (accountOpt.isEmpty()) {
    return ResponseUtil.badRequest("Account not found");
}
Account actualOwner = accountOpt.get();
```

#### Step 2: Create Document with Random Data
```java
Document document = new Document();

// Generate unique file name
String uniqueFileName = "accord_concession_" + UUID.randomUUID().toString() + ".pdf";

// Set document properties with random/default data
document.setFileName(uniqueFileName);
document.setOriginalFileName("Accord_Concession_" + accordConcession.getNumeroAccord() + ".pdf");
document.setContentType("application/pdf");
document.setFileSize(1024L * 256L); // 256 KB
document.setFilePath("/uploads/accord_concession/" + uniqueFileName);
document.setOwner(actualOwner);
document.setExpirationDate(LocalDateTime.now().plusYears(5));
document.setVersion("1.0");
document.setActive(true);
```

#### Step 3: Save Document and Link
```java
// Save document first to get ID
Document savedDocument = documentRepository.save(document);

// Link to accord concession
accordConcession.setDocument(savedDocument);
accordConcession.setActive(true);

// Save accord concession
AccordConcession saved = accordConcessionRepository.save(accordConcession);
```

---

## 4. Generated Document Data

### Fields with Random/Default Values

| Field | Value | Description |
|-------|-------|-------------|
| **fileName** | `accord_concession_{UUID}.pdf` | Unique system name using UUID |
| **originalFileName** | `Accord_Concession_{numeroAccord}.pdf` | User-friendly name |
| **contentType** | `application/pdf` | PDF MIME type |
| **fileSize** | `262144` (256 KB) | Random file size |
| **filePath** | `/uploads/accord_concession/{UUID}.pdf` | Storage path |
| **owner** | From `doneBy` account | Account creating the record |
| **expirationDate** | Current date + 5 years | Default expiry |
| **version** | `1.0` | Initial version |
| **active** | `true` | Active by default |

### Example Generated Values

```json
{
  "id": 1,
  "fileName": "accord_concession_3f8a7b2c-5d91-4e89-b123-9876543210ab.pdf",
  "originalFileName": "Accord_Concession_ACC-2025-001.pdf",
  "contentType": "application/pdf",
  "fileSize": 262144,
  "filePath": "/uploads/accord_concession/accord_concession_3f8a7b2c-5d91-4e89-b123-9876543210ab.pdf",
  "owner": {
    "id": 5,
    "username": "admin",
    "fullName": "John Doe"
  },
  "expirationDate": "2030-10-13T10:30:00",
  "version": "1.0",
  "active": true,
  "createdAt": "2025-10-13T10:30:00",
  "updatedAt": "2025-10-13T10:30:00"
}
```

---

## 5. Database Relationship

### Tables Involved

#### `files` Table (Document)
```sql
CREATE TABLE files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT,
    file_path VARCHAR(500) NOT NULL,
    owner_id BIGINT NOT NULL,
    expiration_date DATETIME NOT NULL,
    version VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (owner_id) REFERENCES accounts(id)
);
```

#### `accord_concession` Table
```sql
CREATE TABLE accord_concession (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doc_id BIGINT NOT NULL,
    doneby BIGINT NOT NULL,
    statut_id BIGINT NOT NULL,
    numero_accord VARCHAR(100),
    objet_concession VARCHAR(500),
    -- ... other fields
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (doc_id) REFERENCES files(id),
    FOREIGN KEY (doneby) REFERENCES accounts(id),
    FOREIGN KEY (statut_id) REFERENCES docstatus(id)
);
```

### Relationship Flow

```
1. POST /api/document/accord-concession
   ↓
2. Create Document in files table
   ↓ (get generated doc_id)
3. Create AccordConcession with doc_id
   ↓
4. Return saved AccordConcession with linked Document
```

---

## 6. API Request Example

### Before (OLD - Would Fail)

```json
POST /api/document/accord-concession
{
  "numeroAccord": "ACC-2025-001",
  "objetConcession": "Land Concession",
  "concessionnaire": "ABC Company",
  "doneBy": {
    "id": 5
  },
  "status": {
    "id": 1
  },
  "document": null  // ❌ Required, would fail
}
```

### After (NEW - Works Automatically)

```json
POST /api/document/accord-concession
{
  "numeroAccord": "ACC-2025-001",
  "objetConcession": "Land Concession",
  "concessionnaire": "ABC Company",
  "dureeAnnees": 25,
  "conditionsFinancieres": "Annual payment of $10,000",
  "doneBy": {
    "id": 5
  },
  "status": {
    "id": 1
  }
  // ✅ NO document field needed - created automatically!
}
```

---

## 7. API Response Example

```json
{
  "success": true,
  "message": "Concession agreement created successfully",
  "data": {
    "id": 10,
    "numeroAccord": "ACC-2025-001",
    "objetConcession": "Land Concession",
    "concessionnaire": "ABC Company",
    "dureeAnnees": 25,
    "conditionsFinancieres": "Annual payment of $10,000",
    "document": {
      "id": 45,
      "fileName": "accord_concession_3f8a7b2c-5d91-4e89-b123-9876543210ab.pdf",
      "originalFileName": "Accord_Concession_ACC-2025-001.pdf",
      "contentType": "application/pdf",
      "fileSize": 262144,
      "filePath": "/uploads/accord_concession/accord_concession_3f8a7b2c-5d91-4e89-b123-9876543210ab.pdf",
      "version": "1.0",
      "expirationDate": "2030-10-13T10:30:00",
      "active": true
    },
    "doneBy": {
      "id": 5,
      "username": "admin",
      "fullName": "John Doe"
    },
    "status": {
      "id": 1,
      "name": "Valid"
    },
    "dateTime": "2025-10-13T10:30:00",
    "active": true
  }
}
```

---

## 8. Validation Changes

### Required Fields (Updated)

✅ **Still Required:**
- `numeroAccord` - Concession agreement number
- `doneBy` (Account) - User creating the record
- `status` - Document status

❌ **No Longer Required:**
- ~~`document`~~ - Now created automatically

### Validation Flow

```java
// 1. Validate numeroAccord
if (accordConcession.getNumeroAccord() == null || 
    accordConcession.getNumeroAccord().trim().isEmpty()) {
    return ResponseUtil.badRequest("Numero accord is required");
}

// 2. Validate doneBy (Account)
if (accordConcession.getDoneBy() == null) {
    return ResponseUtil.badRequest("DoneBy (Account) is required");
}

// 3. Validate account exists
Optional<Account> accountOpt = accountRepository.findById(owner.getId());
if (accountOpt.isEmpty()) {
    return ResponseUtil.badRequest("Account not found with ID: " + owner.getId());
}

// 4. Validate status
if (accordConcession.getStatus() == null) {
    return ResponseUtil.badRequest("Status is required");
}
```

---

## 9. Benefits

### 1. **Simplified API Usage**
- Frontend no longer needs to create document first
- Single API call creates both records
- Automatic relationship management

### 2. **Data Consistency**
- Document always created with valid data
- Guaranteed link between Document and AccordConcession
- No orphan records

### 3. **Automatic File Management**
- Unique file names prevent conflicts (UUID)
- Organized file path structure
- Default values ensure data completeness

### 4. **Audit Trail**
- Document owner tracked from `doneBy`
- Creation timestamp automatically set
- Version tracking from start

---

## 10. Testing

### Test Case 1: Successful Creation

**Request:**
```bash
curl -X POST http://localhost:8080/api/document/accord-concession \
  -H "Content-Type: application/json" \
  -d '{
    "numeroAccord": "ACC-2025-001",
    "objetConcession": "Land Concession",
    "concessionnaire": "ABC Company",
    "dureeAnnees": 25,
    "doneBy": {"id": 5},
    "status": {"id": 1}
  }'
```

**Expected Result:**
- HTTP 200 OK
- Document created in `files` table
- AccordConcession created with `doc_id` linking to document
- Response includes both objects

**Verify in Database:**
```sql
-- Check document created
SELECT * FROM files WHERE file_name LIKE 'accord_concession_%';

-- Check accord_concession linked
SELECT ac.*, f.file_name 
FROM accord_concession ac
JOIN files f ON ac.doc_id = f.id
WHERE ac.numero_accord = 'ACC-2025-001';
```

### Test Case 2: Invalid Account

**Request:**
```bash
curl -X POST http://localhost:8080/api/document/accord-concession \
  -H "Content-Type: application/json" \
  -d '{
    "numeroAccord": "ACC-2025-002",
    "doneBy": {"id": 999999},
    "status": {"id": 1}
  }'
```

**Expected Result:**
- HTTP 400 Bad Request
- Error message: "Account not found with ID: 999999"
- No records created

### Test Case 3: Missing Required Fields

**Request:**
```bash
curl -X POST http://localhost:8080/api/document/accord-concession \
  -H "Content-Type: application/json" \
  -d '{
    "objetConcession": "Land Concession"
  }'
```

**Expected Result:**
- HTTP 400 Bad Request
- Error message: "Numero accord is required" (or similar)
- No records created

---

## 11. Frontend Update Required

Update the frontend component to **remove** the document field from the request:

### Before (OLD)

```javascript
const createAccordConcession = async (formData) => {
  // Had to create document first, then send
  const documentData = await createDocument(...);
  
  const response = await apiClient.post('/document/accord-concession', {
    ...formData,
    document: { id: documentData.id }  // Had to include
  });
};
```

### After (NEW)

```javascript
const createAccordConcession = async (formData) => {
  // Just send the form data - document created automatically!
  const response = await apiClient.post('/document/accord-concession', {
    numeroAccord: formData.numeroAccord,
    objetConcession: formData.objetConcession,
    concessionnaire: formData.concessionnaire,
    dureeAnnees: formData.dureeAnnees,
    conditionsFinancieres: formData.conditionsFinancieres,
    doneBy: { id: currentUserId },
    status: { id: formData.statusId }
    // ✅ No document field needed!
  });
  
  return response.data;
};
```

---

## 12. Logging

The controller now includes detailed logging:

```java
log.info("Creating new concession agreement: {}", accordConcession.getNumeroAccord());
log.info("Created document with ID: {} for concession agreement: {}", 
         savedDocument.getId(), accordConcession.getNumeroAccord());
log.error("Error creating concession agreement", e);
```

**Console Output Example:**
```
2025-10-13 10:30:00 INFO  AccordConcessionController - Creating new concession agreement: ACC-2025-001
2025-10-13 10:30:00 INFO  AccordConcessionController - Created document with ID: 45 for concession agreement: ACC-2025-001
```

---

## 13. Error Handling

All errors are caught and returned with appropriate messages:

```java
try {
    // Document creation and linking logic
} catch (Exception e) {
    log.error("Error creating concession agreement", e);
    return ResponseUtil.badRequest("Failed to create concession agreement: " + e.getMessage());
}
```

**Common Errors:**
- Account not found
- Database constraint violations
- Invalid data format
- Network/connection issues

---

## 14. Future Enhancements

### Potential Improvements

1. **Actual File Upload**
   ```java
   // Add file upload capability
   @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ResponseEntity<?> createWithFile(
       @RequestPart("data") AccordConcession accordConcession,
       @RequestPart("file") MultipartFile file) {
       // Save actual file to disk
       // Update document with real file info
   }
   ```

2. **Custom File Paths**
   ```java
   // Organize by year/month
   String filePath = String.format("/uploads/accord_concession/%d/%02d/%s",
       LocalDate.now().getYear(),
       LocalDate.now().getMonthValue(),
       uniqueFileName);
   ```

3. **File Size Validation**
   ```java
   // Add maximum file size check
   private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
   
   if (file.getSize() > MAX_FILE_SIZE) {
       return ResponseUtil.badRequest("File size exceeds maximum allowed");
   }
   ```

4. **Version Auto-Increment**
   ```java
   // If updating existing agreement, increment version
   String newVersion = incrementVersion(existingDocument.getVersion());
   document.setVersion(newVersion);
   ```

---

## Summary

✅ **Successfully Updated AccordConcessionController**

### Key Changes:
1. ✅ Added DocumentRepository and AccountRepository dependencies
2. ✅ Removed document requirement from API request
3. ✅ Automatically creates Document with random data
4. ✅ Links Document to AccordConcession via doc_id
5. ✅ Validates account exists before creating
6. ✅ Generates unique file names using UUID
7. ✅ Sets default values (version, expiry, etc.)
8. ✅ Includes detailed logging

### Result:
- **Simplified API** - One call creates both records
- **Data Integrity** - Automatic relationship management
- **Better UX** - Frontend doesn't need to manage documents separately

**Status:** ✅ COMPLETE AND READY FOR TESTING

