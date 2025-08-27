package com.kaola.pojo;

import lombok.Data;

@Data
public class FileVO {
    private Long id;
    private String fileName;       // 对应 file_name
    private String contributorName;// 对应关联表的 username
    private String fileType;       // 对应 file_type
}
