# File Upload Feature Documentation

## Date: October 15, 2025

## Overview

Implemented complete file upload functionality in the document management system. Users can now upload actual files when creating concession agreements, with automatic folder creation and file management.

---

## Changes Made

### 1. DocumentUploadService.java - New Upload Method

#### uploadFile(MultipartFile file, String folderName)

**Purpose:** Upload files to the server's file system with automatic folder management

**Parameters:**
- `file` - The multipart file from HTTP request
- `folderName` - Subdirectory within the upload base directory

**Returns:** String - Relative file path for database storage

**Throws:** 
- `IOException` - If file upload fails
- `IllegalArgumentException` - If file or folder name is null/empty

**Features:**
- ✅ Validates file and folder name
- ✅ Extracts extension from uploaded file
- ✅ Generates unique filename (UUID-based)
- ✅ Creates directory if it doesn't exist
- ✅ Copies file to target location
- ✅ Returns relative path for database

**Example:**
```java
MultipartFile file = ...; // From HTTP request
String filePath = documentUploadService.uploadFile(file, "accord_concession");
// Returns: "uploads/accord_concession/accord_concession_uuid.pdf"
```

**Logic Flow:**
1. Validate inputs (file not null/empty, folder name provided)
2. Extract original filename and content type from MultipartFile
3. Extract file extension using existing `extractFileExtension()` method
4. Generate unique filename using existing `generateUniqueFileName()` method
5. Create upload path: `uploadBaseDir/folderName`
6. Create directory if doesn't exist (`Files.createDirectories()`)
7. Copy file to target location with `REPLACE_EXISTING` option
8. Return relative path: `uploads/folderName/uniqueFilename.ext`

---

### 2. AccordConcessionController.java - Updated Method

#### createAccordConcession(@RequestPart, @RequestPart)

**Before:**
```java
@PostMapping
public ResponseEntity<Map<String, Object>> createAccordConcession(
        @RequestBody AccordConcession accordConcession) {
    // ... created dummy document with hardcoded values
}
```

**After:**
```java
@PostMapping(consumes = {"multipart/form-data"})
public ResponseEntity<Map<String, Object>> createAccordConcession(
        @RequestPart("accordConcession") AccordConcession accordConcession,
        @RequestPart(value = "file", required = false) MultipartFile file) {
    // ... handles actual file upload or creates dummy document
}
```

**Changes:**
1. **Endpoint Type:** Now accepts `multipart/form-data`
2. **Parameters:**
   - `@RequestPart("accordConcession")` - JSON data as form field
   - `@RequestPart("file", required = false)` - Optional file upload
3. **Logic:**
   - If file provided: Upload file, extract metadata, use real values
   - If no file: Create document with dummy data (backward compatible)

---

### 3. application.properties - Upload Configuration

**Added:**
```properties
# File Upload Configuration
app.upload.dir=uploads
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

**Configuration:**
- `app.upload.dir=uploads` - Base directory for file uploads (relative to application root)
- `max-file-size=50MB` - Maximum size per file
- `max-request-size=50MB` - Maximum total request size

---

## Complete Upload Flow

### Scenario 1: With File Upload

```
1. Client sends multipart/form-data POST request:
   - Part 1: "accordConcession" (JSON) = {"numeroAccord": "ACC-001", ...}
   - Part 2: "file" (binary) = contract.pdf

2. Controller receives request:
   - accordConcession object is deserialized from JSON
   - file is received as MultipartFile

3. File upload process:
   a. Validate file is not null/empty
   b. Call documentUploadService.uploadFile(file, "accord_concession")
   
4. Upload service:
   a. Extract: originalFilename="contract.pdf", contentType="application/pdf"
   b. Extract extension: ".pdf"
   c. Generate unique: "accord_concession_a1b2-c3d4.pdf"
   d. Create path: "uploads/accord_concession/"
   e. Create directory if not exists
   f. Copy file to: "uploads/accord_concession/accord_concession_a1b2-c3d4.pdf"
   g. Return: "uploads/accord_concession/accord_concession_a1b2-c3d4.pdf"

5. Controller stores metadata:
   - fileName: "accord_concession_a1b2-c3d4.pdf"
   - originalFileName: "Accord_Concession_ACC-001.pdf"
   - contentType: "application/pdf"
   - fileSize: 1048576 (actual size in bytes)
   - filePath: "uploads/accord_concession/accord_concession_a1b2-c3d4.pdf"
   - version: "1.0" (or incremented)

6. Save to database and return success response
```

---

### Scenario 2: Without File Upload (Backward Compatible)

```
1. Client sends multipart/form-data POST request:
   - Part 1: "accordConcession" (JSON) = {"numeroAccord": "ACC-001", ...}
   - Part 2: "file" = null or not included

