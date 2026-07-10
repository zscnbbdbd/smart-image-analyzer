package com.smartimage.controller;

import com.smartimage.dto.PredictionResult;
import com.smartimage.model.AnalysisRecord;
import com.smartimage.service.AnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class ImageController {

    private final AnalysisService analysisService;

    public ImageController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/history")
    public String history(Model model) {
        List<AnalysisRecord> records = analysisService.getHistory();
        model.addAttribute("records", records);
        return "history";
    }

    @PostMapping("/upload")
    @ResponseBody
    public PredictionResult upload(@RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "top_k", defaultValue = "5") int topK) throws IOException {
        return analysisService.analyzeImage(file, topK);
    }

    @GetMapping("/api/health")
    @ResponseBody
    public Map<String, Object> health() {
        return analysisService.healthCheck();
    }

    @GetMapping("/api/history")
    @ResponseBody
    public List<AnalysisRecord> apiHistory() {
        return analysisService.getHistory();
    }
}
