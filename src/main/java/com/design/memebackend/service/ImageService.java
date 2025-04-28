package com.design.memebackend.service;

import com.design.memebackend.model.Annotation;
import com.design.memebackend.model.EmotionResponse;
import com.design.memebackend.model.UserAnnotationRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 图像服务接口
 */
public interface ImageService {
    
    /**
     * 上传图像并分析
     * 
     * @param file 上传的图像文件
     * @return 情感分析结果
     * @throws IOException 如果文件处理出错
     */
    EmotionResponse uploadAndAnalyze(MultipartFile file) throws IOException;
    
    /**
     * 保存用户标注
     *
     * @param request 用户标注请求
     * @return 更新后的标注信息
     */
    Annotation saveUserAnnotation(UserAnnotationRequest request);
    
    /**
     * 获取所有用户标注
     * 
     * @return 所有标注信息列表
     */
    Iterable<Annotation> getAllAnnotations();
}
