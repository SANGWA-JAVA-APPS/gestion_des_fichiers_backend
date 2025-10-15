# DocumentUploadService - Quick Reference Guide

## Overview
Reusable service for document version management across all controllers.

---

## Quick Start

### 1. Inject the Service

```java
private final DocumentUploadService documentUploadService;

public YourController(DocumentUploadService documentUploadService) {
    this.documentUploadService = documentUploadService;
}
```

### 2. Get Next Version (Most Common Use Case)

```java
String originalFileName = "Accord_Concession_" + record.getNumeroAccord() + ".pdf";
String version = documentUploadService.getNextVersionForDocument(originalFileName);
document.setVersion(version);
```

**That's it! The service handles:**
- ✅ Checking if document exists
- ✅ Parsing current version
- ✅ Incrementing minor version
- ✅ Error handling with fallback to "1.0"
- ✅ Comprehensive logging

---

## All Methods (At a Glance)

| Method | Input | Output | Use Case |
|--------|-------|--------|----------|
| `getNextVersionForDocument()` | `String originalFileName` | `String version` | Get next version (auto-increment) |
| `incrementVersion()` | `String currentVersion, String fileName` | `String version` | Manual version increment |
| `documentExists()` | `String originalFileName` | `boolean` | Check if document exists |
| `getLatestDocumentByOriginalFileName()` | `String originalFileName` | `Optional<Document>` | Retrieve latest document |
| `getCurrentVersion()` | `String originalFileName` | `String version` | Get current version without increment |
| `parseVersion()` | `String version` | `int[] [major, minor]` | Parse version into parts |
| `buildVersion()` | `int major, int minor` | `String version` | Build version string |

---

## Common Patterns

### Pattern 1: Simple Version Increment (90% of cases)
```java
String originalFileName = "Document_" + id + ".pdf";
String version = documentUploadService.getNextVersionForDocument(originalFileName);
document.setVersion(version);
```

### Pattern 2: Check Before Upload
```java
String originalFileName = "Document_" + id + ".pdf";

if (documentUploadService.documentExists(originalFileName)) {
    String currentVersion = documentUploadService.getCurrentVersion(originalFileName);
    log.info("Document exists with version: {}", currentVersion);
}

String newVersion = documentUploadService.getNextVersionForDocument(originalFileName);
document.setVersion(newVersion);
```

### Pattern 3: Custom Version Logic
```java
String originalFileName = "Document_" + id + ".pdf";
String currentVersion = documentUploadService.getCurrentVersion(originalFileName);
int[] parts = documentUploadService.parseVersion(currentVersion);

// Custom logic
if (parts[1] >= 9) {
    parts[0]++; // Increment major
    parts[1] = 0; // Reset minor
} else {
    parts[1]++; // Increment minor
}

String newVersion = documentUploadService.buildVersion(parts[0], parts[1]);
document.setVersion(newVersion);
```

### Pattern 4: Retrieve Latest Document
```java
String originalFileName = "Document_" + id + ".pdf";
Optional<Document> latestDoc = documentUploadService
    .getLatestDocumentByOriginalFileName(originalFileName);

if (latestDoc.isPresent()) {
    Document doc = latestDoc.get();
    log.info("Latest version: {}, Created: {}", doc.getVersion(), doc.getCreatedAt());
}
```

---

## Version Format

**Format:** `major.minor` (e.g., `1.0`, `1.5`, `2.10`)

**Default:** `1.0` for new documents

**Increment:** Minor version increments by 1
- `1.0` → `1.1` → `1.2` → ... → `1.9` → `1.10` → `1.11`

**Error Handling:** Falls back to `1.0` if version is invalid

---

## Controllers to Update

Apply this service to all document controllers:

1. ✅ `AccordConcessionController` - **DONE**
2. ⏳ `NormeLoiController`
3. ⏳ `EstateController`
4. ⏳ `CertLicensesController`
5. ⏳ `PermiConstructionController`
6. ⏳ `CargoDamageController`
7. ⏳ `CommAssetLandController`

---

## Migration Checklist

For each controller:

- [ ] Add `private final DocumentUploadService documentUploadService;`
- [ ] Add parameter to constructor
- [ ] Add import: `import com.bar.gestiondesfichier.document.service.DocumentUploadService;`
- [ ] Find version logic (54 lines with Optional, split, parseInt, etc.)
- [ ] Replace with: `String version = documentUploadService.getNextVersionForDocument(originalFileName);`
- [ ] Test creation of 2-3 records to verify version increment

---

## Testing

### Test Case 1: First Document
```bash
POST /api/document/accord-concession
{
  "numeroAccord": "TEST-001",
  "objetConcession": "Test",
  "concessionnaire": "ABC",
  "doneBy": {"id": 5},
  "status": {"id": 1}
}
```
**Expected:** `version: "1.0"`

