package com.kaola.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/7/26 19:45
 */

@Component
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOSSProperties {

    private String endpoint;
    private String bucketName;
    private String region;

}
