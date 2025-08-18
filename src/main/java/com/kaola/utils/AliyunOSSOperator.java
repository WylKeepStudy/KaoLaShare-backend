package com.kaola.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Component
public class AliyunOSSOperator {

    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    /**
     * 文件类型枚举，用于区分上传的文件是图片还是普通文件
     */
    public enum FileCategory {
        IMAGE,
        FILE // 对应普通文件，如PDF, DOC等
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

        String endpoint = aliyunOSSProperties.getEndpoint();
        String bucketName = aliyunOSSProperties.getBucketName();
        String region = aliyunOSSProperties.getRegion();

        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        // 根据文件类别确定存储目录
        String baseDir;
        switch (category) {
            case IMAGE:
                baseDir = "images/"; // 图片存储在 images/ 目录下
                break;
            case FILE:
                baseDir = "files/";  // 普通文件存储在 files/ 目录下
                break;
            default:
                throw new IllegalArgumentException("Unsupported file category: " + category);
        }

        // 生成一个新的不重复的文件名，保留原始文件后缀
        String fileExtension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < originalFilename.length() - 1) {
            fileExtension = originalFilename.substring(lastDotIndex); // 获取文件后缀，例如 ".png"
        }
        String newFileName = UUID.randomUUID().toString() + fileExtension; // 生成UUID作为文件名，加上原始后缀

        // 构造OSS上的完整对象路径，例如 "images/a1b2c3d4-e5f6-7890-1234-567890abcdef.png"
        String objectName = baseDir + newFileName;

        // 创建OSSClient实例。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4); // 推荐使用V4签名
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            // 上传文件
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));
        } finally {
            // 确保OSSClient在无论成功或失败后都被关闭，释放资源
            ossClient.shutdown();
        }

        // 构造并返回文件的访问URL
        // URL格式通常是 https://bucketName.endpoint/objectName
        // 注意：这里需要根据你的endpoint格式来精确构造，如果endpoint本身不带协议，可能需要手动添加
        // 假设endpoint是 "https://oss-cn-beijing.aliyuncs.com"
        return endpoint.replace("https://", "https://" + bucketName + ".") + "/" + objectName;
        // 如果endpoint是 "oss-cn-beijing.aliyuncs.com" (不带协议)，则应该是
        // return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }
}
