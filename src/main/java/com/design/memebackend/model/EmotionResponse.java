package com.design.memebackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Flask API 返回的情感分析结果实体类
 */
@Data
@NoArgsConstructor
public class EmotionResponse {
    
    private String status;
    private String ocrText;
    private Map<String, EmotionCategory> predictions;
    private String message;
    
    @Data
    @NoArgsConstructor
    public static class EmotionCategory {
        private String clazz; // 使用clazz代替
        private double confidence;
        private Map<String, Double> probabilities;
    }
}
