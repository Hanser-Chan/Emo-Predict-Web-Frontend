package com.design.memebackend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * 文件存储配置类
 */
@Configuration
public class StorageConfig {

    @Value("${app.storage.location}")
    private String storageLocation;

    /**
     * 初始化存储目录
     */
    @PostConstruct
    public void init() {
        // 创建主存储目录
        File storageDir = new File(storageLocation);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        
        // 创建图片存储目录
        File imageDir = new File(storageLocation + "/images/original");
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
    }
    
    public String getStorageLocation() {
        return storageLocation;
    }
}
