package com.nexters.teambuilder.s3uploader.api;

import java.util.List;

import com.nexters.teambuilder.s3uploader.service.AmazonS3Service;
//import com.nexters.teambuilder.s3uploader.service.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/files")
public class FileController {
    @Autowired
    private AmazonS3Service amazonS3Service;

//    @Autowired
//    private S3Uploader s3Uploader;

    @PostMapping(value = "/upload", params = {"targetPath!=", "filename!="})
    public List<String> uploadFile(@RequestParam List<MultipartFile> images,
                                   @RequestParam String targetPath,
                                   @RequestParam String filename) {
        return amazonS3Service.uploadImages(targetPath, filename, images);
    }

//    @PostMapping(value = "/upload-file")
//    public String upload(@RequestParam MultipartFile file,
//                         @RequestParam String dirName) throws Exception {
//        return s3Uploader.upload(file, dirName);
//    }
}
