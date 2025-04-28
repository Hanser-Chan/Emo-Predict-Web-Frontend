package com.design.memebackend.controller;

import com.design.memebackend.model.Annotation;
import com.design.memebackend.model.EmotionResponse;
import com.design.memebackend.model.UserAnnotationRequest;
import com.design.memebackend.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图像处理控制器
 */
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // 在生产环境中应限制具体域名
public class ImageController {
    
    private final ImageService imageService;
    
    /**
     * 上传并分析图像
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmotionResponse> uploadAndAnalyze(@RequestParam("file") MultipartFile file) {
        try {
            EmotionResponse response = imageService.uploadAndAnalyze(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("处理图像时出错", e);
            EmotionResponse errorResponse = new EmotionResponse();
            errorResponse.setStatus("error");
            errorResponse.setMessage("处理图像时出错: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 保存用户标注
     */
    @PostMapping("/annotate")
    public ResponseEntity<Annotation> saveAnnotation(@RequestBody UserAnnotationRequest request) {
        try {
            Annotation updatedAnnotation = imageService.saveUserAnnotation(request);
            return ResponseEntity.ok(updatedAnnotation);
        } catch (Exception e) {
            log.error("保存标注时出错", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取所有标注
     */
    @GetMapping
    public ResponseEntity<Iterable<Annotation>> getAllAnnotations() {
        return ResponseEntity.ok(imageService.getAllAnnotations());
    }
}
