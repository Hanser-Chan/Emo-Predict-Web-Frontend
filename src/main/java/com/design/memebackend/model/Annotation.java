package com.design.memebackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

/**
 * 表情包标注信息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "annotations")
public class Annotation {
    
    @Id
    private String id;
    
    private String imageName;
    private String imagePath;
    private String ocrText;
    
    // 模型预测的标签
    private Map<String, String> modelPredictions;
    
    // 模型预测的各标签概率
    private Map<String, Map<String, Double>> modelProbabilities;
    
    // 用户标注的标签
    private Map<String, String> userAnnotations;
    
    @CreatedDate
    private Date createdAt;
    
    @LastModifiedDate
    private Date updatedAt;
}
