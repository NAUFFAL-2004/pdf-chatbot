package com.pdfchatbox.model;

import java.util.List;

public class StoredPdf {

    private String id;
    private String fileName;
    private List<String> chunks;

    public StoredPdf(String id, String fileName, List<String> chunks) {
        this.id = id;
        this.fileName = fileName;
        this.chunks = chunks;
    }

    public StoredPdf() {
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getChunks() {
        return chunks;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setChunks(List<String> chunks) {
        this.chunks = chunks;
    }
}
