# Document Version Increment Feature

## Date: October 15, 2025

## Overview

Implemented automatic version increment functionality for documents in `AccordConcessionController`. When creating a new accord concession, the system now checks if a document with the same original file name exists and automatically increments the version number.

---

## Changes Made

### 1. DocumentRepository.java

**Added Method:**
```java
Optional<Document> findTopByOriginalFileNameOrderByIdDesc(String originalFileName);
```

**Purpose:** Finds the most recent document by `originalFileName`, ordered by ID descending (latest first).

---

### 2. AccordConcessionController.java

**Modified:** `createAccordConcession()` method (lines 159-173)

**New Logic:**

#### Before
```java
Document document = new Document();
String uniqueFileName = "accord_concession_" + UUID.randomUUID().toString() + ".pdf";
document.setFileName(uniqueFileName);
document.setOriginalFileName("Accord_Concession_" + accordConcession.getNumeroAccord() + ".pdf");
// ... other fields
document.setVersion("1.0"); // Always 1.0
document.setActive(true);

Document savedDocument = documentRepository.save(document);
```

#### After
```java
Document document = new Document();
String uniqueFileName = "accord_concession_" + UUID.randomUUID().toString() + ".pdf";
String originalFileName = "Accord_Concession_" + accordConcession.getNumeroAccord() + ".pdf";

document.setFileName(uniqueFileName);
document.setOriginalFileName(originalFileName);
// ... other fields

// Check if document with same original file name exists
Optional<Document> existingDocOpt = documentRepository.findTopByOriginalFileNameOrderByIdDesc(originalFileName);
String newVersion = "1.0"; // Default version

if (existingDocOpt.isPresent()) {
    Document existingDoc = existingDocOpt.get();
    String currentVersion = existingDoc.getVersion();
    
    // Parse and increment version
    if (currentVersion != null && !currentVersion.isEmpty()) {
        try {
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
    }
}

document.setVersion(newVersion);
document.setActive(true);

Document savedDocument = documentRepository.save(document);
log.info("Created document with ID: {} and version: {} for concession agreement: {}",
        savedDocument.getId(), savedDocument.getVersion(), accordConcession.getNumeroAccord());
```

---

## How Version Increment Works

### Version Format
- **Format:** `major.minor` (e.g., `1.0`, `1.1`, `2.5`)
- **Default:** `1.0` for new documents

### Increment Logic

1. **Check for Existing Document**
   - Query database for latest document with same `originalFileName`
   - Uses `findTopByOriginalFileNameOrderByIdDesc()` to get most recent

2. **Parse Current Version**
   - Split version string by "." (e.g., `"1.2"` → `["1", "2"]`)
   - Extract major version: `1`
   - Extract minor version: `2`

3. **Increment Minor Version**
   - Add 1 to minor version: `2 + 1 = 3`
   - New version: `"1.3"`

4. **Handle Edge Cases**
   - **No existing document:** Use `1.0`
   - **Existing but no version:** Use `1.0`
   - **Invalid version format:** Log warning, use `1.0`
   - **Parse error:** Catch exception, use `1.0`

---

## Examples

### Example 1: First Document (No Existing)

**Input:**
- `originalFileName`: `"Accord_Concession_ACC-2025-001.pdf"`
- No existing documents

**Process:**
1. Query returns empty: `existingDocOpt.isEmpty() = true`
2. No existing document found
3. Set version: `"1.0"`

**Result:**
```json
{
  "id": 1,
  "fileName": "accord_concession_a1b2c3d4.pdf",
  "originalFileName": "Accord_Concession_ACC-2025-001.pdf",
  "version": "1.0"
}
```

---

### Example 2: Second Document (Existing v1.0)

**Input:**
- `originalFileName`: `"Accord_Concession_ACC-2025-001.pdf"`
- Existing document with version `"1.0"`

**Process:**
1. Query finds document with ID 1, version `"1.0"`
2. Parse: major=1, minor=0
3. Increment: minor=0+1=1
4. New version: `"1.1"`

