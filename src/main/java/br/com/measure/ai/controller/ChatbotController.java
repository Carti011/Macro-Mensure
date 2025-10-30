package br.com.measure.ai.controller;

import br.com.measure.ai.service.MacroMeasureAgent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final MacroMeasureAgent agent;
    private static final Logger log = LoggerFactory.getLogger(ChatbotController.class);

    public record ChatRequest(String message) {}
    public record ChatResponse(String response) {}

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chatWithAgent(@RequestBody ChatRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(new ChatResponse("Por favor, envie uma mensagem."));
        }

        try {
            String agentResponse = agent.chat(request.message());
            return ResponseEntity.ok(new ChatResponse(agentResponse));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Erro ao processar chat:", e);

            return ResponseEntity.internalServerError().body(new ChatResponse("Desculpe, ocorreu um erro ao processar sua solicitação."));
        }
    }
}