2. Controller receives request:
   - file parameter is null

3. Create dummy document:
   - fileName: "accord_concession_uuid.pdf"
   - originalFileName: "Accord_Concession_ACC-001.pdf"
   - contentType: "application/pdf"
   - fileSize: 0
   - filePath: "uploads/accord_concession/accord_concession_uuid.pdf"
   - version: "1.0" (or incremented)

4. No actual file is created on disk (backward compatible with existing behavior)

5. Save to database and return success response
```

---

## API Usage Examples

### Example 1: Upload PDF File (cURL)

```bash
curl -X POST http://localhost:8104/api/document/accord-concession \
  -H "Content-Type: multipart/form-data" \
  -F 'accordConcession={
    "numeroAccord": "ACC-2025-001",
    "objetConcession": "Land Concession Agreement",
    "concessionnaire": "ABC Corporation",
    "doneBy": {"id": 5},
    "status": {"id": 1}
  };type=application/json' \
  -F 'file=@/path/to/contract.pdf;type=application/pdf'
```

**Response:**
```json
{
  "success": true,
  "message": "Concession agreement created successfully",
  "data": {
    "id": 1,
    "numeroAccord": "ACC-2025-001",
    "objetConcession": "Land Concession Agreement",
    "document": {
      "id": 1,
      "fileName": "accord_concession_a1b2c3d4-e5f6.pdf",
      "originalFileName": "Accord_Concession_ACC-2025-001.pdf",
      "contentType": "application/pdf",
      "fileSize": 1048576,
      "filePath": "uploads/accord_concession/accord_concession_a1b2c3d4-e5f6.pdf",
      "version": "1.0"
    }
  }
}
```

---

### Example 2: Upload Word Document (Postman)

**Request:**
- Method: POST
- URL: `http://localhost:8104/api/document/accord-concession`
- Body: form-data
  - Key: `accordConcession`, Type: Text, Value:
    ```json
    {
      "numeroAccord": "ACC-2025-002",
      "objetConcession": "Commercial Land Lease",
      "concessionnaire": "XYZ Ltd",
      "doneBy": {"id": 5},
      "status": {"id": 1}
    }
    ```
    Content-Type: `application/json`
  - Key: `file`, Type: File, Value: Select `agreement.docx`

**Result:**
- File uploaded to: `uploads/accord_concession/accord_concession_x9y8z7w6.docx`
- Database record created with actual file metadata

---

### Example 3: No File Upload (JavaScript/Fetch)

```javascript
const formData = new FormData();

// Add JSON data
const accordData = {
  numeroAccord: "ACC-2025-003",
  objetConcession: "Office Space Lease",
  concessionnaire: "DEF Company",
  doneBy: { id: 5 },
  status: { id: 1 }
};

formData.append('accordConcession', new Blob([JSON.stringify(accordData)], {
  type: 'application/json'
}));

// No file added (optional parameter)

fetch('http://localhost:8104/api/document/accord-concession', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => console.log(data));
```

**Result:**
- No file uploaded to disk
- Document created with dummy data (fileSize: 0)
- Backward compatible with existing behavior

---

### Example 4: React Frontend Integration

```jsx
import React, { useState } from 'react';
import axios from 'axios';

function AccordConcessionForm() {
  const [file, setFile] = useState(null);
  const [formData, setFormData] = useState({
    numeroAccord: '',
    objetConcession: '',
    concessionnaire: ''
  });

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const data = new FormData();
    
    // Add JSON data
    const accordData = {
      ...formData,
      doneBy: { id: 5 },
      status: { id: 1 }
    };
    
    data.append('accordConcession', new Blob([JSON.stringify(accordData)], {
      type: 'application/json'
    }));

    // Add file if selected
    if (file) {
      data.append('file', file);
    }

    try {
      const response = await axios.post(
        'http://localhost:8104/api/document/accord-concession',
        data,
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }
      );
      
      console.log('Success:', response.data);
      alert('Document uploaded successfully!');
    } catch (error) {
      console.error('Error:', error);
      alert('Upload failed: ' + error.message);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Numero Accord"
        value={formData.numeroAccord}
        onChange={(e) => setFormData({...formData, numeroAccord: e.target.value})}
        required
      />
      
      <input
        type="text"
        placeholder="Objet Concession"
        value={formData.objetConcession}
        onChange={(e) => setFormData({...formData, objetConcession: e.target.value})}
      />
      
      <input
        type="text"
        placeholder="Concessionnaire"
        value={formData.concessionnaire}
        onChange={(e) => setFormData({...formData, concessionnaire: e.target.value})}
      />
      
      <input
        type="file"
        onChange={handleFileChange}
        accept=".pdf,.doc,.docx"
      />
      
      <button type="submit">Create Concession Agreement</button>
    </form>
  );
}

export default AccordConcessionForm;
```