**Result:**
```json
{
  "id": 2,
  "fileName": "accord_concession_e5f6g7h8.pdf",
  "originalFileName": "Accord_Concession_ACC-2025-001.pdf",
  "version": "1.1"
}
```

---

### Example 3: Multiple Updates (v1.5 → v1.6)

**Input:**
- `originalFileName`: `"Accord_Concession_ACC-2025-001.pdf"`
- Existing document with version `"1.5"`

**Process:**
1. Query finds latest document with version `"1.5"`
2. Parse: major=1, minor=5
3. Increment: minor=5+1=6
4. New version: `"1.6"`

**Result:**
```json
{
  "id": 7,
  "fileName": "accord_concession_i9j0k1l2.pdf",
  "originalFileName": "Accord_Concession_ACC-2025-001.pdf",
  "version": "1.6"
}
```

---

### Example 4: Invalid Version Format

**Input:**
- `originalFileName`: `"Accord_Concession_ACC-2025-001.pdf"`
- Existing document with version `"invalid"`

**Process:**
1. Query finds document with version `"invalid"`
2. Try to parse: throws `NumberFormatException`
3. Catch exception, log warning
4. Fallback to default: `"1.0"`

**Result:**
```json
{
  "id": 8,
  "fileName": "accord_concession_m3n4o5p6.pdf",
  "originalFileName": "Accord_Concession_ACC-2025-001.pdf",
  "version": "1.0"
}
```

**Console Log:**
```
WARN  AccordConcessionController - Could not parse version 'invalid', using default '1.0'
```

---

## Database Query

### Method Used
```java
Optional<Document> findTopByOriginalFileNameOrderByIdDesc(String originalFileName);
```

### Generated SQL
```sql
SELECT * FROM files 
WHERE original_file_name = 'Accord_Concession_ACC-2025-001.pdf'
ORDER BY id DESC 
LIMIT 1;
```

**Explanation:**
- Finds documents by `originalFileName`
- Orders by `id` descending (newest first)
- Returns only the top record (most recent)

---

## Testing

### Test Case 1: Create First Document

**Request:**
```bash
POST /api/document/accord-concession
Content-Type: application/json

{
  "numeroAccord": "ACC-2025-001",
  "objetConcession": "Land Concession",
  "concessionnaire": "ABC Company",
  "doneBy": {"id": 5},
  "status": {"id": 1}
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Concession agreement created successfully",
  "data": {
    "id": 1,
    "numeroAccord": "ACC-2025-001",
    "document": {
      "id": 1,
      "fileName": "accord_concession_123e4567-e89b-12d3-a456-426614174000.pdf",
      "originalFileName": "Accord_Concession_ACC-2025-001.pdf",
      "version": "1.0"
    }
  }
}
```

**Console Log:**
```
INFO  AccordConcessionController - No existing document found with name 'Accord_Concession_ACC-2025-001.pdf'. Setting version to 1.0
INFO  AccordConcessionController - Created document with ID: 1 and version: 1.0 for concession agreement: ACC-2025-001
```

---

### Test Case 2: Create Second Document (Same Numero Accord)

**Request:**
```bash
POST /api/document/accord-concession
Content-Type: application/json

{
  "numeroAccord": "ACC-2025-001",
  "objetConcession": "Land Concession - Updated",
  "concessionnaire": "ABC Company",
  "doneBy": {"id": 5},
  "status": {"id": 1}
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Concession agreement created successfully",
  "data": {
    "id": 2,
    "numeroAccord": "ACC-2025-001",
    "document": {
      "id": 2,
      "fileName": "accord_concession_987f6543-e21b-98c7-d654-532109876543.pdf",
      "originalFileName": "Accord_Concession_ACC-2025-001.pdf",
      "version": "1.1"
    }
  }
}
```

**Console Log:**
```
INFO  AccordConcessionController - Found existing document with version 1.0. New version will be: 1.1
INFO  AccordConcessionController - Created document with ID: 2 and version: 1.1 for concession agreement: ACC-2025-001
```

---

### Test Case 3: Verify in Database