### Test Case 2: Second Document (Same ID)
```bash
POST /api/document/accord-concession
{
  "numeroAccord": "TEST-001",
  "objetConcession": "Test Updated",
  "concessionnaire": "ABC",
  "doneBy": {"id": 5},
  "status": {"id": 1}
}
```
**Expected:** `version: "1.1"`

### Test Case 3: Third Document (Same ID)
```bash
POST /api/document/accord-concession
{
  "numeroAccord": "TEST-001",
  "objetConcession": "Test Updated Again",
  "concessionnaire": "ABC",
  "doneBy": {"id": 5},
  "status": {"id": 1}
}
```
**Expected:** `version: "1.2"`

---

## Logging Examples

### New Document
```
DEBUG DocumentUploadService - Checking version for document with original file name: Accord_Concession_TEST-001.pdf
INFO  DocumentUploadService - No existing document found with name 'Accord_Concession_TEST-001.pdf'. Setting version to 1.0
INFO  AccordConcessionController - Created document with ID: 1 and version: 1.0 for concession agreement: TEST-001
```

### Version Increment
```
DEBUG DocumentUploadService - Checking version for document with original file name: Accord_Concession_TEST-001.pdf
DEBUG DocumentUploadService - Parsed version '1.0' to major=1, minor=0
INFO  DocumentUploadService - Found existing document 'Accord_Concession_TEST-001.pdf' with version 1.0. New version will be: 1.1
INFO  AccordConcessionController - Created document with ID: 2 and version: 1.1 for concession agreement: TEST-001
```

---

## Troubleshooting

### Issue: Version always returns "1.0"
**Cause:** `originalFileName` might be different each time
**Solution:** Ensure `originalFileName` is consistent (e.g., based on record ID, not UUID)

```java
// ❌ BAD: Different filename each time
String originalFileName = "Document_" + UUID.randomUUID() + ".pdf";

// ✅ GOOD: Same filename for same record
String originalFileName = "Accord_Concession_" + record.getNumeroAccord() + ".pdf";
```

### Issue: Version skips numbers (1.0 → 1.2)
**Cause:** Another document was created in between
**Solution:** This is expected behavior. Check database for all versions:
```sql
SELECT id, original_file_name, version, created_at 
FROM files 
WHERE original_file_name = 'Accord_Concession_TEST-001.pdf'
ORDER BY id;
```

### Issue: Lombok errors in IDE
**Cause:** Lombok annotation processor not initialized
**Solution:** Restart IDE or rebuild project:
```bash
mvn clean compile
```

---

## Example: Full Controller Integration

```java
package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.document.service.DocumentUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/document/norme-loi")
@RequiredArgsConstructor
public class NormeLoiController {

    private final NormeLoiRepository normeLoiRepository;
    private final DocumentRepository documentRepository;
    private final DocumentUploadService documentUploadService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createNormeLoi(@RequestBody NormeLoi normeLoi) {
        // ... validation
        
        // Create document
        Document document = new Document();
        String uniqueFileName = "norme_loi_" + UUID.randomUUID() + ".pdf";
        String originalFileName = "Norme_Loi_" + normeLoi.getNumeroNorme() + ".pdf";
        
        document.setFileName(uniqueFileName);
        document.setOriginalFileName(originalFileName);
        document.setContentType("application/pdf");
        document.setFilePath("/uploads/norme_loi/" + uniqueFileName);
        
        // Get next version using service
        String version = documentUploadService.getNextVersionForDocument(originalFileName);
        document.setVersion(version);
        
        // Save and link
        Document savedDocument = documentRepository.save(document);
        normeLoi.setDocument(savedDocument);
        
        NormeLoi savedNormeLoi = normeLoiRepository.save(normeLoi);
        return ResponseUtil.success(savedNormeLoi, "Norme/Loi created successfully");
    }
}
```

---

## Related Files

- **Service Implementation:** `src/main/java/com/bar/gestiondesfichier/document/service/DocumentUploadService.java`
- **Repository:** `src/main/java/com/bar/gestiondesfichier/document/repository/DocumentRepository.java`
- **Example Controller:** `src/main/java/com/bar/gestiondesfichier/document/controller/AccordConcessionController.java`
- **Detailed Documentation:** `REFACTORING_VERSION_SERVICE.md`
- **Version Feature:** `DOCUMENT_VERSION_INCREMENT.md`

---

## Summary

✅ **One-line solution for version management:**
```java
String version = documentUploadService.getNextVersionForDocument(originalFileName);
```

**Handles automatically:**
- Version checking
- Version parsing
- Version increment
- Error handling
- Logging

**No need to:**
- Query repository manually
- Parse version strings
- Handle exceptions
- Write version logic repeatedly

**Result:**
- 54 lines → 1 line
- Reusable across all controllers
- Consistent version management
- Easy to maintain and test

