package com.main.chatai.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.chatai.repository.ChatHistoryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;



@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {

    private final RestTemplate restTemplate;
    private final ChatHistoryRepository chatHistoryRepository;
    private final ObjectMapper objectMapper; // 添加JSON解析器

    private static final String FASTAPI_URL = "http://localhost:8001/generate";

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class FastApiResponse {
        private String generated_text;
    }

    @PostMapping(value = "/chat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> chat(
            @RequestParam("prompt") String prompt,
            @RequestParam("chatId") String chatId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        chatHistoryRepository.save("chat", chatId);

        try {
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body("需要上传图片文件");
            }
            String result = multiModalChat(prompt, files.get(0));
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            log.error("API请求失败", e);
            return ResponseEntity.internalServerError().body("服务处理错误");
        }
    }

    private String multiModalChat(String prompt, MultipartFile file) throws IOException {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("text", prompt);
        params.add("image", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        String apiResponse = sendRequest(params);
        return extractResponseText(apiResponse);
    }

    private String sendRequest(MultiValueMap<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(params, headers);

        return restTemplate.postForObject(FASTAPI_URL, request, String.class);
    }

    private String extractResponseText(String jsonResponse) {
        try {
            FastApiResponse response = objectMapper.readValue(jsonResponse, FastApiResponse.class);
            if (response.getGenerated_text() != null) {
                // 拆分响应文本
                String[] lines = response.getGenerated_text().split("\n");
                if (lines.length >= 3) {
                    // 构建结果
                    StringBuilder result = new StringBuilder();
                    boolean foundDiagnosis = false;

                    for (String line : lines) {
                        // 检查是否是“可能疾病诊断”部分
                        if (line.contains("【可能疾病诊断】")) {
                            foundDiagnosis = true;
                            result.append(line).append("\n"); // 添加标题行
                        } else if (foundDiagnosis) {
                            // 对“可能疾病诊断”部分的每一行进行处理
                            if (line.matches("\\d+\\. .*")) { // 匹配“序号. 内容”格式
                                result.append(line.substring(line.indexOf('.') + 1)).append("\n");
                            } else {
                                result.append(line).append("\n");
                            }
                        } else {
                            result.append(line).append("\n");
                        }
                    }

                    return result.toString().trim();
                } else {
                    return response.getGenerated_text();
                }
            }
        } catch (IOException e) {
            log.error("解析API响应失败", e);
        }
        return "未能获取有效回答";
    }
}