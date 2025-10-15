# Document Version Service Refactoring

## Date: October 15, 2025

## Overview

Refactored document version checking logic from `AccordConcessionController` into a reusable service class `DocumentUploadService`. This improves code maintainability and enables version management across all document controllers.

---

## Changes Made

### 1. Created DocumentUploadService.java

**Location:** `src/main/java/com/bar/gestiondesfichier/document/service/DocumentUploadService.java`

**Purpose:** Centralized service for document version management and upload operations

**Key Methods:**

#### a. getNextVersionForDocument(String originalFileName)
```java
/**
 * Get the next version number for a document based on its original file name.
 * If no existing document is found, returns "1.0".
 * If existing document is found, increments the minor version (e.g., "1.5" -> "1.6").
 * 
 * @param originalFileName The original file name to check for existing versions
 * @return The next version number as a string (e.g., "1.0", "1.1", "2.5")
 */
public String getNextVersionForDocument(String originalFileName)
```

**Usage Example:**
```java
String originalFileName = "Accord_Concession_ACC-2025-001.pdf";
String version = documentUploadService.getNextVersionForDocument(originalFileName);
// Returns: "1.0" (if new) or "1.6" (if existing was "1.5")
```

---

#### b. incrementVersion(String currentVersion, String originalFileName)
```java
/**
 * Increment the version number. Parses the current version and increments the minor version.
 * Format: major.minor (e.g., "1.5" -> "1.6")
 * 
 * @param currentVersion The current version string to increment
 * @param originalFileName The original file name (used for logging)
 * @return The incremented version string, or "1.0" if parsing fails
 */
public String incrementVersion(String currentVersion, String originalFileName)
```

**Usage Example:**
```java
String newVersion = documentUploadService.incrementVersion("2.5", "document.pdf");
// Returns: "2.6"

String defaultVersion = documentUploadService.incrementVersion("invalid", "document.pdf");
// Returns: "1.0" (fallback)
```

---

#### c. documentExists(String originalFileName)
```java
/**
 * Check if a document with the given original file name already exists.
 * 
 * @param originalFileName The original file name to check
 * @return true if a document exists, false otherwise
 */
public boolean documentExists(String originalFileName)
```

**Usage Example:**
```java
boolean exists = documentUploadService.documentExists("Accord_Concession_ACC-2025-001.pdf");
if (exists) {
    log.info("Document already exists");
}
```

---

#### d. getLatestDocumentByOriginalFileName(String originalFileName)
```java
/**
 * Get the latest document by original file name.
 * 
 * @param originalFileName The original file name to search for
 * @return Optional containing the latest document if found, empty otherwise
 */
public Optional<Document> getLatestDocumentByOriginalFileName(String originalFileName)
```

**Usage Example:**
```java
Optional<Document> latestDoc = documentUploadService
    .getLatestDocumentByOriginalFileName("Accord_Concession_ACC-2025-001.pdf");

if (latestDoc.isPresent()) {
    log.info("Latest version: {}", latestDoc.get().getVersion());
}
```

---

#### e. getCurrentVersion(String originalFileName)
```java
/**
 * Get the current version of the latest document with the given original file name.
 * Returns "1.0" if no document is found.
 * 
 * @param originalFileName The original file name to check
 * @return The current version string, or "1.0" if no document exists
 */
public String getCurrentVersion(String originalFileName)
```

**Usage Example:**
```java
String currentVersion = documentUploadService.getCurrentVersion("Accord_Concession_ACC-2025-001.pdf");
log.info("Current version: {}", currentVersion);
// Returns: "1.5" or "1.0" (if not found)
```

---

#### f. parseVersion(String version)
```java
/**
 * Parse version string into major and minor components.
 * Returns an array [major, minor]. If parsing fails, returns [1, 0].
 * 
 * @param version The version string to parse (e.g., "2.5")
 * @return Array of [majorVersion, minorVersion]
 */
public int[] parseVersion(String version)
```

**Usage Example:**
```java
int[] parts = documentUploadService.parseVersion("2.5");
// Returns: [2, 5]

int[] defaultParts = documentUploadService.parseVersion("invalid");
// Returns: [1, 0]
```

---

#### g. buildVersion(int major, int minor)
```java
/**
 * Build a version string from major and minor version numbers.
 * 
 * @param major The major version number
 * @param minor The minor version number
 * @return The formatted version string (e.g., "2.5")
 */
public String buildVersion(int major, int minor)
```

