package com.kaola.mapper;

import com.kaola.pojo.File;
import com.kaola.pojo.FileVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

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


    /**
     * 分页查询文件列表
     * @param startIndex 分页起始位置（pageNum-1)*pageSize
     * @param pageSize 每页条数
     * @param departmentId 系ID（筛选条件，可为null）
     * @param keyword 搜索关键词（模糊匹配文件名，可为null）
     * @return 当前页的文件列表
     */
    @Select("SELECT t_file.id,file_name,username AS contributorName,file_type FROM t_file,t_user " +
            "WHERE (t_user.id=t_file.user_id)AND(department_id = #{departmentId} OR #{departmentId} IS NULL) " +
            "AND (file_name LIKE CONCAT('%', #{keyword}, '%') OR #{keyword} IS NULL) " +
            "ORDER BY t_file.create_time DESC " +
            "LIMIT #{startIndex}, #{pageSize}")
    List<FileVO> selectFileList(Integer startIndex, Integer pageSize,
                                Long departmentId, String keyword);


    /**
     * 查询符合条件的文件总条数（用于分页）
     */
    @Select("SELECT COUNT(*) FROM t_file " +
            "WHERE (department_id = #{departmentId} OR #{departmentId} IS NULL) " +
            "AND (file_name LIKE CONCAT('%', #{keyword}, '%') OR #{keyword} IS NULL)")
    Long selectFileTotal(Long departmentId, String keyword);
}