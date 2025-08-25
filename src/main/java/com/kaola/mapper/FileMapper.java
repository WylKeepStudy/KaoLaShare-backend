package com.kaola.mapper;

import com.kaola.pojo.File;
import org.apache.ibatis.annotations.*;

@Mapper
public interface FileMapper {

    // 根据文件id查找文件全部信息（用于下载文件）
    @Select("SELECT id, user_id, department_id, file_name, file_url, file_type, download_count, create_time " +
            "FROM t_file WHERE id = #{id}")
    File findById(@Param("id") Long id);


    /**
     * 更新文件下载次数
     * @param id 文件ID
     * @return 影响行数
     */
    @Update("UPDATE t_file SET download_count = download_count + 1 WHERE id = #{id}")
    int incrementDownloadCount(@Param("id") Long id);


    /**
     * 插入新文件记录到t_file表（上传文件）
     * @param file 文件实体
     * @return 影响行数
     */
    @Insert("INSERT INTO t_file (user_id, department_id, file_name, file_url, file_type, download_count, create_time) " +
            "VALUES (#{userId}, #{departmentId}, #{fileName}, #{fileUrl}, #{fileType}, #{downloadCount}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 关键：获取自增ID并设置到File对象的id属性
    int insertFile(File file);
}