**Usage Example:**
```java
String version = documentUploadService.buildVersion(2, 5);
// Returns: "2.5"
```

---

### 2. Updated AccordConcessionController.java

**Changes:**

#### Before (Lines 157-211)
```java
// Check if document with same original file name exists and increment version
Optional<Document> existingDocOpt = documentRepository.findTopByOriginalFileNameOrderByIdDesc(originalFileName);
String newVersion = "1.0"; // Default version for new documents

if (existingDocOpt.isPresent()) {
    Document existingDoc = existingDocOpt.get();
    String currentVersion = existingDoc.getVersion();
    
    // Parse and increment version
    if (currentVersion != null && !currentVersion.isEmpty()) {
        try {
            // Parse version (e.g., "1.0" -> 1, "2.5" -> 2)
            String[] versionParts = currentVersion.split("\\.");
            int majorVersion = Integer.parseInt(versionParts[0]);
            int minorVersion = versionParts.length > 1 ? Integer.parseInt(versionParts[1]) : 0;
            
            // Increment minor version
            minorVersion++;
            newVersion = majorVersion + "." + minorVersion;
            
            log.info("Found existing document with version {}. New version will be: {}", 
                    currentVersion, newVersion);
        } catch (NumberFormatException e) {
            log.warn("Could not parse version '{}', using default '1.0'", currentVersion);
            newVersion = "1.0";
        }
    } else {
        log.info("Existing document has no version. Setting new version to 1.0");
        newVersion = "1.0";
    }
} else {
    log.info("No existing document found with name '{}'. Setting version to 1.0", originalFileName);
}

document.setVersion(newVersion);
```

#### After (Lines 179-181)
```java
// Use DocumentUploadService to get the next version
String newVersion = documentUploadService.getNextVersionForDocument(originalFileName);
document.setVersion(newVersion);
```

**Improvement:**
- **Before:** 54 lines of version checking logic
- **After:** 2 lines using the service
- **Reduction:** 96% code reduction in controller

---

### 3. Added Dependency Injection

**Constructor Updated:**
```java
public AccordConcessionController(
        AccordConcessionRepository accordConcessionRepository,
        DocumentRepository documentRepository,
        AccountRepository accountRepository,
        DocumentUploadService documentUploadService) {  // NEW
    this.accordConcessionRepository = accordConcessionRepository;
    this.documentRepository = documentRepository;
    this.accountRepository = accountRepository;
    this.documentUploadService = documentUploadService;  // NEW
}
```

**Import Added:**
```java
import com.bar.gestiondesfichier.document.service.DocumentUploadService;
```

---

## Benefits of Refactoring

### 1. **Code Reusability**
- Version logic can now be used across all document controllers:
  - `NormeLoiController`
  - `EstateController`
  - `CertLicensesController`
  - `PermiConstructionController`
  - `CargoDamageController`
  - `CommAssetLandController`

### 2. **Maintainability**
- Single source of truth for version management
- Bug fixes apply to all controllers
- Easier to test and debug

### 3. **Separation of Concerns**
- Controller focuses on HTTP request/response
- Service handles business logic
- Repository manages data access

### 4. **Testability**
- Service can be unit tested independently
- Mock service in controller tests
- Test edge cases once in service tests

### 5. **Flexibility**
- Multiple utility methods for different use cases
- Easy to extend with new version strategies
- Can add complex version logic without changing controllers

---

## Usage in Other Controllers

### Example: NormeLoiController

**Before (without service):**
```java
@PostMapping
public ResponseEntity<Map<String, Object>> createNormeLoi(@RequestBody NormeLoi normeLoi) {
    // ... validation
    
    String originalFileName = "Norme_Loi_" + normeLoi.getNumeroNorme() + ".pdf";
    
    // Copy-paste 54 lines of version logic here
    Optional<Document> existingDocOpt = documentRepository.findTopByOriginalFileNameOrderByIdDesc(originalFileName);
    String newVersion = "1.0";
    if (existingDocOpt.isPresent()) {
        // ... 50 more lines
    }
    
    document.setVersion(newVersion);
}
```

**After (with service):**
```java
@PostMapping
public ResponseEntity<Map<String, Object>> createNormeLoi(@RequestBody NormeLoi normeLoi) {
    // ... validation
    
    String originalFileName = "Norme_Loi_" + normeLoi.getNumeroNorme() + ".pdf";
    
    // Use the service
    String newVersion = documentUploadService.getNextVersionForDocument(originalFileName);
    document.setVersion(newVersion);
}
```

