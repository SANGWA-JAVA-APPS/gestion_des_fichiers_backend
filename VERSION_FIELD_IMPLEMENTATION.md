# Version Field Added to Document.java

## Date: October 13, 2025

## Summary

Successfully added a `version` field as a String to the `Document.java` entity class in the backend.

---

## Changes Made

### File: `Document.java`
**Location:** `d:\Apache\DEV\SPRING BOOT\gestiondesfichier\src\main\java\com\bar\gestiondesfichier\document\model\Document.java`

### Field Added

```java
@Column(name = "version", length = 50)
private String version;
```

### Field Details

- **Type:** `String`
- **Column Name:** `version` (in database)
- **Max Length:** 50 characters
- **Nullable:** Yes (null allowed by default)
- **Location:** Added after `expiryAlertSent` field and before `active` field

---

## Complete Field Section (After Changes)

```java
@Column(name = "expiry_alert_sent")
private boolean expiryAlertSent = false;

@Column(name = "version", length = 50)
private String version;

@Column(nullable = false)
private boolean active = true;
```

---

## Getter and Setter

Since the class uses Lombok's `@Getter` and `@Setter` annotations:

```java
@Entity
@Table(name = "files")
@Getter
@Setter
public class Document {
    // ... fields
}
```

The following methods are automatically generated:
- `public String getVersion()`
- `public void setVersion(String version)`

---

## Database Schema Update Required

⚠️ **Important:** You'll need to update the database schema to add this column.

### SQL Migration Script

```sql
-- Add version column to files table
ALTER TABLE files 
ADD COLUMN version VARCHAR(50) NULL;
```

### Or using Spring Boot Auto-DDL (if enabled)

If you have `spring.jpa.hibernate.ddl-auto=update` in `application.properties`, the column will be created automatically on next application start.

---

## Usage Examples

### Setting Version

```java
// In a service or controller
Document document = new Document();
document.setFileName("contract.pdf");
document.setVersion("1.0");
// ... set other fields

documentRepository.save(document);
```

### Retrieving Version

```java
Document document = documentRepository.findById(id).orElseThrow();
String version = document.getVersion();
System.out.println("Document version: " + version);
```

### Updating Version

```java
Document document = documentRepository.findById(id).orElseThrow();
document.setVersion("2.0"); // Increment version
documentRepository.save(document);
```

---

## Version Format Suggestions

### Semantic Versioning
```
"1.0.0"      // Major.Minor.Patch
"2.1.3"
"3.0.0-beta"
```

### Simple Versioning
```
"1.0"        // Major.Minor
"2.5"
"v1"         // With prefix
"v2.1"
```

### Sequential Versioning
```
"1"          // Simple incrementing
"2"
"3"
```

### Date-Based Versioning
```
"2025-10-13"
"20251013"
"2025.10.13.1"  // With revision number
```

---

## API Response Example

When returning documents via REST API, the version will be included:

```json
{
  "id": 1,
  "fileName": "contract_xyz.pdf",
  "originalFileName": "contract.pdf",
  "contentType": "application/pdf",
  "fileSize": 1048576,
  "filePath": "/uploads/2025/10/contract_xyz.pdf",
  "version": "1.0",
  "status": "ACTIVE",
  "expiryDate": "2026-10-13",
  "active": true,
  "createdAt": "2025-10-13T10:30:00",
  "updatedAt": "2025-10-13T10:30:00"
}
```

---

## Frontend Integration

To display or edit the version in the frontend, update your components:

### In Document Form (Create/Edit)

```javascript
<Form.Group className="mb-3">
  <Form.Label>Version</Form.Label>
  <Form.Control
    type="text"
    name="version"
    placeholder="e.g., 1.0, v2.1, 2025-10-13"
    value={formData.version || ''}
    onChange={handleInputChange}
    maxLength={50}
  />
  <Form.Text className="text-muted">
    Document version (optional, max 50 characters)
  </Form.Text>
</Form.Group>
```

### In Document List/Table

```javascript
<td>{document.version || 'N/A'}</td>
```

### In Document Details View

```javascript
<Card.Text>
  <strong>Version:</strong> {document.version || 'Not specified'}
</Card.Text>
```

---

## Best Practices

### 1. Version Validation

Consider adding validation in your service layer:

```java
public boolean isValidVersion(String version) {
    if (version == null || version.trim().isEmpty()) {
        return true; // Null/empty is valid (optional field)
    }
    // Validate format (example: semantic versioning)
    return version.matches("^\\d+\\.\\d+(\\.\\d+)?$");
}
```

### 2. Auto-Increment Version

Implement automatic version incrementing:

```java
public String incrementVersion(String currentVersion) {
    if (currentVersion == null || currentVersion.isEmpty()) {
        return "1.0";
    }
    
    String[] parts = currentVersion.split("\\.");
    if (parts.length >= 2) {
        int minor = Integer.parseInt(parts[1]) + 1;
        return parts[0] + "." + minor;
    }
    
    return currentVersion;
}
```

### 3. Version History

Consider creating a separate table for version history:

```java
@Entity
@Table(name = "document_versions")
public class DocumentVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
    
    @Column(name = "version")
    private String version;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "changes")
    private String changes; // Description of what changed
}
```

---

## Controller Updates (Example)

If you have a DocumentController, no changes needed as the field will automatically be included in serialization.

```java
@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    
    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody DocumentDTO dto) {
        Document document = new Document();
        document.setFileName(dto.getFileName());
        document.setVersion(dto.getVersion()); // Version from request
        // ... set other fields
        
        Document saved = documentService.save(document);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(
            @PathVariable Long id, 
            @RequestBody DocumentDTO dto) {
        
        Document document = documentService.findById(id);
        
        // Check if content changed and increment version
        if (hasContentChanged(document, dto)) {
            String newVersion = incrementVersion(document.getVersion());
            document.setVersion(newVersion);
        }
        
        // ... update other fields
        Document updated = documentService.save(document);
        return ResponseEntity.ok(updated);
    }
}
```

---

## DTO Update (if using DTOs)

Update your DocumentDTO to include version:

```java
public class DocumentDTO {
    private Long id;
    private String fileName;
    private String version;
    // ... other fields
    
    // Getters and setters
}
```

---

## Testing

### Unit Test Example

```java
@Test
public void testDocumentVersionField() {
    Document document = new Document();
    document.setFileName("test.pdf");
    document.setVersion("1.0");
    
    assertNotNull(document.getVersion());
    assertEquals("1.0", document.getVersion());
}

@Test
public void testDocumentVersionMaxLength() {
    Document document = new Document();
    String longVersion = "a".repeat(51); // 51 characters
    
    document.setVersion(longVersion);
    
    // Should throw exception or be truncated when saving
    assertThrows(DataIntegrityViolationException.class, () -> {
        documentRepository.save(document);
    });
}
```

---

## Migration Checklist

- [x] Added `version` field to `Document.java`
- [ ] Update database schema (run migration or use auto-DDL)
- [ ] Update frontend forms to include version field
- [ ] Update API documentation (if using Swagger/OpenAPI)
- [ ] Add validation logic (optional)
- [ ] Update DTOs if used
- [ ] Add tests for version field
- [ ] Update user documentation

---

## Notes

- The `version` field is **optional** (nullable) - existing documents without a version will have `null`
- Maximum length is set to 50 characters, which is sufficient for most version formats
- The field uses Lombok's `@Getter` and `@Setter`, so no manual getter/setter needed
- Database column name is explicitly set to `version` using `@Column(name = "version")`

---

## Status

✅ **Field Added Successfully** - Ready for database migration and frontend integration!

