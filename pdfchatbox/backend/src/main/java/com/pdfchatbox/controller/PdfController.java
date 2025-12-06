package com.pdfchatbox.controller;

import com.pdfchatbox.model.StoredPdf;
import com.pdfchatbox.service.PdfService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "http://localhost:3000")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            String pdfId = pdfService.storePdf(file);
            response.put("pdfId", pdfId);
            response.put("fileName", file.getOriginalFilename());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // print full error in backend console so we see the real reason
            e.printStackTrace();
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<StoredPdf>> listPdfs() {
        return ResponseEntity.ok(pdfService.listPdfs());
    }
}

