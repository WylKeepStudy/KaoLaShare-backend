package com.kaola.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.OSSObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * AliyunOSSOperator 工具类
 * 管理 OSSClient 的生命周期，提供文件上传和下载功能。
 */
@Component
public class AliyunOSSOperator {

    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    private OSS ossClient; // OSSClient 实例，作为单例


    /**
     * 文件类型枚举，用于区分上传的文件是图片还是普通文件
     */
    public enum FileCategory {
        IMAGE,
        FILE // 对应普通文件，如PDF, DOC等
    }



    /**
     * Spring 容器初始化后，执行此方法初始化 OSSClient
     */
    @PostConstruct
    public void init() {
        String endpoint = aliyunOSSProperties.getEndpoint();
        String region = aliyunOSSProperties.getRegion();

        // 从环境变量中获取访问凭证。
        // 运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = null;
        try {
            credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        } catch (Exception e) {
            // 如果环境变量未设置，这里会抛异常，实际项目中需要更完善的凭证管理
            System.err.println("OSS访问凭证环境变量未设置: " + e.getMessage());
            // 可以选择抛出自定义异常或使用其他凭证方式
        }


        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4); // 推荐使用V4签名

        this.ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider) // 使用注入的凭证提供者
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();
    }

    /**
     * Spring 容器销毁前，执行此方法关闭 OSSClient
     * 释放连接资源，防止内存泄露
     */
    @PreDestroy
    public void destroy() {
        if (this.ossClient != null) {
            this.ossClient.shutdown();
        }
    }



    /**
     * 上传文件到阿里云OSS
     *
     * @param content 文件内容的字节数组
     * @param originalFilename 原始文件名（包含后缀），例如 "my_picture.png" 或 "document.pdf"
     * @param category 文件类别，可以是 FileCategory.IMAGE 或 FileCategory.FILE
     * @return 文件在OSS上的完整URL
     * @throws Exception 如果上传过程中发生错误
     */
    public String upload(byte[] content, String originalFilename, FileCategory category) throws Exception {
        String bucketName = aliyunOSSProperties.getBucketName();
        String endpoint = aliyunOSSProperties.getEndpoint();

        String baseDir;
        switch (category) {
            case IMAGE:
                baseDir = "images/";
                break;
            case FILE:
                baseDir = "files/";
                break;
            default:
                throw new IllegalArgumentException("Unsupported file category: " + category);
        }

        String fileExtension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < originalFilename.length() - 1) {
            fileExtension = originalFilename.substring(lastDotIndex);
        }
        String newFileName = UUID.randomUUID().toString() + fileExtension;
        String objectName = baseDir + newFileName;

        this.ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));

        // 构造并返回文件的访问URL
        return endpoint.replace("https://", "https://" + bucketName + ".") + "/" + objectName;
    }





    /**
     * 从阿里云OSS下载文件
     *
     * @param ossFileUrl 文件在OSS上的完整URL，例如 "https://bucketName.endpoint/files/some-uuid.pdf"
     * @return OSSObject，包含文件的InputStream和元数据。调用方需负责关闭OSSObject。
     * @throws Exception 如果下载过程中发生错误或文件不存在
     */
    public OSSObject download(String ossFileUrl) throws Exception {
        String bucketName = aliyunOSSProperties.getBucketName();
        String endpoint = aliyunOSSProperties.getEndpoint();

        // 从完整的OSS URL中解析出Object Name
        // 例如：https://java-wyl-ai.oss-cn-beijing.aliyuncs.com/files/some-uuid.pdf
        // objectName 就是 files/some-uuid.pdf
        String objectName = ossFileUrl.substring(ossFileUrl.indexOf(bucketName + ".") + bucketName.length() + 1 + endpoint.split("//")[1].length());

        // 确保objectName不以斜杠开头，除非它就是根目录下的文件
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }

        return this.ossClient.getObject(bucketName, objectName);
    }


}
