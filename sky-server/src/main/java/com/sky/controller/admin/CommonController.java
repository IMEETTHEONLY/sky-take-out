package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/admin/common")
@Api("通用接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传接口")
    public Result<String> upload(MultipartFile file)  {
        log.info("文件上传{}",file.getOriginalFilename());
        //获取文件的原始名字
        String originalFilename = file.getOriginalFilename();

        //获取文件的后缀
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        //构造文件名
        String filename= UUID.randomUUID()+extension;

        String name = null;
        try {
            name = aliOssUtil.upload(file.getBytes(), filename);
            return Result.success(name);
        } catch (IOException e) {
            log.info("文件上传失败..",e);
        }

        return Result.error("文件上传失败...");


    }
}
