package com.design.memebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB配置类，启用审计功能自动填充创建时间和更新时间
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
