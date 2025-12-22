# File Upload Implementation Progress

## Date: October 15, 2025

## Overview
Implementing file upload functionality across all document controllers following the pattern from `AccordConcessionController`.

---

## Pattern Template

### Required Changes for Each Controller:

#### 1. **Imports to Add:**
```java
import com.bar.gestiondesfichier.document.model.Document;
import com.bar.gestiondesfichier.document.repository.DocumentRepository;
import com.bar.gestiondesfichier.document.service.DocumentUploadService;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.repository.AccountRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Paths;
```

#### 2. **Constructor Injection:**
```java
private final DocumentUploadService documentUploadService;
private final DocumentRepository documentRepository;
private final AccountRepository accountRepository;

public Controller(Repository repository,
                  DocumentUploadService documentUploadService,
                  DocumentRepository documentRepository,
                  AccountRepository accountRepository) {
    // Initialize fields
}
```

#### 3. **POST Method Signature:**
```java
@PostMapping(consumes = {"multipart/form-data"})
@Transactional
@ApiResponse(responseCode = "400", description = "Invalid request data or missing file")
public ResponseEntity<Map<String, Object>> create(
        @RequestPart("entityName") Entity entity,
        @RequestPart("file") MultipartFile file) {
```

#### 4. **File Upload Logic:**
```java
// Get and verify account
Account owner = entity.getDoneBy();
Optional<Account> accountOpt = accountRepository.findById(owner.getId());
if (accountOpt.isEmpty()) {
    return ResponseUtil.badRequest("Account not found with ID: " + owner.getId());
}
Account actualOwner = accountOpt.get();

// Prepare variables for document metadata
String contentType;
String fileExtension;
String uniqueFileName;
String originalFileName;
String filePath;
long fileSize;

// Validate file
if (file == null || file.isEmpty()) {
    log.warn("File upload is required...");
    return ResponseUtil.badRequest("Document file is required. Please upload a file...");
}

// Upload file
try {
    filePath = documentUploadService.uploadFile(file, "folder_name");
    contentType = file.getContentType();
    fileSize = file.getSize();
    fileExtension = documentUploadService.extractFileExtension(file.getOriginalFilename(), contentType);
    uniqueFileName = Paths.get(filePath).getFileName().toString();
    originalFileName = documentUploadService.generateOriginalFileName("Prefix", entity.getIdentifier(), fileExtension);
} catch (IOException e) {
    log.error("Failed to upload file", e);
    return ResponseUtil.badRequest("Failed to upload file: " + e.getMessage());
}

// Initialize document
Document document = documentUploadService.initializeDocument(
    uniqueFileName, originalFileName, contentType, fileSize, filePath, actualOwner);

// Save document
Document savedDocument = documentRepository.save(document);

// Link and save entity
entity.setDocument(savedDocument);
entity.setActive(true);
Entity savedEntity = repository.save(entity);
```

---

## Implementation Status

### ✅ Completed Controllers

#### 1. **AccordConcessionController** (Reference Implementation)
- **Status:** ✅ Complete (Original)
- **Folder:** `accord_concession`
- **Prefix:** `Accord_Concession`
- **Identifier:** `numeroAccord`
- **Changes:**
  - Added file upload with validation
  - Uses DocumentUploadService for all operations
  - Mandatory file requirement
  - Version auto-increment

#### 2. **NormeLoiController**
- **Status:** ✅ Complete
- **Folder:** `norme_loi`
- **Prefix:** `Norme_Loi`
- **Identifier:** `reference`
- **Date:** October 15, 2025
- **Changes Made:**
  - ✅ Added imports (Document, DocumentRepository, DocumentUploadService, Account, AccountRepository, MultipartFile, Transactional)
  - ✅ Updated constructor to inject DocumentUploadService, DocumentRepository, AccountRepository
  - ✅ Changed `@PostMapping` to `@PostMapping(consumes = {"multipart/form-data"})`
  - ✅ Added `@Transactional` annotation
  - ✅ Changed parameter from `@RequestBody` to `@RequestPart("normeLoi")` and `@RequestPart("file")`
  - ✅ Added account verification logic
  - ✅ Added file upload logic with validation
  - ✅ Added document initialization using DocumentUploadService
  - ✅ Removed `if (normeLoi.getDocument() == null)` validation (now created automatically)
  - ✅ Linked document to normeLoi
  - ✅ Updated API documentation

**Code Summary:**
- Lines changed: ~60 lines in `createNormeLoi` method
- File validation: Mandatory file upload
- Error handling: Clear messages for missing file
- Folder structure: `uploads/norme_loi/`
- Filename pattern: `Norme_Loi_{reference}.{ext}`