---

## Advanced Use Cases

### Use Case 1: Check Before Upload
```java
String originalFileName = "Accord_Concession_ACC-2025-001.pdf";

// Check if document exists
if (documentUploadService.documentExists(originalFileName)) {
    // Get current version
    String currentVersion = documentUploadService.getCurrentVersion(originalFileName);
    log.info("Document exists with version: {}", currentVersion);
    
    // Get next version
    String nextVersion = documentUploadService.getNextVersionForDocument(originalFileName);
    log.info("Next version will be: {}", nextVersion);
}
```

---

### Use Case 2: Manual Version Control
```java
// Get current version components
String currentVersion = documentUploadService.getCurrentVersion(originalFileName);
int[] versionParts = documentUploadService.parseVersion(currentVersion);

int major = versionParts[0];
int minor = versionParts[1];

// Custom logic: increment major version if minor reaches 10
if (minor >= 10) {
    major++;
    minor = 0;
}

String newVersion = documentUploadService.buildVersion(major, minor);
document.setVersion(newVersion);
```

---

### Use Case 3: Retrieve Latest Document
```java
String originalFileName = "Accord_Concession_ACC-2025-001.pdf";

Optional<Document> latestDoc = documentUploadService
    .getLatestDocumentByOriginalFileName(originalFileName);

if (latestDoc.isPresent()) {
    Document doc = latestDoc.get();
    log.info("Latest document: ID={}, Version={}, Created={}", 
            doc.getId(), doc.getVersion(), doc.getCreatedAt());
    
    // Compare with new upload
    String nextVersion = documentUploadService.incrementVersion(
        doc.getVersion(), originalFileName);
    log.info("New upload will be version: {}", nextVersion);
}
```

---

### Use Case 4: Batch Version Check
```java
List<String> fileNames = Arrays.asList(
    "Accord_Concession_ACC-2025-001.pdf",
    "Accord_Concession_ACC-2025-002.pdf",
    "Accord_Concession_ACC-2025-003.pdf"
);

Map<String, String> versionMap = new HashMap<>();

for (String fileName : fileNames) {
    String version = documentUploadService.getCurrentVersion(fileName);
    versionMap.put(fileName, version);
}

log.info("Current versions: {}", versionMap);
// Output: {
//   "Accord_Concession_ACC-2025-001.pdf": "1.5",
//   "Accord_Concession_ACC-2025-002.pdf": "2.0",
//   "Accord_Concession_ACC-2025-003.pdf": "1.0"
// }
```

---

## Testing

### Unit Test Example: DocumentUploadServiceTest

```java
@SpringBootTest
class DocumentUploadServiceTest {

    @Autowired
    private DocumentUploadService documentUploadService;
    
    @MockBean
    private DocumentRepository documentRepository;
    
    @Test
    void testGetNextVersionForDocument_NoExisting() {
        // Given
        String originalFileName = "Test_Document.pdf";
        when(documentRepository.findTopByOriginalFileNameOrderByIdDesc(originalFileName))
            .thenReturn(Optional.empty());
        
        // When
        String version = documentUploadService.getNextVersionForDocument(originalFileName);
        
        // Then
        assertEquals("1.0", version);
    }
    
    @Test
    void testGetNextVersionForDocument_ExistingVersion() {
        // Given
        String originalFileName = "Test_Document.pdf";
        Document existingDoc = new Document();
        existingDoc.setVersion("1.5");
        
        when(documentRepository.findTopByOriginalFileNameOrderByIdDesc(originalFileName))
            .thenReturn(Optional.of(existingDoc));
        
        // When
        String version = documentUploadService.getNextVersionForDocument(originalFileName);
        
        // Then
        assertEquals("1.6", version);
    }
    
    @Test
    void testIncrementVersion_ValidVersion() {
        // When
        String newVersion = documentUploadService.incrementVersion("2.5", "test.pdf");
        
        // Then
        assertEquals("2.6", newVersion);
    }
    
    @Test
    void testIncrementVersion_InvalidVersion() {
        // When
        String newVersion = documentUploadService.incrementVersion("invalid", "test.pdf");
        
        // Then
        assertEquals("1.0", newVersion);
    }
    
    @Test
    void testParseVersion_ValidVersion() {
        // When
        int[] parts = documentUploadService.parseVersion("3.7");
        
        // Then
        assertArrayEquals(new int[]{3, 7}, parts);
    }
    
    @Test
    void testBuildVersion() {
        // When
        String version = documentUploadService.buildVersion(5, 12);
        
        // Then
        assertEquals("5.12", version);
    }
}
```

