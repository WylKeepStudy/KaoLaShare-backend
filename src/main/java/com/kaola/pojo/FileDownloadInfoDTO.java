package com.kaola.pojo;

import com.aliyun.oss.model.OSSObject; // 导入 OSSObject
import lombok.AllArgsConstructor; // 导入 AllArgsConstructor
import lombok.Data;
import lombok.NoArgsConstructor; // 导入 NoArgsConstructor

/**
 * 文件下载信息 DTO
 * 封装文件下载所需的各种信息，包括文件名、文件类型和OSSObject（包含文件流）。
 * 调用方需要负责关闭OSSObject。
 */
@Data
@NoArgsConstructor // Lombok注解，生成无参构造函数
@AllArgsConstructor // Lombok注解，生成全参构造函数
public class FileDownloadInfoDTO {
    private String fileName;    // 文件名，例如 "文档.pdf"
    private String fileType;    // 文件类型，例如 "pdf"
    private Long contentLength; // 文件内容长度，用于设置Content-Length响应头
    private OSSObject ossObject; // OSSObject，包含文件输入流。**调用方必须负责关闭此对象！**
}