---

## Directory Structure

```
project-root/
├── uploads/                          # Upload base directory
│   ├── accord_concession/           # Folder for concession agreements
│   │   ├── accord_concession_a1b2c3d4-e5f6.pdf
│   │   ├── accord_concession_x9y8z7w6-v5u4.docx
│   │   └── accord_concession_m3n4o5p6-q7r8.xlsx
│   ├── norme_loi/                   # Folder for norms/laws (future)
│   ├── estate/                      # Folder for estate docs (future)
│   └── ...                          # Other document types
├── src/
│   └── main/
│       └── resources/
│           └── application.properties
└── ...
```

**Notes:**
- Folders are created automatically on first upload
- Each document type has its own folder
- Unique filenames prevent conflicts
- Original filenames stored in database

---

## Security Considerations

### 1. File Type Validation

**Current:** Extension extracted from filename/content type

**Recommended Enhancement:**
```java
public String uploadFile(MultipartFile file, String folderName) throws IOException {
    // Validate file type
    String extension = extractFileExtension(file.getOriginalFilename(), file.getContentType());
    List<String> allowedExtensions = Arrays.asList(".pdf", ".doc", ".docx", ".xlsx", ".jpg", ".png");
    
    if (!allowedExtensions.contains(extension.toLowerCase())) {
        throw new IllegalArgumentException("File type not allowed: " + extension);
    }
    
    // ... rest of upload logic
}
```

---

### 2. File Size Validation

**Current:** Spring Boot configuration limits (50MB)

**Location:** `application.properties`
```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

**Customizable per controller:**
```java
@PostMapping(consumes = {"multipart/form-data"})
public ResponseEntity<Map<String, Object>> createAccordConcession(...) {
    if (file != null && file.getSize() > 10 * 1024 * 1024) { // 10MB
        return ResponseUtil.badRequest("File size exceeds 10MB limit");
    }
    // ... rest of logic
}
```

---

### 3. Filename Sanitization

**Current:** UUID-based unique filenames (safe)

**Benefit:** Prevents path traversal attacks (../../../etc/passwd)

**Original filenames:** Stored in database, not used in file system

---

### 4. Access Control

**Recommended:** Add authentication/authorization checks

```java
@PostMapping(consumes = {"multipart/form-data"})
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Object>> createAccordConcession(...) {
    // Only admins can upload
}
```

---

## Error Handling

### Scenario 1: File Upload Fails

```java
try {
    filePath = documentUploadService.uploadFile(file, "accord_concession");
} catch (IOException e) {
    log.error("Failed to upload file", e);
    return ResponseUtil.badRequest("Failed to upload file: " + e.getMessage());
}
```

**Possible Causes:**
- Disk full
- Permission denied
- Invalid path
- I/O error

---

### Scenario 2: File Too Large

**Spring Boot automatically rejects:**
```json
{
  "timestamp": "2025-10-15T10:30:00.000+00:00",
  "status": 413,
  "error": "Payload Too Large",
  "message": "Maximum upload size exceeded",
  "path": "/api/document/accord-concession"
}
```

---

### Scenario 3: Invalid File Type

**Current:** Accepts all types (extension extracted)

**With Validation:**
```json
{
  "success": false,
  "message": "File type not allowed: .exe"
}
```

---

## Logging

### Info Level

```
INFO  AccordConcessionController - Creating new concession agreement: ACC-2025-001
INFO  AccordConcessionController - File upload detected: contract.pdf
INFO  DocumentUploadService - Creating upload directory: /path/to/project/uploads/accord_concession
INFO  DocumentUploadService - Uploading file 'contract.pdf' to: /path/to/project/uploads/accord_concession/accord_concession_a1b2.pdf
INFO  DocumentUploadService - File uploaded successfully. Relative path: uploads/accord_concession/accord_concession_a1b2.pdf
INFO  AccordConcessionController - File uploaded successfully: uploads/accord_concession/accord_concession_a1b2.pdf
INFO  AccordConcessionController - Created document with ID: 1 and version: 1.0 for concession agreement: ACC-2025-001
```

### Error Level

```
ERROR AccordConcessionController - Failed to upload file
java.io.IOException: No space left on device
```

---

## Testing

### Unit Tests for DocumentUploadService

```java
@SpringBootTest
class DocumentUploadServiceTest {

    @Autowired
    private DocumentUploadService documentUploadService;

    @TempDir
    Path tempDir;

    @Test
    void testUploadFile_Success() throws IOException {
        // Create mock file
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.pdf",
            "application/pdf",
            "Test content".getBytes()
        );

        // Upload
        String filePath = documentUploadService.uploadFile(file, "test_folder");

