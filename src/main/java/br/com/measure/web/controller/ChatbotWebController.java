package br.com.measure.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chatbot")
public class ChatbotWebController {

    @GetMapping
    public String showChatbotPage() {
        return "chatbot";
    }

    @ModelAttribute("activePage")
    public String getActivePage() {
        return "chatbot";
    }
}