package com.kaola.utils;

import com.kaola.pojo.File;
import lombok.Data;

import java.util.List;

@Data
public class PageResult {

    private Long total;
    // 当前页的数据列表（如当前页的10个文件）
    private List<?> records;
    // 页码（前端传入的pageNum）
    private Integer pageNum;

    private Integer pageSize;

}
