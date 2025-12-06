package com.pdfchatbox.service;

import com.pdfchatbox.model.StoredPdf;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService {

    private final PdfService pdfService;

    // some very common words to ignore in questions
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "what", "is", "are", "the", "a", "an", "of", "in", "on", "to",
            "for", "and", "or", "explain", "about", "define", "please"
    ));

    public ChatService(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    public String answerQuestion(String pdfId, String question) {
        StoredPdf pdf = pdfService.getPdf(pdfId);
        if (pdf == null) {
            return "PDF not found. Please upload again.";
        }

        if (question == null || question.trim().isEmpty()) {
            return "Please ask a proper question.";
        }

        List<String> chunks = pdf.getChunks();
        if (chunks == null || chunks.isEmpty()) {
            return "I could not read any text from this PDF.";
        }

        // 1. Extract important keywords from the question
        Set<String> keywords = extractKeywords(question);
        if (keywords.isEmpty()) {
            return "I couldn't understand the important words in your question.";
        }

        // 2. Find the best matching chunk based on keyword frequency
        int bestIndex = -1;
        int bestScore = 0;
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            int score = scoreChunk(chunk, keywords);
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }

        if (bestIndex == -1) {
            return "Sorry, I couldn't find relevant information in the PDF for your question.";
        }

        String bestChunk = chunks.get(bestIndex);

        // 3. Choose the main keyword (first one) to extract a nice snippet
        String mainKeyword = keywords.iterator().next();
        String snippet = extractSnippet(bestChunk, mainKeyword);

        return "Here is something related I found in the PDF:\n\n" + snippet;
    }

    // ------------ helper methods ------------

    private Set<String> extractKeywords(String question) {
        String[] tokens = question.toLowerCase().split("\\W+");
        Set<String> keywords = new LinkedHashSet<>();
        for (String t : tokens) {
            if (t.length() <= 2) continue; // too short
            if (STOP_WORDS.contains(t)) continue;
            keywords.add(t);
        }
        return keywords;
    }

    private int scoreChunk(String chunk, Set<String> keywords) {
        if (chunk == null) return 0;
        String lower = chunk.toLowerCase();
        int score = 0;
        for (String k : keywords) {
            if (k.isEmpty()) continue;
            int idx = lower.indexOf(k);
            while (idx != -1) {
                score++;
                idx = lower.indexOf(k, idx + k.length());
            }
        }
        return score;
    }

    private String extractSnippet(String chunk, String mainKeyword) {
        if (chunk == null || chunk.isEmpty()) return "";

        String lower = chunk.toLowerCase();
        String key = mainKeyword.toLowerCase();

        int idx = lower.indexOf(key);
        if (idx == -1) {
            // if somehow keyword not found, just return first 400 chars
            return chunk.substring(0, Math.min(400, chunk.length()));
        }

        // take some text around the first occurrence
        int start = Math.max(0, idx - 200);
        int end = Math.min(chunk.length(), idx + 200);

        String rawSnippet = chunk.substring(start, end);

        // try to expand to sentence boundaries (., ? , !, newline)
        int sentenceStart = rawSnippet.lastIndexOf('.', idx - start - 1);
        if (sentenceStart == -1) sentenceStart = rawSnippet.lastIndexOf('\n', idx - start - 1);
        if (sentenceStart != -1 && sentenceStart + 1 < rawSnippet.length()) {
            rawSnippet = rawSnippet.substring(sentenceStart + 1);
        }

        int sentenceEnd = rawSnippet.indexOf('.', idx - start);
        if (sentenceEnd == -1) {
            sentenceEnd = rawSnippet.indexOf('\n', idx - start);
        }
        if (sentenceEnd != -1) {
            rawSnippet = rawSnippet.substring(0, sentenceEnd + 1);
        }

        return rawSnippet.trim();
    }
}
