package com.pinyougou.shop.controller;

import com.pinyougou.utlis.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_URL;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String ext_name = originalFilename.substring(originalFilename.lastIndexOf("."));

        try {
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.properties");
            String file_name = client.uploadFile(file.getBytes(), ext_name);
            String path = FILE_URL+file_name;
            return new Result(true,path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
