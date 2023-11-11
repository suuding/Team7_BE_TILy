package com.example.tily._core.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import com.amazonaws.util.IOUtils;
import com.example.tily._core.errors.CustomException;
import com.example.tily._core.errors.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service implements FileService {
    private final S3Component s3Component;
    private final AmazonS3 amazonS3;

    @Override
    public String uploadFile(MultipartFile file, FileFolder fileFolder) {

        //파일 이름 생성
        String fileName = getFileFolder(fileFolder) + createFileName(file.getOriginalFilename());

        //파일 변환
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        //파일 업로드
        try(InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(
                    new PutObjectRequest(s3Component.getBucket(), fileName, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicReadWrite)
            );
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.FILE_UPLOAD_FAIL);
        }

        return fileName;
    }

    @Override
    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(s3Component.getBucket(), fileName));
    }

    @Override
    public String getFileUrl(String fileName) {
        return amazonS3.getUrl(s3Component.getBucket(), fileName).toString();
    }

    @Override
    public byte[] downloadFile(String fileName) {

        //파일 유무 확인
        validateFileExists(fileName);


        S3Object s3Object = amazonS3.getObject(s3Component.getBucket(), fileName);
        S3ObjectInputStream s3ObjectContent = s3Object.getObjectContent();

        try {
            return IOUtils.toByteArray(s3ObjectContent);
        }catch (IOException e ){
            throw new CustomException(ExceptionCode.IMAGE_DOWNLOAD_FAIL);
        }
    }

    @Override
    public String getFileFolder(FileFolder fileFolder) {
        return fileFolder.getFolder(s3Component);
    }

    private void validateFileExists(String fileName) {
        if(!amazonS3.doesObjectExist(s3Component.getBucket(), fileName))
            throw new CustomException(ExceptionCode.IMAGE_NOT_FOUND);
    }

    //파일 이름 생성 로직
    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    //파일의 확장자명을 가져오는 로직
    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        }catch(StringIndexOutOfBoundsException e) {
            throw new CustomException(ExceptionCode.INVALID_FILE_FORMAT);
        }
    }

    //이미지 URL->파일 이름 변환
    public static String convertToFileName(String imageUrl){
        String[] path = imageUrl.split("/");
        String folderName = path[path.length-2];
        String fileName = path[path.length-1];
        return folderName + "/" + fileName;
    }
}