---

### ⏳ Pending Controllers

#### 3. **EstateController**
- **Status:** ⏳ Pending
- **Folder:** `estate`
- **Prefix:** `Estate`
- **Identifier:** TBD (likely `reference` or `propertyId`)

#### 4. **CertLicensesController**
- **Status:** ⏳ Pending
- **Folder:** `cert_licenses`
- **Prefix:** `Cert_Licenses`
- **Identifier:** TBD

#### 5. **PermiConstructionController**
- **Status:** ⏳ Pending
- **Folder:** `permi_construction`
- **Prefix:** `Permi_Construction`
- **Identifier:** TBD

#### 6. **CargoDamageController**
- **Status:** ⏳ Pending
- **Folder:** `cargo_damage`
- **Prefix:** `Cargo_Damage`
- **Identifier:** TBD

#### 7. **CommAssetLandController**
- **Status:** ⏳ Pending
- **Folder:** `comm_asset_land`
- **Prefix:** `Comm_Asset_Land`
- **Identifier:** TBD

---

## Testing Checklist (Per Controller)

### Before Testing
- [ ] Check that all imports are added
- [ ] Verify constructor injection is correct
- [ ] Confirm `@Transactional` annotation is present
- [ ] Check folder name matches entity name

### Test Cases

#### 1. **Valid File Upload**
```bash
curl -X POST http://localhost:8104/api/document/norme-loi \
  -F 'normeLoi={"reference":"NL-2025-001","doneBy":{"id":5},"status":{"id":1}};type=application/json' \
  -F 'file=@document.pdf'
```
**Expected:** 201 Created with document details

#### 2. **Missing File**
```bash
curl -X POST http://localhost:8104/api/document/norme-loi \
  -F 'normeLoi={"reference":"NL-2025-002","doneBy":{"id":5},"status":{"id":1}};type=application/json'
```
**Expected:** 400 Bad Request with message "Document file is required..."

#### 3. **Empty File**
```bash
curl -X POST http://localhost:8104/api/document/norme-loi \
  -F 'normeLoi={"reference":"NL-2025-003","doneBy":{"id":5},"status":{"id":1}};type=application/json' \
  -F 'file='
```
**Expected:** 400 Bad Request with validation error

#### 4. **Large File (>50MB)**
```bash
curl -X POST http://localhost:8104/api/document/norme-loi \
  -F 'normeLoi={"reference":"NL-2025-004","doneBy":{"id":5},"status":{"id":1}};type=application/json' \
  -F 'file=@large_file.pdf'
```
**Expected:** 413 Payload Too Large

#### 5. **Version Increment**
- Upload same reference twice
- First upload: version = "1.0"
- Second upload: version = "1.1"

---

## Common Issues & Solutions

### Issue 1: Lombok Not Working
**Symptom:** "cannot find symbol: method getVersion()"
**Solution:** Compile with Maven: `mvn clean compile`

### Issue 2: Account Not Found
**Symptom:** 400 error "Account not found with ID: X"
**Solution:** Ensure account ID exists in database

### Issue 3: Folder Not Created
**Symptom:** IOException "No such file or directory"
**Solution:** Check `app.upload.dir` in application.properties

### Issue 4: File Size Limit
**Symptom:** 413 Payload Too Large
**Solution:** Adjust `spring.servlet.multipart.max-file-size` in application.properties

---

## Folder Structure After Implementation

```
uploads/
├── accord_concession/
│   ├── accord_concession_uuid1.pdf
│   └── accord_concession_uuid2.docx
├── norme_loi/
│   ├── norme_loi_uuid1.pdf
│   └── norme_loi_uuid2.pdf
├── estate/
│   └── (pending)
├── cert_licenses/
│   └── (pending)
├── permi_construction/
│   └── (pending)
├── cargo_damage/
│   └── (pending)
└── comm_asset_land/
    └── (pending)
```

---

## Next Steps

1. ✅ **NormeLoiController** - COMPLETED
2. ⏳ **EstateController** - Start next
3. ⏳ **CertLicensesController**
4. ⏳ **PermiConstructionController**
5. ⏳ **CargoDamageController**
6. ⏳ **CommAssetLandController**
7. ⏳ Test all endpoints
8. ⏳ Update frontend forms
9. ⏳ Update API documentation

---

## Estimated Time

- **Per Controller:** ~10 minutes
- **Total Remaining:** ~50 minutes (5 controllers)
- **Testing:** ~30 minutes
- **Total:** ~1.5 hours

---

**Last Updated:** October 15, 2025  
**Progress:** 2/7 controllers (28.6%)  
**Status:** 🟡 In Progress
