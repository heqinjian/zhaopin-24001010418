package com.recruitment.controller;

import com.recruitment.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "文件管理", description = "文件上传相关接口")
@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Operation(summary = "上传图片", description = "上传 jpg、jpeg、png、gif 格式的图片，返回访问地址")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> upload(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        // 取原始文件名后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        // 后缀白名单校验
        if (!suffix.equals(".jpg") && !suffix.equals(".jpeg")
                && !suffix.equals(".png") && !suffix.equals(".gif")) {
            return Result.error("仅支持 jpg、jpeg、png、gif 格式的图片");
        }

        // 目录不存在则创建
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // UUID 去横线命名，保留原后缀
        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;
        File dest = new File(uploadPath + filename);
        try {
            file.transferTo(dest);
        } catch (Exception e) {
            return Result.error("文件上传失败");
        }

        Map<String, String> data = new HashMap<>();
        data.put("url", "/uploads/" + filename);
        data.put("filename", filename);
        return Result.success("上传成功", data);
    }
}
