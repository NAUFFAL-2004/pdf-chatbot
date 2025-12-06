package com.pdfchatbox.service;

import com.pdfchatbox.model.StoredPdf;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PdfService {

    private final Map<String, StoredPdf> pdfStore = new ConcurrentHashMap<>();

    public String storePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty.");
        }

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            if (document.isEncrypted()) {
                throw new RuntimeException("PDF is encrypted and cannot be read.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(document);

            if (fullText == null || fullText.trim().isEmpty()) {
                throw new RuntimeException("No readable text found in the PDF.");
            }

            List<String> chunks = new ArrayList<>();
            int chunkSize = 1000;
            for (int i = 0; i < fullText.length(); i += chunkSize) {
                chunks.add(fullText.substring(i, Math.min(fullText.length(), i + chunkSize)));
            }

            String pdfId = UUID.randomUUID().toString();
            StoredPdf storedPdf = new StoredPdf(pdfId, file.getOriginalFilename(), chunks);
            pdfStore.put(pdfId, storedPdf);

            return pdfId;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read PDF: " + e.getMessage(), e);
        }
    }

    public StoredPdf getPdf(String pdfId) {
        return pdfStore.get(pdfId);
    }

    public List<StoredPdf> listPdfs() {
        return new ArrayList<>(pdfStore.values());
    }
}