---

## Logging Output Examples

### New Document Creation
```
DEBUG DocumentUploadService - Checking version for document with original file name: Accord_Concession_ACC-2025-001.pdf
INFO  DocumentUploadService - No existing document found with name 'Accord_Concession_ACC-2025-001.pdf'. Setting version to 1.0
INFO  AccordConcessionController - Created document with ID: 1 and version: 1.0 for concession agreement: ACC-2025-001
```

### Existing Document (Version Increment)
```
DEBUG DocumentUploadService - Checking version for document with original file name: Accord_Concession_ACC-2025-001.pdf
DEBUG DocumentUploadService - Parsed version '1.5' to major=1, minor=5
INFO  DocumentUploadService - Found existing document 'Accord_Concession_ACC-2025-001.pdf' with version 1.5. New version will be: 1.6
INFO  AccordConcessionController - Created document with ID: 7 and version: 1.6 for concession agreement: ACC-2025-001
```

### Invalid Version (Fallback)
```
DEBUG DocumentUploadService - Checking version for document with original file name: Accord_Concession_ACC-2025-001.pdf
WARN  DocumentUploadService - Failed to parse version 'invalid'. Returning [1, 0]. Error: For input string: "invalid"
WARN  DocumentUploadService - Could not parse version 'invalid' for document 'Accord_Concession_ACC-2025-001.pdf'. Using default '1.0'. Error: For input string: "invalid"
INFO  AccordConcessionController - Created document with ID: 8 and version: 1.0 for concession agreement: ACC-2025-001
```

---

## Migration Guide for Other Controllers

### Step 1: Add Dependency Injection

**In Controller Constructor:**
```java
private final DocumentUploadService documentUploadService;

public YourController(
        // ... existing dependencies
        DocumentUploadService documentUploadService) {  // ADD THIS
    // ... existing assignments
    this.documentUploadService = documentUploadService;  // ADD THIS
}
```

---

### Step 2: Replace Version Logic

**Find this pattern in your controller:**
```java
// OLD PATTERN
Optional<Document> existingDocOpt = documentRepository.findTopByOriginalFileNameOrderByIdDesc(originalFileName);
String newVersion = "1.0";

if (existingDocOpt.isPresent()) {
    Document existingDoc = existingDocOpt.get();
    String currentVersion = existingDoc.getVersion();
    
    if (currentVersion != null && !currentVersion.isEmpty()) {
        try {
            String[] versionParts = currentVersion.split("\\.");
            // ... more logic
        } catch (NumberFormatException e) {
            // ...
        }
    }
}

document.setVersion(newVersion);
```

**Replace with:**
```java
// NEW PATTERN
String newVersion = documentUploadService.getNextVersionForDocument(originalFileName);
document.setVersion(newVersion);
```

---

### Step 3: Add Import

**At the top of your controller:**
```java
import com.bar.gestiondesfichier.document.service.DocumentUploadService;
```

---

## Summary

✅ **Refactored version checking logic into reusable service**

### Key Achievements:
1. ✅ Created `DocumentUploadService` with 7 utility methods
2. ✅ Reduced controller code by 96% (54 lines → 2 lines)
3. ✅ Enabled reusability across all document controllers
4. ✅ Improved maintainability and testability
5. ✅ Separated business logic from controller layer
6. ✅ Comprehensive logging for debugging
7. ✅ Robust error handling with fallbacks

### Files Modified:
- `DocumentUploadService.java` - **CREATED** (195 lines)
- `AccordConcessionController.java` - **REFACTORED** (52 lines removed)

### Status:
🟢 **READY FOR USE**

**Next Steps:**
1. Apply same pattern to other controllers (NormeLoiController, EstateController, etc.)
2. Add unit tests for DocumentUploadService
3. Update integration tests for AccordConcessionController
4. Consider extending with major version increment logic

---

## Related Documentation
- [DOCUMENT_VERSION_INCREMENT.md](DOCUMENT_VERSION_INCREMENT.md) - Original version increment feature documentation
- [COPILOT REQUIREMENTS DOCUMENT.md](COPILOT%20REQUIREMENTS%20DOCUMENT.md) - Project requirements

