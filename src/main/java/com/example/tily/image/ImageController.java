package com.example.tily.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {
    final private ImageService imageService;

    // 업로드
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        imageService.uploadImage(file);

        return ResponseEntity.ok().body(null);
    }

    // 다운로드
    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable("fileName") String fileName) {
        byte[] downloadImage = imageService.downloadImage(fileName);

        return ResponseEntity.ok().contentType(MediaType.valueOf("image/png")).body(downloadImage);
    }
}
