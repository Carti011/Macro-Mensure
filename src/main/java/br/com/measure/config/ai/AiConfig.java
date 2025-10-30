package br.com.measure.config.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AiConfig {

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.open-ai.chat-model.timeout:120s}")
    private Duration timeout;

    @Value("${langchain4j.open-ai.chat-model.temperature:0.0}")
    private Double temperature;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        System.out.println("--- Criando bean OpenAiChatModel manualmente ---");
        System.out.println("Model Name: " + modelName);
        System.out.println("Timeout: " + timeout);
        System.out.println("Temperature: " + temperature);

        if (apiKey == null || apiKey.isBlank() || apiKey.equals("${OPENAI_API_KEY}")) {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("ERRO: Variável de ambiente OPENAI_API_KEY não está definida!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            throw new IllegalArgumentException("Variável de ambiente OPENAI_API_KEY não definida!");
        }


        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .timeout(timeout)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}