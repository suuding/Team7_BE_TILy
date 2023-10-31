package com.example.tily.image;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class AmazonS3Controller {
    final private AwsS3Service awsS3Service;

    // 업로드
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        awsS3Service.uploadImage(file);

        return ResponseEntity.ok().body(null);
    }

    // 다운로드
    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable("fileName") String fileName) {
        byte[] downloadImage = awsS3Service.downloadImage(fileName);

        return ResponseEntity.ok().contentType(MediaType.valueOf("image/png")).body(downloadImage);
    }
}
