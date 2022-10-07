package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author 黄梁峰
 * <p>
 * 文件管理
 */
@RestController
@RequestMapping(value = "/admin/product")
public class FileController {

    @Value("${fileServer.url}")
    private String imageUrl;

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fileUpload")
    public Result fileUpload(@RequestParam MultipartFile file) throws Exception {
        return Result.ok(imageUrl + FileUtils.upload(file));
    }

    @GetMapping(value = "/fileDownload")
    public Result fileDownload(String group_name, String remote_filename)throws Exception{
        byte[] bytes = FileUtils.download(group_name, remote_filename);
        FileOutputStream inputStream = new FileOutputStream(new File("G:\\BaiduNetdiskDownload\\1.jpg"));
        inputStream.write(bytes,0,bytes.length);
        inputStream.close();
        return Result.ok();
    }
}
