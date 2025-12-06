package com.pdfchatbox.controller;

import com.pdfchatbox.model.ChatRequest;
import com.pdfchatbox.model.ChatResponse;
import com.pdfchatbox.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String answer = chatService.answerQuestion(request.getPdfId(), request.getQuestion());
        return ResponseEntity.ok(new ChatResponse(answer));
    }
}
