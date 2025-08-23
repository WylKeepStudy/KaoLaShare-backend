package com.kaola.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/24 00:11
 */


@Data
public class File {
    private Long id;
    private Long userId;
    private Long departmentId;
    private String fileName;
    private String fileUrl; // 文件的OSS地址
    private String fileType;
    private Integer downloadCount;
    private LocalDateTime createTime;
}