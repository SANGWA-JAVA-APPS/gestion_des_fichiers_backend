package com.bar.gestiondesfichier.document.reporting;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/document/reporting")
@DocumentControllerCors
@Tag(name = "Reporting", description = "Reporting metrics and summaries")
public class ReportingController {

    private final ReportingRepository reportingRepository;

    public ReportingController(ReportingRepository reportingRepository) {
        this.reportingRepository = reportingRepository;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get reporting summary", description = "Retrieve summary metrics for reporting")
    public ResponseEntity<Map<String, Object>> getSummary(
            @Parameter(description = "Minimum documents per entity to be considered overloaded")
            @RequestParam(defaultValue = "100") Integer documentThreshold) {
        ReportingSummaryProjection summary = reportingRepository.getSummary(documentThreshold);
        return ResponseUtil.success(summary, "Reporting summary retrieved successfully");
    }

    @GetMapping("/document-types")
    @Operation(summary = "Get document type counts", description = "Retrieve counts of documents by type")
    public ResponseEntity<Map<String, Object>> getDocumentTypeCounts() {
        List<DocumentTypeCountProjection> counts = reportingRepository.getDocumentTypeCounts();
        return ResponseUtil.success(counts, "Document type counts retrieved successfully");
    }

    @GetMapping("/file-status")
    @Operation(summary = "Get file status counts", description = "Retrieve counts of files by status")
    public ResponseEntity<Map<String, Object>> getFileStatusCounts() {
        List<FileStatusCountProjection> counts = reportingRepository.getFileStatusCounts();
        return ResponseUtil.success(counts, "File status counts retrieved successfully");
    }
}
