package com.example.tily._core.S3;

import com.example.tily._core.S3.FileFolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

// S3이외 다른 서비스를 염두해두고 인터페이스 사용
public interface FileService {
    //파일 업로드
    String uploadFile(MultipartFile file, FileFolder fileFolder);

    //파일 삭제
    void deleteFile(String fileName);

    //파일 URL 조회
    String getFileUrl(String fileName);

    //파일 다운로드
    byte[] downloadFile(String fileName) throws FileNotFoundException;

    //폴더 조회
    String getFileFolder(FileFolder fileFolder);
}
