package com.smartimage.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartimage.dto.PredictionResult;
import com.smartimage.dto.PredictionResult.PredictionItem;
import com.smartimage.model.AnalysisRecord;
import com.smartimage.repository.AnalysisRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {

    private final RestTemplate restTemplate;
    private final AnalysisRecordRepository repository;
    private final ObjectMapper objectMapper;

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    public AnalysisService(RestTemplate restTemplate,
                           AnalysisRecordRepository repository,
                           ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * 上传图片到 AI 服务进行分类，并保存分析记录
     */
    public PredictionResult analyzeImage(MultipartFile file, int topK) throws IOException {
        // 构造 multipart 请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        body.add("top_k", String.valueOf(topK));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 调用 AI 微服务
        String url = aiServiceUrl + "/predict";
        ResponseEntity<PredictionResult> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, PredictionResult.class);

        PredictionResult result = response.getBody();

        // 保存分析记录到数据库
        if (result != null && result.getPredictions() != null && !result.getPredictions().isEmpty()) {
            AnalysisRecord record = new AnalysisRecord();
            record.setFileName(file.getOriginalFilename());
            record.setOriginalName(file.getOriginalFilename());
            record.setFileSize(file.getSize());
            record.setContentType(file.getContentType());

            PredictionItem top = result.getPredictions().get(0);
            record.setTopPrediction(String.format("%s (%.2f%%)", top.getLabel(), top.getConfidence() * 100));
            record.setPredictionsJson(objectMapper.writeValueAsString(result.getPredictions()));

            repository.save(record);
        }

        return result;
    }

    public List<AnalysisRecord> getHistory() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Map<String, Object> healthCheck() {
        try {
            ResponseEntity<Map> resp = restTemplate.getForEntity(aiServiceUrl + "/health", Map.class);
            return resp.getBody();
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}
