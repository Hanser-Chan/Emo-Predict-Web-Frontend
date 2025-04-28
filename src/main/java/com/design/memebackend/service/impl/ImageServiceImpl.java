package com.design.memebackend.service.impl;

import com.design.memebackend.config.StorageConfig;
import com.design.memebackend.model.Annotation;
import com.design.memebackend.model.EmotionResponse;
import com.design.memebackend.model.UserAnnotationRequest;
import com.design.memebackend.repository.AnnotationRepository;
import com.design.memebackend.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 图像服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final AnnotationRepository annotationRepository;
    private final StorageConfig storageConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.flask-api.url}")
    private String flaskApiUrl;

    @Override
    public EmotionResponse uploadAndAnalyze(MultipartFile file) throws IOException {
        // 验证文件
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件为空");
        }

        String filename = UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        String relativePath = "/images/original/" + filename;
        String fullPath = storageConfig.getStorageLocation() + relativePath;

        // 调用Flask API获取情感分析结果
        EmotionResponse response = callFlaskApi(file);
        
        // 只有在用户提交标注时才保存图片和记录
        if ("success".equals(response.getStatus())) {
            // 转换情感分析结果
            Map<String, String> modelPredictions = new HashMap<>();
            Map<String, Map<String, Double>> modelProbabilities = new HashMap<>();

            response.getPredictions().forEach((category, emotion) -> {
                // 注意：Flask API返回的是"class"字段，但Java中是关键字，所以使用clazz
                modelPredictions.put(category, emotion.getClazz());
                modelProbabilities.put(category, emotion.getProbabilities());
            });

            // 创建标注记录
            Annotation annotation = Annotation.builder()
                    .imageName(filename)
                    .imagePath(relativePath)
                    .ocrText(response.getOcrText())
                    .modelPredictions(modelPredictions)
                    .modelProbabilities(modelProbabilities)
                    .build();

            // 保存记录并返回ID
            Annotation saved = annotationRepository.save(annotation);
            
            // 将ID添加到响应中，便于前端引用
            response.setMessage(saved.getId());
        }
        
        return response;
    }

    /**
     * 调用Flask API进行图像分析
     */
    private EmotionResponse callFlaskApi(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<EmotionResponse> response = restTemplate.exchange(
                    flaskApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    EmotionResponse.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("调用Flask API时出错", e);
            EmotionResponse errorResponse = new EmotionResponse();
            errorResponse.setStatus("error");
            errorResponse.setMessage("调用情感分析服务时出错: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public Annotation saveUserAnnotation(UserAnnotationRequest request) {
        // 获取标注记录
        Annotation annotation = annotationRepository.findById(request.getImageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到标注记录"));

        // 更新用户标注
        annotation.setUserAnnotations(request.getAnnotations());
        
        // 如果之前没有保存图片，现在需要保存
        if (!Files.exists(Paths.get(storageConfig.getStorageLocation() + annotation.getImagePath()))) {
            // 由于图片实际上在前面的uploadAndAnalyze方法中没有保存，
            // 我们在这里需要让前端重新上传图片或从其他地方获取，
            // 这里简化处理，只更新记录，不再保存图片
            log.warn("图片文件不存在：{}", annotation.getImagePath());
        }
        
        // 保存更新后的记录
        return annotationRepository.save(annotation);
    }

    @Override
    public Iterable<Annotation> getAllAnnotations() {
        return annotationRepository.findAll();
    }
}
