package com.design.memebackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 用户提交的标注请求
 */
@Data
@NoArgsConstructor
public class UserAnnotationRequest {
    private String imageId;
    private Map<String, String> annotations;
}
