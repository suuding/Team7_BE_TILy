package com.example.tily.image;

import com.example.tily._core.errors.exception.CustomException;
import com.example.tily._core.errors.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public void uploadImage(MultipartFile file) throws IOException {
        imageRepository.save(
                ImageData.builder()
                         .name(file.getOriginalFilename())
                         .type(file.getContentType())
                         .imageData(ImageUtils.compressImage(file.getBytes()))
                         .build());
    }

    // 이미지 파일로 압축하기
    public byte[] downloadImage(String fileName) {
        ImageData imageData = imageRepository.findByName(fileName)
                .orElseThrow(() -> new CustomException(ExceptionCode.IMAGE_NOT_FOUND));

        return ImageUtils.decompressImage(imageData.getImageData());
    }
}