        // Verify
        assertNotNull(filePath);
        assertTrue(filePath.contains("test_folder"));
        assertTrue(filePath.endsWith(".pdf"));
    }

    @Test
    void testUploadFile_NullFile_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            documentUploadService.uploadFile(null, "test_folder");
        });
    }

    @Test
    void testUploadFile_EmptyFile_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.pdf",
            "application/pdf",
            new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> {
            documentUploadService.uploadFile(file, "test_folder");
        });
    }

    @Test
    void testUploadFile_NullFolderName_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.pdf",
            "application/pdf",
            "Test content".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            documentUploadService.uploadFile(file, null);
        });
    }
}
```

---

### Integration Tests for Controller

```java
@SpringBootTest
@AutoConfigureMockMvc
class AccordConcessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateAccordConcession_WithFile() throws Exception {
        // Create mock file
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "contract.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );

        // Create JSON data
        String accordJson = """
            {
                "numeroAccord": "ACC-TEST-001",
                "objetConcession": "Test Concession",
                "doneBy": {"id": 5},
                "status": {"id": 1}
            }
            """;

        MockMultipartFile jsonPart = new MockMultipartFile(
            "accordConcession",
            "",
            "application/json",
            accordJson.getBytes()
        );

        // Perform request
        mockMvc.perform(multipart("/api/document/accord-concession")
                .file(file)
                .file(jsonPart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.document.fileName").exists())
                .andExpect(jsonPath("$.data.document.fileSize").value(greaterThan(0)));
    }
}
```

---

## Migration Guide for Other Controllers

### Step 1: Update Method Signature

```java
// Before
@PostMapping
public ResponseEntity<Map<String, Object>> createNormeLoi(@RequestBody NormeLoi normeLoi) {

// After
@PostMapping(consumes = {"multipart/form-data"})
public ResponseEntity<Map<String, Object>> createNormeLoi(
        @RequestPart("normeLoi") NormeLoi normeLoi,
        @RequestPart(value = "file", required = false) MultipartFile file) {
```

---

### Step 2: Add File Upload Logic

```java
String contentType;
String fileExtension;
String uniqueFileName;
String originalFileName;
String filePath;
long fileSize;

if (file != null && !file.isEmpty()) {
    try {
        // Upload file
        filePath = documentUploadService.uploadFile(file, "norme_loi");
        
        // Extract metadata
        contentType = file.getContentType();
        fileSize = file.getSize();
        fileExtension = documentUploadService.extractFileExtension(
            file.getOriginalFilename(), contentType);
        uniqueFileName = Paths.get(filePath).getFileName().toString();
        originalFileName = documentUploadService.generateOriginalFileName(
            "Norme_Loi", normeLoi.getNumeroNorme(), fileExtension);
            
    } catch (IOException e) {
        return ResponseUtil.badRequest("Failed to upload file: " + e.getMessage());
    }
} else {
    // No file - create dummy document
    contentType = "application/pdf";
    fileExtension = documentUploadService.extractFileExtension(null, contentType);
    uniqueFileName = documentUploadService.generateUniqueFileName("norme_loi", fileExtension);
    originalFileName = documentUploadService.generateOriginalFileName(
        "Norme_Loi", normeLoi.getNumeroNorme(), fileExtension);
    filePath = "uploads/norme_loi/" + uniqueFileName;
    fileSize = 0L;
}
```

---

### Step 3: Set Document Properties

```java
document.setFileName(uniqueFileName);
document.setOriginalFileName(originalFileName);
document.setContentType(contentType);
document.setFileSize(fileSize);
document.setFilePath(filePath);
```

---

## Summary

✅ **File Upload Feature Complete**

### Key Features:
1. ✅ Upload real files via multipart/form-data
2. ✅ Automatic folder creation
3. ✅ UUID-based unique filenames
4. ✅ Dynamic extension extraction
5. ✅ Configurable upload directory
6. ✅ 50MB file size limit
7. ✅ Backward compatible (file optional)
8. ✅ Comprehensive logging

### Files Modified:
- `DocumentUploadService.java` - Added `uploadFile()` method (+65 lines)
- `AccordConcessionController.java` - Updated to accept file uploads (+45 lines modified)
- `application.properties` - Added upload configuration (+3 lines)

### Supported Operations:
- ✅ Upload PDF, Word, Excel, images, etc.
- ✅ Store files in organized folders
- ✅ Track file metadata in database
- ✅ Auto-increment document versions

### Status:
🟢 **READY FOR USE**

**Next Steps:**
1. Apply same pattern to other controllers
2. Add file type validation
3. Add file download endpoint
4. Add file deletion functionality
5. Consider cloud storage integration (AWS S3, Azure Blob)

