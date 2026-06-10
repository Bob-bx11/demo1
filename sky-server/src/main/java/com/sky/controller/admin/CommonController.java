package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    // 读取配置文件里的本地路径
    @Value("${file.upload.local-path}")
    private String localPath;

    /**
     * 文件上传（本地存储，存入 images 文件夹）
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file.getOriginalFilename());

        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();

            // 截取文件后缀  .jpg .png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 使用UUID生成新文件名，防止重复覆盖
            String fileName = UUID.randomUUID().toString() + extension;

            // 目标目录（配置文件已经指定是 images 文件夹）
            File folder = new File(localPath);
            if (!folder.exists()) {
                folder.mkdirs(); // 不存在就自动创建
            }

            // 最终文件路径
            File targetFile = new File(folder, fileName);

            // 将上传的文件保存到目标位置
            file.transferTo(targetFile);

            // 返回可访问的图片地址（前端直接用这个回显）
            return Result.success("/images/" + fileName);
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}