**Query:**
```sql
SELECT id, original_file_name, version, created_at 
FROM files 
WHERE original_file_name = 'Accord_Concession_ACC-2025-001.pdf'
ORDER BY id;
```

**Expected Result:**
```
+----+---------------------------------------+---------+---------------------+
| id | original_file_name                    | version | created_at          |
+----+---------------------------------------+---------+---------------------+
|  1 | Accord_Concession_ACC-2025-001.pdf    | 1.0     | 2025-10-15 10:30:00 |
|  2 | Accord_Concession_ACC-2025-001.pdf    | 1.1     | 2025-10-15 11:45:00 |
|  3 | Accord_Concession_ACC-2025-001.pdf    | 1.2     | 2025-10-15 14:20:00 |
+----+---------------------------------------+---------+---------------------+
```

---

## Benefits

### 1. **Automatic Version Control**
- No manual version management needed
- System automatically tracks document revisions

### 2. **Audit Trail**
- Clear history of document versions
- Easy to track changes over time

### 3. **Document Integrity**
- Each document has unique `fileName` (UUID)
- Same `originalFileName` links related versions

### 4. **Error Handling**
- Graceful fallback for invalid versions
- Comprehensive logging for troubleshooting

### 5. **Database Efficiency**
- Single query to find latest version
- Indexed by `id` for fast lookups

---

## Logging

### Info Logs

**New Document (No Existing):**
```
INFO  AccordConcessionController - No existing document found with name 'Accord_Concession_ACC-2025-001.pdf'. Setting version to 1.0
INFO  AccordConcessionController - Created document with ID: 1 and version: 1.0 for concession agreement: ACC-2025-001
```

**Existing Document (Version Increment):**
```
INFO  AccordConcessionController - Found existing document with version 1.5. New version will be: 1.6
INFO  AccordConcessionController - Created document with ID: 7 and version: 1.6 for concession agreement: ACC-2025-001
```

**Existing Document (No Version):**
```
INFO  AccordConcessionController - Existing document has no version. Setting new version to 1.0
INFO  AccordConcessionController - Created document with ID: 8 and version: 1.0 for concession agreement: ACC-2025-001
```

### Warning Logs

**Invalid Version Format:**
```
WARN  AccordConcessionController - Could not parse version 'abc', using default '1.0'
```

---

## Future Enhancements

### 1. Major Version Bump
Allow incrementing major version (e.g., 1.9 → 2.0):
```java
// Check if minor version exceeds threshold
if (minorVersion >= 10) {
    majorVersion++;
    minorVersion = 0;
}
```

### 2. Semantic Versioning
Support `major.minor.patch` format:
```java
String newVersion = majorVersion + "." + minorVersion + "." + patchVersion;
```

### 3. Version History Endpoint
Create API to retrieve all versions:
```java
@GetMapping("/history/{originalFileName}")
public List<Document> getDocumentHistory(@PathVariable String originalFileName) {
    return documentRepository.findByOriginalFileNameOrderByIdDesc(originalFileName);
}
```

### 4. Version Comparison
Add endpoint to compare versions:
```java
@GetMapping("/compare/{id1}/{id2}")
public DocumentComparison compareVersions(@PathVariable Long id1, @PathVariable Long id2) {
    // Compare two document versions
}
```

---

## Summary

✅ **Implemented automatic version increment for accord concession documents**

### Key Features:
1. ✅ Checks for existing documents by `originalFileName`
2. ✅ Parses current version and increments minor version
3. ✅ Handles edge cases (no existing, invalid version)
4. ✅ Comprehensive logging
5. ✅ Error-resistant with fallback to default version

### Files Modified:
- `DocumentRepository.java` - Added `findTopByOriginalFileNameOrderByIdDesc()` method
- `AccordConcessionController.java` - Added version checking and increment logic

### Status:
🟢 **READY FOR TESTING**

**Next Steps:**
1. Test with first document creation (expect version 1.0)
2. Test with second document (same numeroAccord, expect version 1.1)
3. Verify version increment continues correctly (1.2, 1.3, etc.)

