package com.kaola.controller;

import com.aliyun.oss.model.OSSObject;
import com.kaola.exception.BusinessException;
import com.kaola.exception.FileNotFoundException;
import com.kaola.exception.UnsupportedFileTypeException;
import com.kaola.pojo.FileDownloadInfoDTO;
import com.kaola.pojo.Result;
import com.kaola.service.FileService;
import com.kaola.utils.AliyunOSSOperator;
import com.kaola.utils.PageResult;
import com.kaola.utils.UserContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/18 21:17
 */

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;


    /**
     * 上传用户头像文件
     * @param file 文件
     * @return 上传结果 url
     * @throws Exception 上传过程中可能抛出的异常
     */
    @PostMapping("/upload/avatar")
    public Result uploadAvatar(MultipartFile file) throws Exception {
        log.info("上传头像: {}",  file.getOriginalFilename());

        // --- 校验文件类型是否为图片 ---
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png") && !contentType.startsWith("image/jpg"))) {
            // 抛出自定义异常，由全局异常处理器捕获
            throw new UnsupportedFileTypeException("文件类型不支持，仅支持jpg,jpeg,png格式！");
        }
        // --- 校验结束 ---

        String url = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename(), AliyunOSSOperator.FileCategory.IMAGE);
        log.info("上传成功: {}", url);
        return Result.success(url);
    }




    /**
     * 上传学习资料文件接口
     * 路径：/file/upload
     * 接收用户上传的文件、标题、所属系ID，并保存元数据
     *
     * @param file 用户上传的文件
     * @param title 文件标题
     * @param departmentId 文件所属系ID
     * @return 包含新生成文件ID的成功响应
     */
    @PostMapping("/upload")
    public Result uploadMaterial(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("departmentId") Long departmentId) {

        log.info("开始上传文件：文件名={}, 标题={}, 所属系ID={}", file.getOriginalFilename(), title, departmentId);

        // 1. 基本参数校验
        if (file.isEmpty()) {
            return Result.error(400, "上传文件不能为空");
        }
        if (title == null || title.trim().isEmpty()) {
            return Result.error(400, "文件标题不能为空");
        }
        if (departmentId == null) {
            return Result.error(400, "请选择文件所属系");
        }

        // 从JWT Token中获取当前登录用户的ID
        Long currentUserId = UserContext.getUserId();

        try {
            // 2. 调用Service层处理文件上传和元数据保存
            Long newFileId = fileService.uploadMaterial(file, title, departmentId, currentUserId);
            log.info("文件上传成功，新文件ID: {}", newFileId);
            return Result.success(newFileId); // 返回新文件ID
        } catch (BusinessException e) {
            // 捕获Service层抛出的业务异常
            log.warn("文件上传业务处理失败: {}", e.getMessage());
            return Result.error(400, e.getMessage()); // 业务异常通常返回400
        } catch (Exception e) {
            // 捕获其他未知异常
            log.error("文件上传失败，发生未知错误: ", e);
            return Result.error(500, "文件上传失败，服务器内部错误");
        }
    }




    /**
     * 下载单个文件接口
     * 路径：/file/download/{fileId}
     * 返回 ResponseEntity<Resource>，由Spring自动管理文件流。
     *
     * @param fileId 文件ID
     * @return 文件的二进制数据流和响应头
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws Exception {
        log.info("接受下载文件ID: {}", fileId);

        FileDownloadInfoDTO fileDownloadInfo = null;
        OSSObject ossObject = null; // 在try块外部声明，以便在catch块中访问
        try {
            // 1. 调用Service层获取文件信息和OSSObject
            fileDownloadInfo = fileService.downloadFile(fileId);
            String fileName = fileDownloadInfo.getFileName();
            String fileType = fileDownloadInfo.getFileType();
            Long contentLength = fileDownloadInfo.getContentLength();
            ossObject = fileDownloadInfo.getOssObject(); // 获取OSSObject

            // 2. 设置响应头
            HttpHeaders headers = new HttpHeaders();

            // Content-Type: 根据文件类型设置，如果无法判断，则使用 application/octet-stream
            MediaType contentType = getMediaTypeForFileType(fileType);
            headers.setContentType(contentType);

            // Content-Disposition: 设置下载文件名，并进行URL编码
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

            // Content-Length: 设置文件内容长度，便于浏览器显示下载进度
            if (contentLength != null && contentLength >= 0) {
                headers.setContentLength(contentLength);
            }

            // 3. 将OSSObject的InputStream封装为InputStreamResource
            // 关键改进：创建一个 InputStream 的匿名内部类，当其 close() 方法被调用时，
            // 确保底层 OSSObject 也会被关闭。
            final OSSObject finalOssObject = ossObject; // 用于在匿名内部类中访问
            InputStream wrappedInputStream = new InputStream() {
                private final InputStream delegate = finalOssObject.getObjectContent(); // 代理实际的OSS InputStream
                private volatile boolean closed = false; // 避免重复关闭

                @Override
                public int read() throws IOException {
                    return delegate.read();
                }

                @Override
                public int read(byte[] b) throws IOException {
                    return delegate.read(b);
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    return delegate.read(b, off, len);
                }

                @Override
                public void close() throws IOException {
                    if (!closed) { // 避免重复关闭
                        try {
                            delegate.close(); // 首先关闭OSSObject的内容流
                            log.debug("OSSObject content stream for file ID {} closed.", fileId);
                        } finally {
                            if (finalOssObject != null) {
                                try {
                                    finalOssObject.close(); // 然后关闭OSSObject本身，释放底层HTTP连接
                                    log.debug("OSSObject for file ID {} closed.", fileId);
                                } catch (IOException e) {
                                    log.error("Failed to close OSSObject for file ID {}: {}", fileId, e.getMessage());
                                }
                            }
                            closed = true; // 标记已关闭
                        }
                    }
                }
            };

            InputStreamResource resource = new InputStreamResource(wrappedInputStream);

            log.info("文件ID {} 准备响应下载", fileId);
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (Exception e) {
            // 捕获所有异常，让GlobalExceptionHandler统一处理
            log.error("文件ID {} 下载失败: {}", fileId, e.getMessage(), e);
            // 在异常发生时，如果OSSObject已经被创建，也尝试关闭它，作为双重保障
            if (ossObject != null) { // 使用在try块外部声明的ossObject
                try {
                    ossObject.close();
                    log.error("在异常情况下关闭了OSSObject for file ID {}.", fileId);
                } catch (IOException ioException) {
                    log.error("在异常情况下关闭OSSObject失败: {}", ioException.getMessage());
                }
            }
            throw e; // 重新抛出异常，交给全局异常处理器
        }
    }

    /**
     * 辅助方法：根据文件类型后缀判断 MediaType
     * @param fileType 文件后缀，例如 "pdf", "docx" (不带点)
     * @return MediaType 对象
     */
    private MediaType getMediaTypeForFileType(String fileType) {
        if (fileType == null || fileType.isEmpty()) {
            return MediaType.APPLICATION_OCTET_STREAM; // 默认通用二进制流
        }
        switch (fileType.toLowerCase()) {
            case "pdf": return MediaType.APPLICATION_PDF;
            case "doc":
            case "docx": return MediaType.parseMediaType("application/msword");
            case "xls":
            case "xlsx": return MediaType.parseMediaType("application/vnd.ms-excel");
            case "ppt":
            case "pptx": return MediaType.parseMediaType("application/vnd.ms-powerpoint");
            case "zip": return MediaType.parseMediaType("application/zip");
            case "rar": return MediaType.parseMediaType("application/x-rar-compressed");
            case "txt": return MediaType.TEXT_PLAIN;
            case "jpg":
            case "jpeg": return MediaType.IMAGE_JPEG;
            case "png": return MediaType.IMAGE_PNG;
            // 添加更多MIME类型...
            default: return MediaType.APPLICATION_OCTET_STREAM; // 默认
        }
    }


    @GetMapping("/list") // 接口路径：/file/list（与前端请求一致）
    public Result getFileList(
            // @RequestParam：接收URL中的Query参数（对应Apifox的Params）
            @RequestParam(required = true) Integer pageNum,
            @RequestParam(required = true) Integer pageSize,
            @RequestParam(required = false) Long departmentId, // 可选参数
            @RequestParam(required = false) String keyword) { // 可选参数

        // 1. 简单参数校验（避免页码/条数为负数）
        if (pageNum < 1 || pageSize < 1) {
            return Result.error(400, "页码和每页条数必须大于0");
        }

        // 2. 调用Service获取分页结果
        PageResult pageResult = fileService.getFileList(pageNum, pageSize,
                departmentId, keyword);

        // 3. 返回成功响应（符合项目统一格式）
        return Result.success(pageResult);
    }


}
