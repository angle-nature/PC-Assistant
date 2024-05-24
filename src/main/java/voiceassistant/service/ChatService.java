package voiceassistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Value("${openai.api.key}")
    private String apiKey;

    // "https://key.wenwen-ai.com/v1/chat/completions";
    @Value("${api_url}")
    private String api_url;
    @Value("${model}")
    private String model;

    private final ObjectMapper objectMapper;
    private List<Map<String, String>> conversationHistory;
    private int messageCount;

    public ChatService() {
        this.objectMapper = new ObjectMapper();
        this.conversationHistory = new ArrayList<>();
        this.messageCount = 0;

        // 添加初始的 system 消息
        resetConversation();
    }

    private void resetConversation() {
        this.conversationHistory = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你现在叫小蔡同学，扮演一个基于windows11系统的PC助手，当用户输入动作时，你需要生成相应的不需要做任何修改的python脚本；当用户询问其他问题时，正常回答用户问题即可。");
        this.conversationHistory.add(systemMessage);
        this.messageCount = 0;
    }

    public String getChatResponse(String message) {
        // 每 10 轮重置对话历史
        if (messageCount >= 10) {
            resetConversation();
        }
        // 添加用户消息到对话历史
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        conversationHistory.add(userMessage);
        messageCount++;
        // 构建请求的 JSON 字符串
        String jsonInputString;
        try {
            jsonInputString = objectMapper.writeValueAsString(Map.of(
                    "model", model,
                    "messages", conversationHistory
            ));
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Unable to create request JSON.";
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api_url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode jsonResponse = objectMapper.readTree(response.body());

            // 获取并添加助手的回复到对话历史
            String reply = jsonResponse.path("choices").get(0).path("message").path("content").asText();
            Map<String, String> assistantMessage = new HashMap<>();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", reply);
            conversationHistory.add(assistantMessage);
            messageCount++;

            return reply;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "小蔡同学掉线了！";
        }
    }
}
