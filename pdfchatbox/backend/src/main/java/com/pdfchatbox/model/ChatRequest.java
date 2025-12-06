package com.pdfchatbox.model;

public class ChatRequest {

    private String pdfId;
    private String question;

    public ChatRequest() {
    }

    public ChatRequest(String pdfId, String question) {
        this.pdfId = pdfId;
        this.question = question;
    }

    public String getPdfId() {
        return pdfId;
    }

    public void setPdfId(String pdfId) {
        this.pdfId = pdfId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
