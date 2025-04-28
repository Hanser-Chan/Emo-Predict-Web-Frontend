package com.design.memebackend.repository;

import com.design.memebackend.model.Annotation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 表情包标注信息数据访问接口
 */
@Repository
public interface AnnotationRepository extends MongoRepository<Annotation, String> {
    // 可以添加自定义查询方法
}
